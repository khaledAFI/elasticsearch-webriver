package com.klead.es.river.handler.impl;

import com.klead.es.common.EsAdminService;
import com.klead.es.common.RiverMailer;
import com.klead.es.river.*;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.Status;
import com.klead.es.river.factory.TaskExecutorFactory;
import com.klead.es.river.factory.WorkerFactory;
import com.klead.es.river.handler.IWorkersHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by kafi on 17/02/2016.
 */
@Component
public class WorkersHandler  implements IWorkersHandler {
    private static final Logger LOGGER = Logger.getLogger(WorkersHandler.class);
    private static final long TERMINATION_AWAIT_TIMEOUT = 5;

    @Value("${additionalPoolSize}")
    private String additionalPoolSize;
    @Value("${queueCapacity}")
    private String queueCapacity;

    @Autowired
    private WorkerFactory workerFactory;
    @Autowired
    private TaskExecutorFactory taskExecutorFactory;
    @Autowired
    private EsAdminService esAdminService;
    @Autowired
    private RiverMailer riverMailer;

    @Override
    public IndexationResult runWorkers(final IndexationCommand command, final List<Document> data) {
        long start = System.currentTimeMillis();
        IndexationResult indexationResult = new IndexationResult();
        try {
            Integer bulkBlockSize = Integer.valueOf(command.getIndexationPacketSize());
            int fromIndex = 0;
            int numberOfDocs = data.size();
            int numberOfWorkers = (numberOfDocs / bulkBlockSize.intValue()) + 1;
            LOGGER.info("NUMBER OF WORKERS : " + numberOfWorkers);
            indexationResult.setWorkersNumber(numberOfWorkers);
            indexationResult.setTotalDocsCount(Long.valueOf(numberOfDocs));

            // Prepare a new taskExecutor thread pool
            ThreadPoolTaskExecutor taskExecutor = taskExecutorFactory.getTaskExecutor(numberOfWorkers, numberOfWorkers + Integer.valueOf(additionalPoolSize), Integer.valueOf(queueCapacity));
            //create a list to hold the Future object associated with Callable
            List<Future<WorkerReport>> list = new ArrayList();
            // process blocks of data with dedicated worker
            for (int i = 0; i < numberOfWorkers; i++) {
                int toIndex = fromIndex + bulkBlockSize > numberOfDocs ? numberOfDocs : fromIndex + bulkBlockSize;
                Future<WorkerReport> future = taskExecutor.submit(workerFactory.getWorker(i, data.subList(fromIndex, toIndex), command));
                list.add(future);
                fromIndex = toIndex;
            }
            // build work report
            buildWorkReport(list, command, indexationResult, start);

            // shutdown task executor after workers termination
            taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            taskExecutor.shutdown();
            taskExecutor.getThreadPoolExecutor().awaitTermination(TERMINATION_AWAIT_TIMEOUT, TimeUnit.SECONDS);

            indexationResult.setResultCode(ResultCode.INDEXATION_SUCCESS.name());
            LOGGER.debug("INDEXATION STATUS : SUCCESS");
            riverMailer.sendPreConfiguredSuccessMail("SUCCESS");
        } catch (Exception e) {
            LOGGER.error("INDEXATION STATUS : FAILED", e);
            riverMailer.sendPreConfiguredFailMail("FAILURE");
        } finally {
            LOGGER.debug("End  indexation process.");
        }
        return indexationResult;
    }

    private void buildWorkReport(List<Future<WorkerReport>> list,final IndexationCommand command, IndexationResult indexationResult,final long start) {
        WorkReport  workReport = new WorkReport();
        List<WorkerReport> workerReports = new ArrayList<>();
        for (Future<WorkerReport> fut : list) {
            try {
                workerReports.add(fut.get());
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.error("ERROR : getting worker report", e);
            }
        }
        Long totalFailureDocsCount = 0L;
        Long totalSuccessDocsCount = 0L;
        for (WorkerReport report : workerReports) {
            if (report != null) {
                if (Status.SUCCESS.name().equals(report.getStatus())) {
                    totalSuccessDocsCount += report.getDocsCount();
                } else if (Status.FAILURE.name().equals(report.getStatus())) {
                    totalFailureDocsCount += report.getDocsCount();
                }
                workReport.getReport().put(report.getId(), report);
            }
        }

        indexationResult.setTotalSuccessDocsCount(totalSuccessDocsCount);
        indexationResult.setTotalFailureDocsCount(totalFailureDocsCount);
        indexationResult.setTotalExecutionTime(System.currentTimeMillis() - start);
        try {
            indexationResult.setEsReport(esAdminService.indexStatistics(command.getIndexName()));
        } catch (Exception e) {
            LOGGER.error("Error getting Es statistics.", e);
        }
        indexationResult.setWorkReport(workReport);
    }
}

