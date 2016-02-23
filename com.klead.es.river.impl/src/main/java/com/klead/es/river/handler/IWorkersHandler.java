package com.klead.es.river.handler;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;

/**
 * Created by kafi on 17/02/2016.
 */
public interface IWorkersHandler {

    IndexationResult  runWorkers(IndexationCommand command);
}
