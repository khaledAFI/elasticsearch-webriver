package com.klead.es.river;

/**
 * Created by kafi on 03/02/2016.
 */

public interface IRiver {

    IndexationResult indexFull(IndexationCommand command);

    IndexationResult indexDelta(IndexationCommand command);
}
