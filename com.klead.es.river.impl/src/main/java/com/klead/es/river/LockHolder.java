package com.klead.es.river;

/**
 * Created by kafi on 23/02/2016.
 */
public interface LockHolder {

    boolean tryLock(IndexationCommand command, long secondsToWait);

    void tryUnlock(IndexationCommand command);
}
