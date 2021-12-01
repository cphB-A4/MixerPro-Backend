package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import dtos.*;
import entities.Post;
import entities.User;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import errorhandling.API_Exception;
import errorhandling.UserNotFoundException;
import facades.FacadeExample;
import facades.UserFacade;
import utils.EMF_Creator;
import utils.HttpUtils;
import utils.SetupTestUsers;


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

}