package com.klead.es.river.rest;

import com.klead.es.river.IRiver;
import com.klead.es.river.IndexationCommand;
import com.klead.es.river.IndexationResult;
import com.klead.es.river.impl.MysqlRiver;
import org.apache.log4j.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by kafi on 03/02/2016.
 */

@Path("/river")
public class RiverRest implements IRiver {
    private static final Logger logger = Logger.getLogger(MysqlRiver.class);

    private MysqlRiver river;
    @POST
    @Path("index")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public IndexationResult index(IndexationCommand command) {
        return river.index(command);
    }

    public MysqlRiver getRiver() {
        return river;
    }

    public void setRiver(MysqlRiver river) {
        this.river = river;
    }
}
