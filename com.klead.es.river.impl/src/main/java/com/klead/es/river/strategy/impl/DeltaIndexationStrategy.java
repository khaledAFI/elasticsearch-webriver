package com.klead.es.river.strategy.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.exception.BusinessException;
import com.klead.es.river.exception.TechnicalException;
import com.klead.es.river.handler.ICommandValidationHandler;
import com.klead.es.river.handler.IPreconditionHandler;
import com.klead.es.river.handler.IWorkersHandler;
import com.klead.es.river.strategy.IndexationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by kafi on 16/02/2016.
 */
@Component
public class DeltaIndexationStrategy implements IndexationStrategy {

    @Autowired
    private ICommandValidationHandler commandValidationHandler;
    @Autowired
    private IPreconditionHandler preconditionHandler;
    @Autowired
    private IWorkersHandler workersHandler;

    @Override
    public IndexationResult index(IndexationCommand command) {
        IndexationResult indexationResult = null;
        // check parameters at first
        try {
            commandValidationHandler.validate(command);
        } catch (BusinessException be) {
            indexationResult.setResultCode(be.getMessage());
            return indexationResult;
        }
        // check preconditions before performing indexation
        try {
            preconditionHandler.checkPreconditions(command);
        } catch (TechnicalException te) {
            indexationResult.setResultCode(te.getMessage());
            return indexationResult;
        }
        // Run indexing Workers
        indexationResult = workersHandler.runWorkers(command);

        return indexationResult;
    }
}
