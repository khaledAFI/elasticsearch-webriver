package com.klead.es.river.handler.impl;

import com.klead.es.common.EsAdminService;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.ResultCode;
import com.klead.es.river.Settings;
import com.klead.es.river.exception.TechnicalException;
import com.klead.es.river.handler.IPreconditionHandler;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * Created by kafi on 12/02/2016.
 */
@Component
public class PreconditionHandler implements IPreconditionHandler {

    private static final Logger LOGGER = Logger.getLogger(PreconditionHandler.class);
    @Autowired
    private EsAdminService esAdminService;
    @Autowired
    private String indexTypeMapping;

    @Override
    public void checkPreconditions(IndexationCommand command) throws TechnicalException {
        try {
            checkClusterHealth();
            checkIndexHealth(command);
        } catch (Exception e) {
            LOGGER.error("PRECONDITIONS check failed !!!. aborting indexation process");
            throw new TechnicalException(ResultCode.PRECONDITION_ERROR.name(), e);
        }
    }

    private void checkIndexHealth(IndexationCommand command) throws TechnicalException {
        String indexName = command.getIndexName();
        String indexType = command.getIndexType();
        if (esAdminService.indexExists(indexName)) {
            if (!esAdminService.deleteIndex(indexName)) {
                throw new TechnicalException(ResultCode.OLD_INDEX_DELETE_ERROR.name());
            }
        }
        HashMap<String, Object> configProperties = new HashMap<>();
        configProperties.put(Settings.SHARD_NUMBER.name(), command.getShardNumber());
        configProperties.put(Settings.REPLICA_NUMBER.name(), command.getReplicaNumber());
        configProperties.put(Settings.INDEX_TYPE.name(), indexType);
        configProperties.put(Settings.INDEX_TYPE_MAPPING.name(), indexTypeMapping);

        if (!esAdminService.createIndex(indexName, configProperties)) {
            throw new TechnicalException(ResultCode.INDEX_CREATION_ERROR.name());
        }

        ClusterHealthStatus status = esAdminService.ensureGreen(indexName);
        if (!ClusterHealthStatus.GREEN.equals(status)) {
            throw new TechnicalException(ResultCode.INDEX_HEALTH_ERROR.name());
        }
        LOGGER.info("INDEX Health : GREEN");
    }

    private void checkClusterHealth() throws TechnicalException {
        // check Es cluster health
        if (!esAdminService.verifyClusterStatus(ClusterHealthStatus.GREEN)) {
            throw new TechnicalException(ResultCode.CLUSTER_HEALTH_ERROR.name());
        }
        LOGGER.info("CLUSTER Health : GREEN");
    }

}
