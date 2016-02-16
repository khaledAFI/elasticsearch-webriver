package com.klead.es.river.data.mapping;

import com.klead.es.river.data.Document;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.List;

/**
 * Created by kafi on 08/02/2016.
 */


public interface  DocumentMapper {

     List<XContentBuilder> convertDocumentsToJson(List<Document> docs);

}



