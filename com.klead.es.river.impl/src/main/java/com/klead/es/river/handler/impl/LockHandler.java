package com.klead.es.river.handler.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.handler.ILockHandler;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * Lock Strategy base on index name : this means that for a given index only one indexation command can be processed
 * Created by kafi on 23/02/2016.
 *
 */
@Component("lockHandler")
public class LockHandler implements ILockHandler {
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
        Lock l = mLocks.get(command.getIndexName());
        if (l !=  null){
            l.unlock();
        }
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
