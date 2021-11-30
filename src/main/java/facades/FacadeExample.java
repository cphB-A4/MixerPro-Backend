package facades;

import dtos.GenreDTO;
import dtos.RenameMeDTO;
import entities.Genre;
import entities.RenameMe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;

//import errorhandling.RenameMeNotFoundException;
import entities.User;
import utils.EMF_Creator;

/**
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class FacadeExample {

    private static FacadeExample instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private FacadeExample() {
    }


    /**
     * @param _emf
     * @return an instance of this facade class.
     */
    public static FacadeExample getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new FacadeExample();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public RenameMeDTO create(RenameMeDTO rm) {
        RenameMe rme = new RenameMe(rm.getDummyStr1(), rm.getDummyStr2());
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(rme);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new RenameMeDTO(rme);
    }

    public List<String> getUsersFavouriteGenres(String username) {
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        List<String> userSFavouriteGenres = user.getPreSelectedGenres();
        return userSFavouriteGenres;
    }

    public String getUserDescriptionById(String username){
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        String userDescription = user.getProfileDescription();
        if (userDescription == null){
            return "No description yet.";
        }
        System.out.println("getUserDescriptionById: " + userDescription);
        return userDescription;
    }

    public void addGenresToPerson(List<GenreDTO> genreDTOList, String username) {
        EntityManager em = emf.createEntityManager();
        List<Genre> genreList = new ArrayList<>();
        User user = em.find(User.class, username);

        List<String> preSelectedGenres = user.getPreSelectedGenres();
        System.out.println(preSelectedGenres);
        if (preSelectedGenres == null) {
            for (GenreDTO genreDTO : genreDTOList) {
                Genre genre = new Genre(genreDTO.getName());
                // genreList.add(genre);
                user.addGenre(genre);
            }
        } else {
            //sammenligne tideligere genre tilknyttet en user med nye valgte genre
            for (GenreDTO genreDTO : genreDTOList) {
                if (preSelectedGenres.contains(genreDTO.getName())) {
                    throw new WebApplicationException(genreDTO.getName() + " have already been added. Try again", 400);
                }
                System.out.println("helloooooo");
                Genre genre = new Genre(genreDTO.getName());
                // genreList.add(genre);
                user.addGenre(genre);
            }
        }

        try {
            em.getTransaction().begin();
            em.merge(user);
            em.getTransaction().commit();

        } finally {
            em.close();
        }

    }

    public RenameMeDTO getById(long id) { //throws RenameMeNotFoundException {
        EntityManager em = emf.createEntityManager();
        RenameMe rm = em.find(RenameMe.class, id);
//        if (rm == null)
//            throw new RenameMeNotFoundException("The RenameMe entity with ID: "+id+" Was not found");
        return new RenameMeDTO(rm);
    }

    //TODO Remove/Change this before use
    public long getRenameMeCount() {
        EntityManager em = getEntityManager();
        try {
            long renameMeCount = (long) em.createQuery("SELECT COUNT(r) FROM RenameMe r").getSingleResult();
            return renameMeCount;
        } finally {
            em.close();
        }
    }

    public List<RenameMeDTO> getAll() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<RenameMe> query = em.createQuery("SELECT r FROM RenameMe r", RenameMe.class);
        List<RenameMe> rms = query.getResultList();
        return RenameMeDTO.getDtos(rms);
    }

    public List<GenreDTO> getAllGenres() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Genre> query = em.createQuery("SELECT g FROM Genre g", Genre.class);
        List<Genre> genres = query.getResultList();
        return GenreDTO.getDtos(genres);
    }


    public static void main(String[] args) {
        emf = EMF_Creator.createEntityManagerFactory();
        FacadeExample fe = getFacadeExample(emf);
        fe.getAll().forEach(dto -> System.out.println(dto));
    }

}
