package net.obstfelder.celleye;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by obsjoa on 10.02.2017.
 */
@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
public class RestResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RestResource.class);

    @GET
    @Path("/alarms/all")
    public Response getAllAlarms()
    {
        LOGGER.info("Rest interface hit!");
        return Response.accepted("DU TRAFF!").build();
    }

}
