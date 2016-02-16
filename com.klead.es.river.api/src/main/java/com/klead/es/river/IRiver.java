package com.klead.es.river;

import com.klead.es.river.exception.PreconditionsValidationException;

/**
 * Created by kafi on 03/02/2016.
 */

public interface IRiver {

    IndexationResult index(IndexationCommand command) throws PreconditionsValidationException;

}
