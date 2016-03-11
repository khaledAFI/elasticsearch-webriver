package com.klead.es.river.handler;

import com.klead.es.river.IndexationCommand;

/**
 * Created by kafi on 23/02/2016.
 */
public interface ILockHandler {

    boolean tryLock(IndexationCommand command, long secondsToWait);

    void tryUnlock(IndexationCommand command);
}
