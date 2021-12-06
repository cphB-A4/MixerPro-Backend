package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.*;

import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import errorhandling.API_Exception;
import facades.FacadeExample;
import facades.UserFacade;
import utils.EMF_Creator;


@Path("post")
public class PostResource {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();


    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private final FacadeExample FACADE = FacadeExample.getFacadeExample(EMF);
    private final UserFacade instance = UserFacade.getUserFacade(EMF);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous!!\"}";
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }


    @Path("/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("user")
    public String addPost(String post) {
        String thisUser;
        try {
            thisUser = securityContext.getUserPrincipal().getName();
            instance.addPost(post, thisUser);
            return "Post added!";
        }catch(WebApplicationException e){
            throw new WebApplicationException(e.getMessage());
        }
    }

    @Path("/getAllPostsByUsername/{username}")
   // @RolesAllowed("user")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAllPostsByUsername(@PathParam("username") String username) {
        try {
            List<PostDTO> list = instance.getAllPostsByUsername(username);
            return gson.toJson(list);
        } catch (WebApplicationException ex) {
           throw new WebApplicationException(ex.getMessage(),ex.getResponse().getStatus());
        }
    }

    @Path("/deletePost")
    @RolesAllowed("user")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deletePost(String postId) throws API_Exception {
        String thisUser;
        try {
            thisUser = securityContext.getUserPrincipal().getName();
            String msg = instance.deletePost(postId, thisUser);
            return msg;
        } catch (WebApplicationException ex) {
            //String errorString = "{\"code\": " + ex.getResponse().getStatus() + ", \"message\": \"" + ex.getMessage() + "\"}";
            throw new WebApplicationException(ex.getMessage(),ex.getResponse().getStatus());
            //return errorString;//
        }
    }
}