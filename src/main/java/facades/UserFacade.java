package facades;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.GenreDTO;
import entities.Genre;
import entities.Role;
import entities.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;

import errorhandling.API_Exception;
import errorhandling.UserNotFoundException;
import security.errorhandling.AuthenticationException;

import java.util.List;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public GenreDTO deleteGenre(String genre) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Genre genre1 = em.find(Genre.class, genre);
            em.remove(genre1);
            em.getTransaction().commit();

        } catch (NullPointerException | IllegalArgumentException ex) {
            throw new WebApplicationException("Could not delete: " + genre + " doesn't not exist", 404);
        } finally {
            em.close();
        }
        return new GenreDTO(genre);
    }

    public String updateProfileDescription(String jsonDescription, String username) throws UserNotFoundException, API_Exception {
        EntityManager em = emf.createEntityManager();
        User user;
        String description;
        try {
            JsonObject json = JsonParser.parseString(jsonDescription).getAsJsonObject();
            description = json.get("description").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied",400,e);
        }
        try {

            user = em.find(User.class, username);
            boolean isDescriptionValid = user.setProfileDescription(description);
            if (!isDescriptionValid) {
                throw new WebApplicationException("User description too long",400);
            }
        } catch (WebApplicationException ex){
            throw new WebApplicationException(ex.getMessage(), ex.getResponse().getStatus());
        }
            try {
                em.getTransaction().begin();
                em.merge(user);
                em.getTransaction().commit();

    } finally {
                em.close();

    }
        return "Description successfully updated";
    }


    public String deleteGenreFromUser(String jsonGenre, String username) throws API_Exception {
        EntityManager em = emf.createEntityManager();
        boolean isDeleted = false;
        String message;
        User user;
        String genre;

        //Checks input
try {
        JsonObject json = JsonParser.parseString(jsonGenre).getAsJsonObject();
        genre = json.get("name").getAsString();

    } catch (Exception e) {
        throw new API_Exception("Malformed JSON Suplied",400,e);
    }
        try {
             user = em.find(User.class, username);
            System.out.println(genre);
           isDeleted = user.getFavouriteGenres().remove(new Genre(genre));
           if (!isDeleted){
               throw new WebApplicationException("Error happend during deletion", 400);
           }
            message = genre + "is deleted";
        } catch (WebApplicationException e) {

            throw new WebApplicationException(e.getMessage(),e.getResponse().getStatus());
        }

        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return message;
    }

    public void registerUser(String userJSON) throws API_Exception {
        EntityManager em = emf.createEntityManager();
        String username;
        String password;
        try {
            JsonObject json = JsonParser.parseString(userJSON).getAsJsonObject();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied",400,e);
        }
        User user = new User(username,password);
        em.getTransaction().begin();
        Role userRole = new Role("user");
        user.addRole(userRole);
        em.persist(user);
        em.getTransaction().commit();
        
    }

}
