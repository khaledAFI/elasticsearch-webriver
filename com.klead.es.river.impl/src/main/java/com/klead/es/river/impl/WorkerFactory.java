package com.klead.es.river.impl;

import com.klead.es.common.AppContext;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.data.Document;

import java.util.List;

/**
 * Created by kafi on 15/02/2016.
 */
public class WorkerFactory {

    MysqlWorker getWorker(long id, List<Document> docs, IndexationCommand command) {
        MysqlWorker mysqlWorker = (MysqlWorker) AppContext.getApplicationContext().getBean("mysqlWorker");
        mysqlWorker.setId(id);
        mysqlWorker.setCommand(command);
        mysqlWorker.setDocuments(docs);
        return mysqlWorker;
    }
}
