package com.klead.es.river.handler;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.data.Document;

import java.util.List;

/**
 * Created by kafi on 17/02/2016.
 */
public interface IWorkersHandler {

    IndexationResult  runWorkers(IndexationCommand command, List<Document> data);
}
