package com.klead.es.river.builder;

import com.klead.es.river.WorkerReport;

/**
 * Created by kafi on 10/03/2016.
 */
public class WorkerReportBuilder {

    private Long id;
    private Long docsCount;
    private String status;
    private Long workerExecutionTime;

    public WorkerReportBuilder() {

    }

    public WorkerReportBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public WorkerReportBuilder docsCount(Long docsCount) {
        this.docsCount = docsCount;
        return this;
    }

    public WorkerReportBuilder status(String status) {
        this.status = status;
        return this;
    }

    public WorkerReportBuilder workerExecutionTime(Long workerExecutionTime) {
        this.workerExecutionTime = workerExecutionTime;
        return this;
    }

    public WorkerReport build() {
        return new WorkerReport(this);
    }

    public Long getId() {
        return id;
    }

    public Long getDocsCount() {
        return docsCount;
    }

    public String getStatus() {
        return status;
    }

    public Long getWorkerExecutionTime() {
        return workerExecutionTime;
    }
}
