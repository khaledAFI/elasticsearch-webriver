package com.klead.es.river.factory;

import com.klead.es.common.AppContext;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.data.Document;
import com.klead.es.river.worker.MysqlWorker;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by kafi on 15/02/2016.
 */
@Component
public class WorkerFactory {

    public MysqlWorker getWorker(long id, List<Document> docs, IndexationCommand command) {
        MysqlWorker mysqlWorker =AppContext.getApplicationContext().getBean("mysqlWorker", MysqlWorker.class);
        mysqlWorker.setId(id);
        mysqlWorker.setCommand(command);
        mysqlWorker.setDocuments(docs);
        return mysqlWorker;
    }
}
