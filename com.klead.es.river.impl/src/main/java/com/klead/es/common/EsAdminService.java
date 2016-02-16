package com.klead.es.common;

import com.klead.es.river.impl.Settings;
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
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.indexing.IndexingStats;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Created by kafi on 08/02/2016.
 */
public class EsAdminService {
    private static final Logger LOGGER = Logger.getLogger(EsAdminService.class);

    public static Integer ES_SERVER_CONNECTION_TIMEOUT = 300;

    private EsClusterClient esClusterClient;



    public boolean indexExists(String indexName) throws Exception {
        IndicesExistsResponse res = esClusterClient.getClient()
                .admin()
                .indices()
                .prepareExists(indexName)
                .execute()
                .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        boolean exists = res.isExists();
        if (LOGGER.isInfoEnabled()) {
            String msg = exists ? "exists" : "does not exist";
            LOGGER.info("indexName: '" + indexName + "' " + msg);
        }
        return exists;
    }

    public boolean createIndex(String indexName, HashMap<String, Object> configProperties) throws Exception {
        IndicesAdminClient indicesAdminClient = esClusterClient.getClient().admin()
                .indices();
        CreateIndexResponse response = indicesAdminClient
                .prepareCreate(indexName)
                .setSettings(ImmutableSettings.settingsBuilder()
                        .put("number_of_shards", configProperties.get(Settings.SHARD_NUMBER.name()))
                        .put("number_of_replicas", configProperties.get(Settings.REPLICA_NUMBER.name())))
                .addMapping(
                        configProperties.get(Settings.INDEX_TYPE.name()).toString(),
                        configProperties.get(Settings.INDEX_TYPE_MAPPING.name()).toString())
                .execute()
                .get(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        boolean acknowledged = response.isAcknowledged();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(acknowledged ? "********************* index created" : "**************** Index Not created");
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
            LOGGER.debug(acknowledged ? "********************* index deleted" : "**************** Index Not deleted");
        }
        return acknowledged;
    }


    public void purgeIndex(String indexName, String indexType) throws Exception {
        DeleteByQueryResponse response = esClusterClient.getClient().prepareDeleteByQuery(indexName).
                setQuery(QueryBuilders.matchAllQuery()).
                setTypes(indexType).
                execute().actionGet();
        refreshIndex(indexName);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug( "********************* index purged" );
        }
    }

    public FlushResponse flushIndex(String indexName) throws Exception {
        FlushResponse res = esClusterClient.getClient()
                .admin()
                .indices()
                .prepareFlush(indexName)
                .execute()
                .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        return res;
    }

    public RefreshResponse refreshIndex(String indexName) throws Exception {
        RefreshResponse res = esClusterClient.getClient()
                .admin()
                .indices()
                .prepareRefresh(indexName)
                .execute()
                .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        return res;
    }



    public UpdateSettingsResponse updateIndexSettings( String indexName, HashMap<String, Object> settings) throws Exception {
        UpdateSettingsResponse res = esClusterClient.getClient().admin().indices()
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
        return res;
    }


    public  boolean verifyClusterStatus(ClusterHealthStatus status)  {
        boolean isStatus = true;
        try {
            ClusterHealthResponse healthResponse =  esClusterClient.getClient().admin().cluster().health(new ClusterHealthRequest().waitForStatus(status))
                    .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS);
            if (healthResponse != null && healthResponse.isTimedOut()) {
                isStatus =  false;
            }
        } catch (ElasticsearchTimeoutException e) {
            isStatus =false;
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

    public Hashtable<String, Object> indexStats(String indexName) throws Exception {
        IndicesStatsResponse res = esClusterClient.getClient()
                .admin()
                .indices()
                .prepareStats(indexName)
                .all()
                .execute()
                .actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
        IndexingStats indexing = res.getTotal().getIndexing();
        long indexCount = indexing.getTotal().getIndexCount();
        TimeValue indexTime = indexing.getTotal().getIndexTime();
        double debitSecond = indexCount / (indexTime.getSeconds() == 0 ? 0.0001 : indexTime.getSeconds());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("====================================== total indexed docs: " + indexCount);
            LOGGER.info("====================================== indexation time  in seconde: " + indexTime.getSeconds());
            LOGGER.info("====================================== indexation time  in minute: " + indexTime.getMinutes());
            LOGGER.info("====================================== debit docs/s  " + debitSecond);
        }
        Hashtable<String, Object> meticsInfos = new Hashtable<String, Object>();
        meticsInfos.put("totalIndexedDocs", indexCount);
        meticsInfos.put("indexationTimeSecond", indexTime.getSeconds());
        meticsInfos.put("throuputDocsPerSecond", debitSecond);
        return meticsInfos;
    }

    public EsClusterClient getEsClusterClient() {
        return esClusterClient;
    }

    public void setEsClusterClient(EsClusterClient esClusterClient) {
        this.esClusterClient = esClusterClient;
    }


}

