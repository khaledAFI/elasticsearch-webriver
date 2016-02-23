package com.klead.es.river;

import java.io.Serializable;

/**
 * Created by kafi on 23/02/2016.
 */
public class EsReport implements Serializable {

    public EsReport() {
    }

    private Long indexEntryCount;
    private Long  indexationExecutionTime;

    public void setIndexEntryCount(Long indexEntryCount) {
        this.indexEntryCount = indexEntryCount;
    }

    public void setIndexationExecutionTime(Long indexationExecutionTime) {
        this.indexationExecutionTime = indexationExecutionTime;
    }

    public Long getIndexEntryCount() {
        return indexEntryCount;
    }

    public Long getIndexationExecutionTime() {
        return indexationExecutionTime;
    }
}
