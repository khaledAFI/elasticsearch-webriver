package com.klead.es.river.strategy.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.strategy.IndexationStrategy;

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
