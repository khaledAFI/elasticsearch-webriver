package com.klead.es.river.strategy;

import com.klead.es.river.IndexationCommand;

/**
 * Created by kafi on 23/02/2016.
 */
public interface LockStrategy {

    boolean tryLock(IndexationCommand command, long secondsToWait);

    void tryUnlock(IndexationCommand command);
}
