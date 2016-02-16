package com.klead.es.river.data.indexing;


import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.data.Document;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
public interface DocumentIndexer<D extends Document> {
    IndexationResult index(List<XContentBuilder> documents, IndexationCommand command);
}
