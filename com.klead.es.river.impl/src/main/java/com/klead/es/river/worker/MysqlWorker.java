package com.klead.es.river.worker;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.indexing.IDocumentIndexer;
import com.klead.es.river.data.mapping.IDocumentMapper;
import com.klead.es.river.data.marking.IDataMarker;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
@Component
@Scope(value = "prototype")
public class MysqlWorker implements Runnable {

    private Long id;

    @Autowired
    private IDocumentMapper documentToJsonMapper;
    @Autowired
    private IDocumentIndexer documentBulkIndexer;
    @Autowired
    private IDataMarker sqlDataMarker;

    private List<Document> documents;
    private IndexationCommand command;

    public void run() {
        List<XContentBuilder> jsonList = documentToJsonMapper.convertDocumentsToJson(documents);
        documentBulkIndexer.index(jsonList, command);
        sqlDataMarker.markData(documents);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public void setCommand(IndexationCommand command) {
        this.command = command;
    }

}

