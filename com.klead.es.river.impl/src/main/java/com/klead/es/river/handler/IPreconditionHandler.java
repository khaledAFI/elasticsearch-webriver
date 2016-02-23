package com.klead.es.river.handler;

import com.klead.es.river.IndexationCommand;

/**
 * Created by kafi on 17/02/2016.
 */
public interface IPreconditionHandler {

    void checkPreconditions(IndexationCommand command);
}
