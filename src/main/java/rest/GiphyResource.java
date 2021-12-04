package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.PostDTO;
import errorhandling.API_Exception;
import facades.FacadeExample;
import facades.GiphyFacade;
import facades.UserFacade;
import utils.EMF_Creator;

import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.List;


@Path("giphy")
public class GiphyResource {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();


    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final GiphyFacade giphyFacade = GiphyFacade.getGiphyFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @Path("/getTrendingGifs")
   // @RolesAllowed("user")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getTrendingGifs() {
        try {
            List<String> gifUrlList = giphyFacade.getTrendingGifs();
            return gson.toJson(gifUrlList);
        } catch (WebApplicationException | IOException ex) {
           throw new WebApplicationException(ex.getMessage(),500);
        }
    }


}