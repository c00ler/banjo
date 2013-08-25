package com.gmail.avenderov.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * User: avenderov
 */
@Path("properties")
@Produces(MediaType.APPLICATION_JSON)
public class Properties {

    @GET
    @Path("/{name}")
    public String getConfig(@PathParam("name") final String name) {
        return "test";
    }

}
