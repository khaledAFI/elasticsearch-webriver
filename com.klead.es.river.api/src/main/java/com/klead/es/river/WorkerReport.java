package com.klead.es.river;

import com.klead.es.river.builder.WorkerReportBuilder;

import java.io.Serializable;

/**
 * Created by kafi on 12/02/2016.
 */
public class WorkerReport implements Serializable {
    private Long id;
    private Long docsCount;
    private String status;
    private Long workerExecutionTime;


    public WorkerReport(WorkerReportBuilder builder) {
        this.id = builder.getId();
        this.docsCount = builder.getDocsCount();
        this.status = builder.getStatus();
        this.workerExecutionTime = builder.getWorkerExecutionTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDocsCount() {
        return docsCount;
    }

    public void setDocsCount(Long docsCount) {
        this.docsCount = docsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getWorkerExecutionTime() {
        return workerExecutionTime;
    }

    public void setWorkerExecutionTime(Long workerExecutionTime) {
        this.workerExecutionTime = workerExecutionTime;
    }


}
