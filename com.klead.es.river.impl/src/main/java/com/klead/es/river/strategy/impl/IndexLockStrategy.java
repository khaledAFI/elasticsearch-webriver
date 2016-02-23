package com.klead.es.river.strategy.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.handler.impl.PreconditionHandler;
import com.klead.es.river.strategy.LockStrategy;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by kafi on 23/02/2016.
 */
@Component("indexLockStrategy")
public class IndexLockStrategy implements LockStrategy {
    private static final Logger LOGGER = Logger.getLogger(PreconditionHandler.class);

    private Map<String, Lock> mLocks = new HashMap<>();

    @Override
    public boolean tryLock(IndexationCommand command,long secondsToWait) {
        Lock lock = getLock(command);
        boolean acquired = false;
        try {
             acquired = lock.tryLock(secondsToWait, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
          LOGGER.error("ERROR : acquiring Lock for index name : "+command.getIndexName());
        }
        return acquired;
    }


    @Override
    public void tryUnlock(IndexationCommand command){
        Lock lock = getLock(command);
        lock.unlock();
    }

    private Lock getLock(IndexationCommand command) {
        Lock l = mLocks.get(command.getIndexName());
        if (l == null) {
            l = new ReentrantLock();
            mLocks.put(command.getIndexName(), l);
        }
        return l;
    }
}
