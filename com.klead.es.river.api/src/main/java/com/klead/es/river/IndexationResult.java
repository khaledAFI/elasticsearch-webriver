package com.klead.es.river;

import java.io.Serializable;

/**
 * Created by kafi on 08/02/2016.
 */
public class IndexationResult implements Serializable {

    private Integer workersNumber;
    private Long totalDocsCount;
    private Long totalSuccessDocsCount;
    private Long totalFailureDocsCount;
    private String resultCode;
    private WorkReport workReport;
    private Long totalExecutionTime;

    public Long getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public void setTotalExecutionTime(Long totalExecutionTime) {
        this.totalExecutionTime = totalExecutionTime;
    }

    public Integer getWorkersNumber() {
        return workersNumber;
    }

    public void setWorkersNumber(Integer workersNumber) {
        this.workersNumber = workersNumber;
    }

    public Long getTotalDocsCount() {
        return totalDocsCount;
    }

    public void setTotalDocsCount(Long totalDocsCount) {
        this.totalDocsCount = totalDocsCount;
    }

    public Long getTotalSuccessDocsCount() {
        return totalSuccessDocsCount;
    }

    public void setTotalSuccessDocsCount(Long totalSuccessDocsCount) {
        this.totalSuccessDocsCount = totalSuccessDocsCount;
    }

    public Long getTotalFailureDocsCount() {
        return totalFailureDocsCount;
    }

    public void setTotalFailureDocsCount(Long totalFailureDocsCount) {
        this.totalFailureDocsCount = totalFailureDocsCount;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }


    public WorkReport getWorkReport() {
        return workReport;
    }

    public void setWorkReport(WorkReport workReport) {
        this.workReport = workReport;
    }
}
