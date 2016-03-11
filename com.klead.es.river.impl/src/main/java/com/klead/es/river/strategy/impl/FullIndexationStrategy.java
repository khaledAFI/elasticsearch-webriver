package com.klead.es.river.strategy.impl;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.ResultCode;
import com.klead.es.river.data.Document;
import com.klead.es.river.strategy.IndexationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by kafi on 16/02/2016.
 */
@Component
public class FullIndexationStrategy extends IndexationStrategy {


    @Override
    protected IndexationResult proceed(IndexationCommand command) {
        // get data to index
        List<Document> data = sqlDataReader.readFullData(command);
        if (data.size()==0) {
            IndexationResult indexationResult = new IndexationResult();
            indexationResult.setResultCode(ResultCode.NO_DATA_TO_INDEX.name());
            return indexationResult;
        }
        return  workersHandler.runWorkers(command, data);
    }
}
