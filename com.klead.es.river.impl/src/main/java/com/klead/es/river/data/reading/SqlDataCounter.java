package com.klead.es.river.data.reading;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;

/**
 * Created by kafi on 11/02/2016.
 */
public class SqlDataCounter {

    private String dataCountQuery;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public int countNumberOfDocs() {
        return this.namedParameterJdbcTemplate.queryForInt(dataCountQuery, new HashMap<String, Object>());
    }

    public void setDataCountQuery(String dataCountQuery) {
        this.dataCountQuery = dataCountQuery;
    }

    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}
