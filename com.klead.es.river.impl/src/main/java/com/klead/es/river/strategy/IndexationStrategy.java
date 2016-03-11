package com.klead.es.river.strategy;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.ResultCode;
import com.klead.es.river.data.reading.SqlDataReader;
import com.klead.es.river.exception.BusinessException;
import com.klead.es.river.exception.TechnicalException;
import com.klead.es.river.handler.ICommandValidationHandler;
import com.klead.es.river.handler.ILockHandler;
import com.klead.es.river.handler.IPreconditionHandler;
import com.klead.es.river.handler.IWorkersHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * Created by kafi on 16/02/2016.
 */
public abstract class IndexationStrategy {
    @Value("${tryLockAcquiringTimeout}")
    private String tryLockAcquiringTimeout;
    @Autowired
    private ICommandValidationHandler commandValidationHandler;
    @Autowired
    private IPreconditionHandler preconditionHandler;
    @Autowired
    protected IWorkersHandler workersHandler;
    @Autowired
    private ILockHandler lockHandler;
    @Autowired
    protected SqlDataReader sqlDataReader;

    public IndexationResult index(IndexationCommand command) {
        {
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
                if (lockHandler.tryLock(command, Long.valueOf(tryLockAcquiringTimeout))) {
                    try {
                        preconditionHandler.checkPreconditions(command);
                        // Run indexing workers
                        indexationResult = proceed(command);
                       
                    } finally {
                        lockHandler.tryUnlock(command);
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

    protected abstract IndexationResult proceed(IndexationCommand command);
}
