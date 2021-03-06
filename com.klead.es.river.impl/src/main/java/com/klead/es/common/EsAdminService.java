package com.klead.es.common;

import com.klead.es.river.EsReport;
import com.klead.es.river.Settings;
import com.klead.es.river.exception.TechnicalException;
import org.apache.log4j.Logger;
import org.elasticsearch.ElasticsearchTimeoutException;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.Priority;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Created by kafi on 08/02/2016.
 */

public class EsAdminService {
    private static final Logger LOGGER = Logger.getLogger(EsAdminService.class);

    public static Integer ES_SERVER_CONNECTION_TIMEOUT = 300;
    @Autowired
    private EsClusterClient esClusterClient;


    public boolean indexExists(String indexName) throws TechnicalException {
        boolean exists = false;
        try {
            IndicesExistsResponse res = esClusterClient.getClient()
                    .admin()
                    .indices()
                    .prepareExists(indexName)
                    .execute()
                    .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            exists = res.isExists();
            if (LOGGER.isInfoEnabled()) {
                String msg = exists ? "exists" : "does not exist";
                LOGGER.info("indexName: '" + indexName + "' " + msg);
            }
        } catch (Exception e) {
            LOGGER.error("Index exist failed", e);
            throw new TechnicalException("Error while checking if index exists", e);
        }
        return exists;
    }

    public boolean createIndex(String indexName, HashMap<String, Object> configProperties) throws TechnicalException {
        boolean acknowledged = false;
        try {
            IndicesAdminClient indicesAdminClient = esClusterClient.getClient().admin()
                    .indices();
            CreateIndexResponse
                    response = indicesAdminClient
                    .prepareCreate(indexName)
                    .setSettings(ImmutableSettings.settingsBuilder()
                            .put("number_of_shards", configProperties.get(Settings.SHARD_NUMBER.name()))
                            .put("number_of_replicas", configProperties.get(Settings.REPLICA_NUMBER.name())))
                    .addMapping(
                            configProperties.get(Settings.INDEX_TYPE.name()).toString(),
                            configProperties.get(Settings.INDEX_TYPE_MAPPING.name()).toString())
                    .execute()
                    .get(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            acknowledged = response.isAcknowledged();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(acknowledged ? "INDEX created" : "INDEX Not created");
            }
        } catch (Exception e) {
            LOGGER.error("Index creation failed", e);
            throw new TechnicalException("Error while creating index", e);
        }

        return acknowledged;
    }

    public boolean deleteIndex(String indexName) {
        IndicesAdminClient indicesAdminClient = esClusterClient.getClient().admin()
                .indices();
        DeleteIndexResponse response = indicesAdminClient
                .prepareDelete(indexName)
                .execute().actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        boolean acknowledged = response.isAcknowledged();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(acknowledged ? "INDEX deleted" : "INDEX Not deleted");
        }
        return acknowledged;
    }


    public void purgeIndex(String indexName, String indexType) throws TechnicalException {
        try {
            DeleteByQueryResponse response = esClusterClient.getClient().prepareDeleteByQuery(indexName).
                    setQuery(QueryBuilders.matchAllQuery()).
                    setTypes(indexType).
                    execute().actionGet();
            refreshIndex(indexName);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("INDEX purged");
            }
        } catch (Exception e) {
            LOGGER.error("Index purge failed", e);
            throw new TechnicalException("Error while purging index", e);
        }
    }

    public FlushResponse flushIndex(String indexName) throws TechnicalException {
        FlushResponse res = null;
        try {
            res = esClusterClient.getClient()
                    .admin()
                    .indices()
                    .prepareFlush(indexName)
                    .execute()
                    .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("Index flush failed", e);
            throw new TechnicalException("Error while flushing index", e);
        }
        return res;
    }

    public RefreshResponse refreshIndex(String indexName) throws TechnicalException {
        RefreshResponse res = null;
        try {
            res = esClusterClient.getClient()
                    .admin()
                    .indices()
                    .prepareRefresh(indexName)
                    .execute()
                    .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        } catch (Exception e) {
            LOGGER.error("Index refresh failed", e);
            throw new TechnicalException("Error while refreshing index", e);
        }
        return res;
    }


    public UpdateSettingsResponse updateIndexSettings(String indexName, HashMap<String, Object> settings) throws TechnicalException {
        UpdateSettingsResponse res = null;
        try {
            res = esClusterClient.getClient().admin().indices()
                    .prepareUpdateSettings(indexName)
                    .setSettings(settings)
                    .execute()
                    .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            if (LOGGER.isInfoEnabled()) {
                Set<String> keys = settings.keySet();
                for (String key : keys) {
                    LOGGER.info(key + ": has been updated with: '" + settings.get(key) + "' ");
                }
            }
        } catch (Exception e) {
            LOGGER.error("Index setting update failed", e);
            throw new TechnicalException("Error while updating index setting", e);
        }
        return res;
    }


    public boolean verifyClusterStatus(ClusterHealthStatus status) {
        boolean isStatus = true;
        try {
            ClusterHealthResponse healthResponse = esClusterClient.getClient().admin().cluster().health(new ClusterHealthRequest().waitForStatus(status))
                    .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            if (healthResponse != null && healthResponse.isTimedOut()) {
                isStatus = false;
            }
        } catch (ElasticsearchTimeoutException e) {
            isStatus = false;
            LOGGER.error("timeout, cluster does not respond to health request, cowardly refusing to continue with operations");
        }
        return isStatus;
    }

    public ClusterHealthStatus ensureGreen(final String... indexes) {
        final ClusterHealthResponse actionGet = esClusterClient.getClient()
                .admin()
                .cluster()
                .health(Requests.clusterHealthRequest(indexes)
                        .waitForGreenStatus().waitForEvents(Priority.LANGUID)
                        .waitForRelocatingShards(0)).actionGet();
        if (actionGet.isTimedOut()) {
            LOGGER.error("ensureGreen timed out, cluster state:\n"
                    + esClusterClient.getClient().admin().cluster().prepareState().get()
                    .getState().prettyPrint()
                    + "\n"
                    + esClusterClient.getClient().admin().cluster().preparePendingClusterTasks()
                    .get().prettyPrint());
        }
        return actionGet.getStatus();
    }

    public EsReport indexStatistics(String indexName) throws Exception {
        IndicesStatsResponse res = esClusterClient.getClient()
                .admin()
                .indices()
                .prepareStats(indexName)
                .all()
                .execute()
                .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        EsReport esReport = new EsReport();
        esReport.setIndexationExecutionTime(res.getTotal().getIndexing().getTotal().getIndexTime().getMillis());
        esReport.setIndexEntryCount(res.getTotal().getIndexing().getTotal().getIndexCount());
        return esReport;
    }

    public void setEsClusterClient(EsClusterClient esClusterClient) {
        this.esClusterClient = esClusterClient;
    }
}

