package com.klead.es.river.strategy.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.ResultCode;
import com.klead.es.river.exception.BusinessException;
import com.klead.es.river.exception.TechnicalException;
import com.klead.es.river.handler.ICommandValidationHandler;
import com.klead.es.river.handler.IPreconditionHandler;
import com.klead.es.river.handler.IWorkersHandler;
import com.klead.es.river.strategy.IndexationStrategy;
import com.klead.es.river.strategy.LockStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by kafi on 16/02/2016.
 */
@Component
public class FullIndexationStrategy implements IndexationStrategy {
    @Value("${tryLockAcquiringTimeout}")
    private  String  tryLockAcquiringTimeout ;
    @Autowired
    private ICommandValidationHandler commandValidationHandler;
    @Autowired
    private IPreconditionHandler preconditionHandler;
    @Autowired
    private IWorkersHandler workersHandler;
    @Autowired
    private LockStrategy indexLockStrategy;


    @Override
    public IndexationResult index(IndexationCommand command) {
        IndexationResult indexationResult = new IndexationResult();
        // check parameters at first
        try {
            commandValidationHandler.validate(command);
        } catch (BusinessException be) {
            indexationResult.setResultCode(be.getMessage());
            return indexationResult;
        }
        // check preconditions before performing indexation
        try {
            // check Lock
            if (indexLockStrategy.tryLock(command, Long.valueOf(tryLockAcquiringTimeout))) {
                try {
                    preconditionHandler.checkPreconditions(command);
                    // Run indexing Workers
                    indexationResult = workersHandler.runWorkers(command);
                }finally {
                    indexLockStrategy.tryUnlock(command);
                }
            } else {
                indexationResult.setResultCode(ResultCode.INDEXATION_ALREADY_RUNNING.name());
            }

        } catch (TechnicalException te) {
            indexationResult.setResultCode(te.getMessage());
        }
        return indexationResult;
    }
}
