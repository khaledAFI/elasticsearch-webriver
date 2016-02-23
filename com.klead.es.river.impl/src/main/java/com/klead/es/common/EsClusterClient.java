package com.klead.es.common;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.Map;

/**
 * Created by kafi on 08/02/2016.
 */

public class EsClusterClient {

    private static final Logger LOGGER = LogManager.getLogger(EsClusterClient.class);

    private Map<Integer,String > elasticSearchNodesPorts;

    private String elasticsearchClusterName;
    private TransportClient client;

    public void init() throws Exception {
        long start = System.currentTimeMillis();
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", elasticsearchClusterName).build();
        TransportClient client = new TransportClient(settings);
        for (Map.Entry<Integer,String> esNodesNames : elasticSearchNodesPorts.entrySet()) {
            client.addTransportAddress(new InetSocketTransportAddress(esNodesNames.getValue(),
                    esNodesNames.getKey()));
        }

        long end = System.currentTimeMillis();
        long nodeInitTime = end - start;
        this.client = client;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("nodeInitTime Time ms: " + nodeInitTime);
        }

    }

    public void destroy() throws Exception {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("es client shutdown");
        }
    }

    public Map<Integer, String> getElasticSearchNodesPorts() {
        return elasticSearchNodesPorts;
    }

    public void setElasticSearchNodesPorts(Map<Integer, String> elasticSearchNodesPorts) {
        this.elasticSearchNodesPorts = elasticSearchNodesPorts;
    }

    public String getElasticsearchClusterName() {
        return elasticsearchClusterName;
    }

    public void setElasticsearchClusterName(String elasticsearchClusterName) {
        this.elasticsearchClusterName = elasticsearchClusterName;
    }

    public TransportClient getClient() {
        return client;
    }

    public void setClient(TransportClient client) {
        this.client = client;
    }
}
