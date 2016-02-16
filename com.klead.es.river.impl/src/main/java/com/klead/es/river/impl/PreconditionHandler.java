package com.klead.es.river.impl;

import com.klead.es.common.EsAdminService;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.ResultCode;
import com.klead.es.river.exception.PreconditionsValidationException;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;

import java.util.HashMap;

/**
 * Created by kafi on 12/02/2016.
 */
public class PreconditionHandler {

    private static final Logger LOGGER = Logger.getLogger(PreconditionHandler.class);
    private EsAdminService esAdminService;
    private String indexTypeMapping;

    public  void verifyPreconditions(IndexationCommand command, IndexationResult indexationResult) throws PreconditionsValidationException {
        try {
            checkClusterHealth(indexationResult);
            checkIndexHealth(command, indexationResult);
        } catch (Exception e) {
            LOGGER.error("PRECONDITIONS check failed !!!. aborting indexation process");
            throw new PreconditionsValidationException();
        }
    }

    private void checkIndexHealth(IndexationCommand command, IndexationResult indexationResult) throws Exception {
        String indexName = command.getIndexName();
        String indexType = command.getIndexType();
        if(esAdminService.indexExists(indexName)){
            if (!esAdminService.deleteIndex(indexName)){
                indexationResult.setResultCode(ResultCode.OLD_INDEX_DELETE_ERROR.name());
                throw new PreconditionsValidationException();
            }
        }
        HashMap<String, Object> configProperties = new HashMap<>();
        configProperties.put(Settings.SHARD_NUMBER.name(), command.getShardNumber());
        configProperties.put(Settings.REPLICA_NUMBER.name(), command.getReplicaNumber());
        configProperties.put(Settings.INDEX_TYPE.name(), indexType);
        configProperties.put(Settings.INDEX_TYPE_MAPPING.name(), indexTypeMapping);

        if(! esAdminService.createIndex(indexName, configProperties)){
            indexationResult.setResultCode(ResultCode.INDEX_CREATION_ERROR.name());
            throw new PreconditionsValidationException();
        }

        ClusterHealthStatus status = esAdminService.ensureGreen(indexName);
        if (!ClusterHealthStatus.GREEN.equals(status)) {
            indexationResult.setResultCode(ResultCode.INDEX_HEALTH_ERROR.name());
            throw new PreconditionsValidationException();
        }
        LOGGER.info("INDEX Health : GREEN");
    }

    private void checkClusterHealth(IndexationResult indexationResult) throws PreconditionsValidationException {
        // check Es cluster health
        if (!esAdminService.verifyClusterStatus(ClusterHealthStatus.GREEN)) {
            indexationResult.setResultCode(ResultCode.CLUSTER_HEALTH_ERROR.name());
            throw new PreconditionsValidationException();
        }
        LOGGER.info("CLUSTER Health : GREEN");
    }

    public void setEsAdminService(EsAdminService esAdminService) {
        this.esAdminService = esAdminService;
    }

    public void setIndexTypeMapping(String indexTypeMapping) {
        this.indexTypeMapping = indexTypeMapping;
    }
}
