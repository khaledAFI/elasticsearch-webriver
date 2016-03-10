package com.klead.es.river.worker;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.WorkerReport;
import com.klead.es.river.builder.WorkerReportBuilder;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.Status;
import com.klead.es.river.data.indexing.IDocumentIndexer;
import com.klead.es.river.data.mapping.IDocumentMapper;
import com.klead.es.river.data.marking.IDataMarker;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by kafi on 09/02/2016.
 */
@Component
@Scope(value = "prototype")
public class MysqlWorker implements Callable<WorkerReport> {
    private static final Logger LOGGER = Logger.getLogger(MysqlWorker.class);
    private Long id;

    @Autowired
    private IDocumentMapper documentToJsonMapper;
    @Autowired
    private IDocumentIndexer documentBulkIndexer;
    @Autowired
    private IDataMarker sqlDataMarker;
    @Autowired
    private DataSourceTransactionManager txManager;

    private List<Document> documents;
    private IndexationCommand command;

    public WorkerReport call() {
        long start = System.currentTimeMillis();
        WorkerReport workerReport = null;
        TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
        try {
            List<XContentBuilder> jsonList = documentToJsonMapper.convertDocumentsToJson(documents);
            documentBulkIndexer.index(jsonList, command);
            sqlDataMarker.markData(documents);

            long ends = System.currentTimeMillis();
            workerReport = buildWorkerReport(Status.SUCCESS.name(), ends - start);
            txManager.commit(status);

        } catch (Throwable th) {
            long ends = System.currentTimeMillis();
            workerReport = buildWorkerReport(Status.FAILURE.name(), ends - start);
            txManager.rollback(status);
        }
        return workerReport;
    }

    private WorkerReport buildWorkerReport(final String status, final long executionTime) {
        WorkerReport workerReport = null;
        try {
            WorkerReportBuilder builder = new WorkerReportBuilder();
            workerReport = builder.id(id)
                    .docsCount(Long.valueOf(documents.size()))
                    .status(status)
                    .workerExecutionTime(executionTime).build();
            LOGGER.info("WORKER[" + id + "] " + status);
        } catch (Exception e) {
            LOGGER.error("worker report build error");
        }
        return workerReport;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void setCommand(IndexationCommand command) {
        this.command = command;
    }

}

