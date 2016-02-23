package com.klead.es.river.rest;

import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.MysqlRiver;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by kafi on 03/02/2016.
 */
@Component
@Path("/river")
public class RiverRest  {
    private static final Logger LOGGER = Logger.getLogger(MysqlRiver.class);
    @Autowired
    private MysqlRiver river;

    @POST
    @Path("indexFull")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IndexationResult indexFull(IndexationCommand command) {
        return river.indexFull(command);
    }

    @POST
    @Path("indexDelta")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IndexationResult indexDelta(IndexationCommand command) {
        return river.indexDelta(command);
    }

}
