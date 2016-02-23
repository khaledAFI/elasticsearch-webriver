package com.klead.es.river.strategy;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;

/**
 * Created by kafi on 16/02/2016.
 */
public interface  IndexationStrategy {

     IndexationResult index(IndexationCommand command);
}
