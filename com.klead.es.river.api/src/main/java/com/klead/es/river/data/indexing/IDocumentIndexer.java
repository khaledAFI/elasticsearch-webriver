package com.klead.es.river.data.indexing;


import com.klead.es.river.IndexationCommand;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
public interface IDocumentIndexer {
    void index(List<XContentBuilder> documents, IndexationCommand command);
}
