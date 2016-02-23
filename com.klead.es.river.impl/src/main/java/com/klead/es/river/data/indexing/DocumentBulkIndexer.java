package com.klead.es.river.data.indexing;

import com.klead.es.common.EsClusterClient;
import com.klead.es.river.IndexationCommand;
import org.apache.log4j.Logger;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by kafi on 09/02/2016.
 */
@Component("documentBulkIndexer")
public class DocumentBulkIndexer implements IDocumentIndexer {
    private static Logger LOGGER = Logger.getLogger(DocumentBulkIndexer.class);
    private static final long ES_SERVER_CONNECTION_TIMEOUT = 100;
    @Autowired
    private EsClusterClient esClusterClient;

    public void index(List<XContentBuilder> documents, IndexationCommand command) {
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

       responseBulkRequestBuilder.execute().actionGet(ES_SERVER_CONNECTION_TIMEOUT, TimeUnit.SECONDS);

    }
    public void setEsClusterClient(EsClusterClient esClusterClient) {
        this.esClusterClient = esClusterClient;
    }
}
