package com.klead.es.river.strategy;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;

/**
 * Created by kafi on 10/03/2016.
 */
public class ExecutionContext {
    private IndexationStrategy indexationStrategy;

    public ExecutionContext(IndexationStrategy indexationStrategy) {
        this.indexationStrategy = indexationStrategy;
    }

    public IndexationResult executeStrategy(IndexationCommand command){
        return indexationStrategy.index(command);
    }
}
