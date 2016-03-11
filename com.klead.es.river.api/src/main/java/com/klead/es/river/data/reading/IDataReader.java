package com.klead.es.river.data.reading;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.data.Document;

import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
public interface IDataReader {
    List<Document> readFullData(IndexationCommand command);
    List<Document> readDeltaData(IndexationCommand command);
}
