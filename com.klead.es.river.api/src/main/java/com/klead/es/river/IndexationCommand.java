package com.klead.es.river;

import java.io.Serializable;

/**
 * Created by kafi on 08/02/2016.
 */
public class IndexationCommand implements Serializable{

    private Integer indexationPacketSize;
    private String indexName;
    private String indexType;
    private Integer shardNumber;
    private Integer replicaNumber;
    private String routingColumn;

    public Integer getIndexationPacketSize() {
        return indexationPacketSize;
    }

    public void setIndexationPacketSize(Integer indexationPacketSize) {
        this.indexationPacketSize = indexationPacketSize;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public Integer getShardNumber() {
        return shardNumber;
    }

    public void setShardNumber(Integer shardNumber) {
        this.shardNumber = shardNumber;
    }

    public Integer getReplicaNumber() {
        return replicaNumber;
    }

    public void setReplicaNumber(Integer replicaNumber) {
        this.replicaNumber = replicaNumber;
    }

    public String getRoutingColumn() {
        return routingColumn;
    }

    public void setRoutingColumn(String routingColumn) {
        this.routingColumn = routingColumn;
    }

}
