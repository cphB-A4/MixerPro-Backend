package rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import dtos.*;
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

import facades.FacadeExample;
import facades.UserFacade;
import utils.EMF_Creator;
import utils.HttpUtils;
import utils.SetupTestUsers;

/**
 * @author lam@cphbusiness.dk
 */
@Path("info")
public class DemoResource {
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

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("select u from User u", entities.User.class);
            List<User> users = query.getResultList();
            return "[" + users.size() + "]";
        } finally {
            em.close();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("populateUsers")
    public String populate() {
        SetupTestUsers.populateUsers();
        return "You have been populated";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("genres")
    public String showAllGenres() {
        List<GenreDTO> allGenres = FACADE.getAllGenres();
        return /*"\"genres\":" +  */gson.toJson(allGenres);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("userGenres/{username}")
   // @RolesAllowed("user")
    public String getUsersFavouriteGenres(@PathParam("username") String username) {

    List<String> usersFavouriteGenres = FACADE.getUsersFavouriteGenres(username);

        return gson.toJson(usersFavouriteGenres);
    }

    @Path("/genre")
    @RolesAllowed("user")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteGenre (String genre){
    String thisUser = securityContext.getUserPrincipal().getName();
    EntityManager em = EMF.createEntityManager();
    try {
        //TypedQuery<User> query = em.createQuery("DELETE  from User u WHERE u. ", entities.User.class);
   GenreDTO genreDTO = instance.deleteGenre(genre);
   return "{\"genre\": \" has been removed\"}";
        //instance.deleteGenre(genre,thisUser);
    } catch (WebApplicationException ex) {
            String errorString = "{\"code\": " + ex.getResponse().getStatus() + ", \"message\": \"" + ex.getMessage() + "\"}";
            return errorString;
        }

        //facade.deleteGenreFromUser
    // deleteGenreFromUser(genre, thisuser)
}

    @Path("/deleteGenreFromUser")
    @RolesAllowed("user")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteGenreFromUser (String genre){
        String thisUser = securityContext.getUserPrincipal().getName();
        EntityManager em = EMF.createEntityManager();

        String deletedMsg = instance.deleteGenreFromUser(genre, thisUser);

        return gson.toJson(deletedMsg);
    }


    @Path("{id}")
    //uncomment later
   // @RolesAllowed("user")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String addEditGenres(@PathParam("id") String username, String genres) {
        try {
            //[ {name: "rap"}, {name: "pop"} ]
            System.out.println(genres);

            Type genreTypeList = new TypeToken<ArrayList<GenreDTO>>(){}.getType();
            List<GenreDTO> genreDTOList = gson.fromJson(genres,genreTypeList);
            int numberOfElementInJson = genreDTOList.size();
            System.out.println(numberOfElementInJson);
            for (GenreDTO genreDTO : genreDTOList) {
                System.out.println(genreDTO.getName());
            }
             FACADE.addGenresToPerson(genreDTOList,username);
            return "worked";
        } catch (WebApplicationException ex) {
            String errorString = "{\"code\": " + ex.getResponse().getStatus() + ", \"message\": \"" + ex.getMessage() + "\"}";
            throw new WebApplicationException(ex.getMessage(), ex.getResponse().getStatus());
           // return errorString;
        }

    }



}