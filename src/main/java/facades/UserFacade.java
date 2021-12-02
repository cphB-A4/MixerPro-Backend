package facades;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dtos.GenreDTO;
import entities.Genre;
import entities.Post;
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
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }
        try {

            user = em.find(User.class, username);
            boolean isDescriptionValid = user.setProfileDescription(description);
            if (!isDescriptionValid) {
                throw new WebApplicationException("User description too long", 400);
            }
        } catch (WebApplicationException ex) {
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
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }
        try {
            user = em.find(User.class, username);
            System.out.println(genre);
            isDeleted = user.getFavouriteGenres().remove(new Genre(genre));
            if (!isDeleted) {
                throw new WebApplicationException("Error happend during deletion", 400);
            }
            message = genre + "is deleted";
        } catch (WebApplicationException e) {

            throw new WebApplicationException(e.getMessage(), e.getResponse().getStatus());
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
        User userFromDB;
        String username;
        String password;
        try {
            JsonObject json = JsonParser.parseString(userJSON).getAsJsonObject();
            username = json.get("newUsername").getAsString();
            password = json.get("newPassword").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }
        userFromDB = em.find(User.class, username);
        if (userFromDB == null) {
            User user = new User(username, password);
            em.getTransaction().begin();
            Role userRole = new Role("user");
            user.addRole(userRole);
            em.persist(user);
            em.getTransaction().commit();
        } else {
            throw new WebApplicationException("Username: '" + username + "' is already taken", 404);
        }


    }

    public String addPost(String postJSON, String username) {
        EntityManager em = emf.createEntityManager();
        User user;
        String artist;
        String trackName;
        String coverURL;
        String trackID;
        String description;

        try {
            user = em.find(User.class, username);
            JsonObject json = JsonParser.parseString(postJSON).getAsJsonObject();
            artist = json.get("artist").getAsString();
            trackName = json.get("trackName").getAsString();
            coverURL = json.get("coverURL").getAsString();
            trackID = json.get("trackID").getAsString();
            description = json.get("description").getAsString();
        } catch (Exception e) {
            throw new WebApplicationException("Malformed JSON Suplied", 400);
        }

        try {
            Post post = new Post(user, trackID,  trackName,  artist,  coverURL,  description);
            em.getTransaction().begin();
            em.persist(post);
            //user.addPost(post);
            //em.merge(user);
            em.getTransaction().commit();

            return "Post added";
        } catch (RuntimeException ex) {
            throw new WebApplicationException(ex.getMessage());
        } finally {
            em.close();
        }

    }
    public String deletePost(String postIdJSON, String username) throws WebApplicationException, API_Exception {
        EntityManager em = emf.createEntityManager();
        String postId;
        try {
            JsonObject json = JsonParser.parseString(postIdJSON).getAsJsonObject();
            postId = json.get("postID").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }
        try {
            em.getTransaction().begin();
            Post post = em.find(Post.class, postId);
            if(post.getPost_id() == Integer.parseInt(postId) || post.getUser().getUserName() == username) {
                em.remove(post);
                em.getTransaction().commit();

                return  "{\"msg\": \" successfully deleted\"}";
            } return "{\"msg\": \" You can only delete your own posts\"}";
        } catch (NullPointerException | IllegalArgumentException ex) {
            throw new WebApplicationException("Could not delete, provided id: " + postId + " does not exist", 404);
        } catch (RuntimeException ex) {
            throw new WebApplicationException("Internal Server Problem. We are sorry for the inconvenience", 500);
        } finally {
            em.close();
        }
    }

}
