package com.klead.es.river.impl;

import com.klead.es.river.*;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.reading.SqlDataCounter;
import com.klead.es.river.data.reading.SqlDataReader;
import com.klead.es.river.exception.PreconditionsValidationException;
import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by kafi on 03/02/2016.
 */


public class MysqlRiver implements IRiver {
    private static final Logger LOGGER = Logger.getLogger(MysqlRiver.class);
    private ThreadPoolTaskExecutor taskExecutor;
    private WorkerFactory workerFactory;
    private PreconditionHandler preconditionHandler;
    private SqlDataCounter sqlDataCounter;
    private SqlDataReader sqlDataReader;
    private WorkReport workReport;

    public IndexationResult index(IndexationCommand command) {
        long start = System.currentTimeMillis();
        IndexationResult indexationResult = new IndexationResult();
        // Check preconditions before performing indexation
        try {
            preconditionHandler.verifyPreconditions(command, indexationResult);
        } catch (PreconditionsValidationException e) {
            return indexationResult;
        }
        // Run indexing Workers
        runWorkers(command, indexationResult);
        long end = System.currentTimeMillis();
        indexationResult.setTotalExecutionTime(end - start);
        return indexationResult;
    }

    private void runWorkers(IndexationCommand command, IndexationResult indexationResult) {
        try {
            workReport.setReport(new TreeMap<Long, WorkerReport>());
            Integer bulkBlockSize = Integer.valueOf(command.getBulkBlockSize());
            int fromIndex = 0;
            int numberOfDocs = sqlDataCounter.countNumberOfDocs();
            int numberOfWorkers = (numberOfDocs / bulkBlockSize.intValue()) + 1;
            LOGGER.info("NUMBER OF WORKERS : " + numberOfWorkers);
            indexationResult.setWorkersNumber(numberOfWorkers);
            indexationResult.setTotalDocsCount(Long.valueOf(numberOfDocs));
            List<Document> data = sqlDataReader.readData( command);

            for (int i = 0; i < numberOfWorkers; i++) {
                int toIndex = fromIndex + bulkBlockSize > numberOfDocs ? numberOfDocs : fromIndex + bulkBlockSize ;
                taskExecutor.execute(workerFactory.getWorker(i, data.subList(fromIndex, toIndex), command));
                fromIndex = toIndex;
            }
            indexationResult.setWorkReport(workReport);
            indexationResult.setResultCode(ResultCode.INDEXATION_SUCCESS.name());
            LOGGER.info("INDEXATION STATUS : SUCCESS");
        } catch (Exception e) {
            LOGGER.error("INDEXATION STATUS : FAILED", e);
        }
    }

    public void setTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setWorkerFactory(WorkerFactory workerFactory) {
        this.workerFactory = workerFactory;
    }

    public void setPreconditionHandler(PreconditionHandler preconditionHandler) {
        this.preconditionHandler = preconditionHandler;
    }

    public void setSqlDataCounter(SqlDataCounter sqlDataCounter) {
        this.sqlDataCounter = sqlDataCounter;
    }

    public void setWorkReport(WorkReport workReport) {
        this.workReport = workReport;
    }

    public void setSqlDataReader(SqlDataReader sqlDataReader) {
        this.sqlDataReader = sqlDataReader;
    }
}
