package com.klead.es.river.impl;

import com.klead.es.common.AppContext;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.WorkReport;
import com.klead.es.river.WorkerReport;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.indexing.DocumentBulkIndexer;
import com.klead.es.river.data.mapping.DocumentToJsonMapper;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
public class MysqlWorker implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(MysqlWorker.class);
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";
    private long id;
    private DocumentToJsonMapper documentToJsonMapper;
    private DocumentBulkIndexer documentBulkIndexer;
    private List<Document> documents;
    private IndexationCommand command;

    public void run() {
        long start = System.currentTimeMillis();
        WorkReport workReport = (WorkReport) AppContext.getApplicationContext().getBean("workReport");
        Long bulkBlockSize = Long.valueOf(command.getBulkBlockSize());
        try {
            List<XContentBuilder> jsonList = documentToJsonMapper.convertDocumentsToJson(documents);
            documentBulkIndexer.index(jsonList, command);
            long ends = System.currentTimeMillis();
            addWorkerReport(id, documents.size(), SUCCESS, ends - start, workReport);
        } catch (Exception e) {
            long ends = System.currentTimeMillis();
            addWorkerReport(id, bulkBlockSize.intValue(), FAILURE, ends - start, workReport);
        }
    }

    private void addWorkerReport(long id, int docCount, String status, long executionTime, WorkReport workReport) {
        WorkerReport workerReport = new WorkerReport();
        workerReport.setDocsCount(Long.valueOf(docCount));
        workerReport.setId(id);
        workerReport.setStatus(status);
        workerReport.setWorkerExecutionTime(executionTime);
        workReport.getReport().put(id, workerReport);
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void setDocumentToJsonMapper(DocumentToJsonMapper documentToJsonMapper) {
        this.documentToJsonMapper = documentToJsonMapper;
    }

    public void setDocumentBulkIndexer(DocumentBulkIndexer documentBulkIndexer) {
        this.documentBulkIndexer = documentBulkIndexer;
    }

    public void setCommand(IndexationCommand command) {
        this.command = command;
    }


}

