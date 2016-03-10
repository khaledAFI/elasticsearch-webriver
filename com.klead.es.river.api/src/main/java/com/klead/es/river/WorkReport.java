package com.klead.es.river;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by kafi on 12/02/2016.
 */
public class WorkReport implements Serializable{

    public WorkReport() {
        this.report =new TreeMap<>();
    }

    private Map<Long, WorkerReport> report ;

    public Map<Long, WorkerReport> getReport() {
        return report;
    }

    public void setReport(Map<Long, WorkerReport> report) {
        this.report = report;
    }

}
