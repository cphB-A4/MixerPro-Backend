package facades;

import dtos.GenreDTO;
import entities.Genre;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.WebApplicationException;

import security.errorhandling.AuthenticationException;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
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
public GenreDTO deleteGenre(String genre){
    EntityManager em = emf.createEntityManager();
    try {
        em.getTransaction().begin();
        Genre genre1 = em.find(Genre.class, genre);
        em.remove(genre1);
        em.getTransaction().commit();

    } catch (NullPointerException | IllegalArgumentException ex) {
        throw new WebApplicationException("Could not delete: " + genre  + " doesn't not exist", 404);
    } finally {
        em.close();
    }
    return new GenreDTO(genre);
}
}
