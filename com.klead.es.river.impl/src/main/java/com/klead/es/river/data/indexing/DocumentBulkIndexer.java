package com.klead.es.river.data.indexing;

import com.klead.es.common.EsClusterClient;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.data.Document;
import org.apache.log4j.Logger;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kafi on 09/02/2016.
 */
public class DocumentBulkIndexer implements DocumentIndexer<Document> {
    private static Logger LOGGER = Logger.getLogger(DocumentBulkIndexer.class);
    private static final long ES_SERVER_CONNECTION_TIMEOUT = 100;

    private EsClusterClient esClusterClient;

    public IndexationResult index(List<XContentBuilder> documents, IndexationCommand command) {
        try {
            String indexName = command.getIndexName();
            String indexType = command.getIndexType();
            BulkRequestBuilder responseBulkRequestBuilder = esClusterClient.getClient().prepareBulk();
            for (XContentBuilder document : documents) {
                responseBulkRequestBuilder.add(esClusterClient.getClient()
                        .prepareIndex(indexName, indexType)
                        .setSource(document)
                        .setConsistencyLevel(WriteConsistencyLevel.DEFAULT)
                        .setRouting(command.getRoutingColumn())
                        .request());
            }

            BulkResponse bulkResponse = responseBulkRequestBuilder.execute().actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);
            long tookInMillis = bulkResponse.getTookInMillis();

        } catch (Exception e) {
            LOGGER.error("ERROR : BULK INSERTION DOCUMENT ", e);
        }
        return new IndexationResult();
    }

    public void setEsClusterClient(EsClusterClient esClusterClient) {
        this.esClusterClient = esClusterClient;
    }
}
