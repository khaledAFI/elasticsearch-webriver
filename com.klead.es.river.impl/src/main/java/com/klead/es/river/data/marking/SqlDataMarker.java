package com.klead.es.river.data.marking;

import com.klead.es.river.data.Document;
import com.klead.es.river.data.Package;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * Created by kafi on 17/02/2016.
 */
@Component
public class SqlDataMarker implements IDataMarker {
    @Autowired
    private String dataMarkQuery;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void markData(List<Document> documents) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        Collection ids = CollectionUtils.collect(documents, new Transformer() {
            @Override
            public Object transform(Object o) {
                return ((Package) o).getId();
            }
        });
        mapSqlParameterSource.addValue("ids", ids);
        namedParameterJdbcTemplate.update(dataMarkQuery, mapSqlParameterSource);
    }
}
