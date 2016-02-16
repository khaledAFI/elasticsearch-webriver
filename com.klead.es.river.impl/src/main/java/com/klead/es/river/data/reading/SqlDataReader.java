package com.klead.es.river.data.reading;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.Package;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
public class SqlDataReader implements DataReader {

    private static Logger LOGGER = Logger.getLogger(SqlDataReader.class);


    private String sqlQuery;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public List<Document> readData(IndexationCommand command) {
        Long bulkBlockSize = Long.valueOf(command.getBulkBlockSize());
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        //TODO  add SQL params
//        mapSqlParameterSource.addValue("offset", offset);
//        mapSqlParameterSource.addValue("bulkBlockSize", bulkBlockSize.longValue());
        return namedParameterJdbcTemplate.query(sqlQuery, mapSqlParameterSource, new ResultSetExtractor<List<Document>>() {
                    public List<Document> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                        List<Document> docs = new ArrayList<>();
                        while (resultSet.next()) {
                            Package doc = new Package();
                            doc.setId(resultSet.getLong("ID"));
                            doc.setIdLogement(resultSet.getInt("RID_LOGEMENT"));
                            doc.setDureeJours(resultSet.getInt("DUREE_JOUR"));
                            doc.setDureeNuits(resultSet.getInt("DUREE_NUIT"));
                            doc.setVilleDepart(resultSet.getString("VILLE_DEPART"));
                            doc.setPension(resultSet.getString("REF_PENSION"));
                            doc.setPrix(resultSet.getLong("PRIX"));
                            doc.setPrixj30(resultSet.getLong("PRIX_J30"));
                            doc.setDispo(resultSet.getInt("DISPO"));
                            doc.setOffreCompleteId(resultSet.getLong("OFFRE_COMPLETE_ID"));
                            doc.setCoupDeCoeur(resultSet.getBoolean("IS_COUP_DE_COEUR"));
                            doc.setStopAffaire(resultSet.getBoolean("STOP_AFFAIRE"));
                            doc.setRidTourOperateur(resultSet.getInt("RID_TOUROPERATEUR"));
                            doc.setDateCreation(resultSet.getDate("CREATE_DATE"));
                            doc.setDateMAJ(resultSet.getDate("UPDATE_DATE"));
                            docs.add(doc);
                        }
                        return docs;
                    }
                }
        );
    }



    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}
