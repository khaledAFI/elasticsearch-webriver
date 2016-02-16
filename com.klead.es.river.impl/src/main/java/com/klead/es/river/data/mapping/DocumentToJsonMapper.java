package com.klead.es.river.data.mapping;

import com.klead.es.river.data.Document;
import com.klead.es.river.data.Package;
import com.klead.es.river.data.reading.SqlDataReader;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kafi on 09/02/2016.
 */
public class DocumentToJsonMapper implements DocumentMapper {


    private static Logger LOGGER = Logger.getLogger(SqlDataReader.class);

    public List<XContentBuilder> convertDocumentsToJson(List<Document> docs) {
        List<XContentBuilder> jsons = new ArrayList<>();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        try {
            for (Document doc : docs) {
                XContentBuilder jsonSejour = XContentFactory.jsonBuilder().prettyPrint();

                Package pack = (Package) doc;
                jsonSejour.startObject()
                        .field("id").value(pack.getId())
                        .field("idLogement").value(pack.getIdLogement())
                        .field("dureeJours").value(pack.getDureeJours())
                        .field("dureeNuits").value(pack.getDureeNuits())
                        .field("villeDepart").value(pack.getVilleDepart())
                        .field("pension").value(pack.getPension())
                        .field("prix").value(pack.getPrix())
                        .field("prix_j30").value(pack.getPrixj30())
                        .field("offreCompleteId").value(pack.getOffreCompleteId())
                        .field("dispo").value(pack.getDispo())
                        .field("ridTourOperateur").value(pack.getRidTourOperateur())
                        .field("dateCreation").value(dt.format(pack.getDateCreation()))
                        .field("dateMAJ").value(dt.format(pack.getDateMAJ()))
                        .field("coupDeCoeur").value(pack.getCoupDeCoeur())
                        .field("stopAffaire").value(pack.getStopAffaire())

                        .endObject();
                jsons.add(jsonSejour);
            }
        } catch (IOException e) {
            LOGGER.error("ERROR : Creating JSON Document");
        }
        return jsons;
    }
}
