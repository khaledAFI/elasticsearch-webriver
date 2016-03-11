package com.klead.es.river.data.reading;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.data.Document;
import com.klead.es.river.data.Package;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
@Repository
public class SqlDataReader implements IDataReader {

    private static Logger LOGGER = Logger.getLogger(SqlDataReader.class);

    @Autowired
    private String fullDataQuery;
    @Autowired
    private String deltaDataQuery;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public List<Document> readFullData(IndexationCommand command) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        return namedParameterJdbcTemplate.query(fullDataQuery, mapSqlParameterSource, new ResultSetExtractor<List<Document>>() {
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

    public List<Document> readDeltaData(IndexationCommand command) {
        MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
        mapSqlParameterSource.addValue("startDeltaTimestamp", command.getStartDeltaTimestamp());
        mapSqlParameterSource.addValue("endDeltaTimestamp", command.getEndDeltaTimestamp());
        return namedParameterJdbcTemplate.query(deltaDataQuery, mapSqlParameterSource, new ResultSetExtractor<List<Document>>() {
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



}
