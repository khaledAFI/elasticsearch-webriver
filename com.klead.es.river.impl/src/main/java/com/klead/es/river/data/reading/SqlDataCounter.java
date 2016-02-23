package com.klead.es.river.data.reading;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

/**
 * Created by kafi on 11/02/2016.
 */
@Repository
public class SqlDataCounter {
    @Autowired
    private String dataCountQuery;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public int countNumberOfDocs() {
        return this.namedParameterJdbcTemplate.queryForInt(dataCountQuery, new HashMap<String, Object>());
    }

}
