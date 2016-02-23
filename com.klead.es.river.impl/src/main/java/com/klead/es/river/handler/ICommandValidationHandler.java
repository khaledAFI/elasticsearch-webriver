package com.klead.es.river.handler;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.exception.BusinessException;

/**
 * Created by kafi on 18/02/2016.
 */
public interface ICommandValidationHandler {

     void validate(IndexationCommand command) throws BusinessException;

}
