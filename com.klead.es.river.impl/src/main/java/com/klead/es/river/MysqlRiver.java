package com.klead.es.river;

import com.klead.es.river.strategy.impl.ExecutionContext;
import com.klead.es.river.strategy.IndexationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kafi on 03/02/2016.
 */

@Component
public class MysqlRiver implements IRiver {

    @Autowired
    private IndexationStrategy fullIndexationStrategy;
    @Autowired
    private IndexationStrategy deltaIndexationStrategy;

    @Override
    public IndexationResult indexFull(IndexationCommand command) {
        ExecutionContext  executionContext = new ExecutionContext(fullIndexationStrategy);
        return executionContext.executeStrategy(command);
    }

    @Override
    public IndexationResult indexDelta(IndexationCommand command) {
        ExecutionContext  executionContext = new ExecutionContext(deltaIndexationStrategy);
        return executionContext.executeStrategy(command);
    }
}
