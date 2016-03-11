package com.klead.es.river;

/**
 * Created by kafi on 11/02/2016.
 */
public enum ResultCode {
    INDEXATION_SUCCESS,
    INDEXATION_ALREADY_RUNNING,
    CLUSTER_HEALTH_ERROR,
    INDEX_HEALTH_ERROR,
    OLD_INDEX_DELETE_ERROR,
    INDEX_CREATION_ERROR,
    INDEXATION_COMMAND_EMPTY,
    INDEXATION_PACKET_SIZE_EMPTY,
    INDEXATION_PACKET_SIZE_ILLEGAL,
    INDEX_NAME_EMPTY,
    INDEX_TYPE_EMPTY,
    REPLICA_NUMBER_EMPTY,
    SHARD_NUMBER_EMPTY,
    ROUTING_COLUMN_EMPTY,
    ROUTING_COLUMN_UNKNOWN,
    PRECONDITION_ERROR,
    NO_DATA_TO_INDEX;


}
