package com.klead.es.river.handler.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.ResultCode;
import com.klead.es.river.WorkReport;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.reading.SqlDataCounter;
import com.klead.es.river.data.reading.SqlDataReader;
import com.klead.es.river.factory.TaskExecutorFactory;
import com.klead.es.river.factory.WorkerFactory;
import com.klead.es.river.handler.IWorkersHandler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kafi on 17/02/2016.
 */
@Component
public class WorkersHandler  implements IWorkersHandler {
    private static final Logger LOGGER = Logger.getLogger(WorkersHandler.class);
    private static final long TERMINATION_AWAIT_TIMEOUT = 5;

    @Value("${additionalPoolSize}")
    private String additionalPoolSize ;
    @Value("${queueCapacity}")
    private String queueCapacity ;
    @Autowired
    private WorkReport workReport;
    @Autowired
    private SqlDataCounter sqlDataCounter;
    @Autowired
    private SqlDataReader sqlDataReader;
    @Autowired
    private WorkerFactory workerFactory;
    @Autowired
    private TaskExecutorFactory taskExecutorFactory;

    @Override
    public IndexationResult runWorkers(IndexationCommand command) {
        IndexationResult indexationResult = new IndexationResult();
        try {
            // init the report
            workReport.init();
            Integer bulkBlockSize = Integer.valueOf(command.getIndexationPacketSize());
            int fromIndex = 0;
            int numberOfDocs = sqlDataCounter.countNumberOfDocs();
            int numberOfWorkers = (numberOfDocs / bulkBlockSize.intValue()) + 1;
            LOGGER.info("NUMBER OF WORKERS : " + numberOfWorkers);
            indexationResult.setWorkersNumber(numberOfWorkers);
            indexationResult.setTotalDocsCount(Long.valueOf(numberOfDocs));

            // get data to index
            List<Document> data = sqlDataReader.readData(command);

            // Prepare a new taskExecutor thread pool
            ThreadPoolTaskExecutor taskExecutor = taskExecutorFactory.getTaskExecutor(numberOfWorkers, numberOfWorkers + Integer.valueOf(additionalPoolSize), Integer.valueOf(queueCapacity));

            // process blocks of data with dedicated worker
            for (int i = 0; i < numberOfWorkers; i++) {
                int toIndex = fromIndex + bulkBlockSize > numberOfDocs ? numberOfDocs : fromIndex + bulkBlockSize;
                taskExecutor.execute(workerFactory.getWorker(i, data.subList(fromIndex, toIndex), command));
                fromIndex = toIndex;
            }

            // shutdown task executor after workers termination
            taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
            taskExecutor.shutdown();
            taskExecutor.getThreadPoolExecutor().awaitTermination(TERMINATION_AWAIT_TIMEOUT, TimeUnit.SECONDS);

            indexationResult.setResultCode(ResultCode.INDEXATION_SUCCESS.name());
            LOGGER.info("INDEXATION STATUS : SUCCESS");
        } catch (Exception e) {
            LOGGER.error("INDEXATION STATUS : FAILED", e);
        }
        return  indexationResult;
    }
}
