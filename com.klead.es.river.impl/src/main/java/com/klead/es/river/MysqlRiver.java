package com.klead.es.river;

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
        return fullIndexationStrategy.index(command);
    }

    @Override
    public IndexationResult indexDelta(IndexationCommand command) {
        return deltaIndexationStrategy.index(command);
    }
}
