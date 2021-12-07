package utils;


import dtos.GenreDTO;
import entities.Genre;
import entities.Role;
import entities.User;
import facades.FacadeExample;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;

public class SetupTestUsers {

  public static void populateUsers() {

    EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    EntityManager em = emf.createEntityManager();
    FacadeExample fe = FacadeExample.getFacadeExample(emf);

    // IMPORTAAAAAAAAAANT!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    // This breaks one of the MOST fundamental security rules in that it ships with default users and passwords
    // CHANGE the three passwords below, before you uncomment and execute the code below
    // Also, either delete this file, when users are created or rename and add to .gitignore
    // Whatever you do DO NOT COMMIT and PUSH with the real passwords

    User user = new User("user", "test1");
    User admin = new User("admin", "test2");
    User both = new User("user_admin", "test3");

    if(admin.getUserPass().equals("test")||user.getUserPass().equals("test")||both.getUserPass().equals("test"))
      throw new UnsupportedOperationException("You have not changed the passwords");

   em.getTransaction().begin();
    Role userRole = new Role("user");
    Role adminRole = new Role("admin");
    user.addRole(userRole);
    admin.addRole(adminRole);
    both.addRole(userRole);
    both.addRole(adminRole);
    em.persist(userRole);
    em.persist(adminRole);
    em.persist(user);
    em.persist(admin);
    em.persist(both);
    em.getTransaction().commit();
    System.out.println("PW: " + user.getUserPass());
    System.out.println("Testing user with OK password: " + user.verifyPassword("test"));
    System.out.println("Testing user with wrong password: " + user.verifyPassword("test1"));
    System.out.println("Created TEST Users");

    //Test user_genre table
   /* List<GenreDTO> genreDTOList = new ArrayList<>();
    genreDTOList.add(new GenreDTO("rap"));
    genreDTOList.add(new GenreDTO("pop"));
    fe.addGenresToPerson(genreDTOList,"user");

    */
  }
  public static void addGenres(){
      EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
      EntityManager em = emf.createEntityManager();
      List <Genre> genres = new ArrayList<>();

     genres.add(new Genre("acoustic"));
     genres.add(new Genre("afrobeat"));
     genres.add(new Genre("alt-rock"));
     genres.add(new Genre("alternative"));
     genres.add(new Genre("ambient"));
     genres.add(new Genre("anime"));
     genres.add(new Genre("black-metal"));
     genres.add(new Genre("bluegrass"));
     genres.add(new Genre("blues"));
     genres.add(new Genre("bossanova"));
     genres.add(new Genre("brazil"));
     genres.add(new Genre("breakbeat"));
     genres.add(new Genre("british"));
     genres.add(new Genre("cantopop"));
     genres.add(new Genre("chicago-house"));
     genres.add(new Genre("children"));
     genres.add(new Genre("chill"));
     genres.add(new Genre("classical"));
     genres.add(new Genre("club"));
     genres.add(new Genre("comedy"));
     genres.add(new Genre("country"));
     genres.add(new Genre("dance"));
     genres.add(new Genre("dancehall"));
     genres.add(new Genre("death-metal"));
     genres.add(new Genre("deep-house"));
     genres.add(new Genre("detroit-techno"));
     genres.add(new Genre("disco"));
     genres.add(new Genre("disney"));
     genres.add(new Genre("drum-and-bass"));
     genres.add(new Genre("dub"));
     genres.add(new Genre("dubstep"));
     genres.add(new Genre("edm"));
     genres.add(new Genre("electro"));
     genres.add(new Genre("electronic"));
     genres.add(new Genre("emo"));
     genres.add(new Genre("folk"));
     genres.add(new Genre("forro"));
     genres.add(new Genre("french"));
     genres.add(new Genre("funk"));
     genres.add(new Genre("garage"));
     genres.add(new Genre("german"));
     genres.add(new Genre("gospel"));
     genres.add(new Genre("goth"));
     genres.add(new Genre("grindcore"));
     genres.add(new Genre("groove"));
     genres.add(new Genre("grunge"));
     genres.add(new Genre("guitar"));
     genres.add(new Genre("happy"));
     genres.add(new Genre("hard-rock"));
     genres.add(new Genre("hardcore"));
     genres.add(new Genre("hardstyle"));
     genres.add(new Genre("heavy-metal"));
     genres.add(new Genre("hip-hop"));
     genres.add(new Genre("holidays"));
     genres.add(new Genre("honky-tonk"));
     genres.add(new Genre("house"));
     genres.add(new Genre("idm"));
     genres.add(new Genre("indian"));
     genres.add(new Genre("indie"));
     genres.add(new Genre("indie-pop"));
     genres.add(new Genre("industrial"));
     genres.add(new Genre("iranian"));
     genres.add(new Genre("j-dance"));
     genres.add(new Genre("j-idol"));
     genres.add(new Genre("j-pop"));
     genres.add(new Genre("j-rock"));
     genres.add(new Genre("jazz"));
     genres.add(new Genre("k-pop"));
     genres.add(new Genre("kids"));
     genres.add(new Genre("latin"));
     genres.add(new Genre("latino"));
     genres.add(new Genre("malay"));
     genres.add(new Genre("mandopop"));
     genres.add(new Genre("metal"));
     genres.add(new Genre("metal-misc"));
     genres.add(new Genre("metalcore"));
     genres.add(new Genre("minimal-techno"));
     genres.add(new Genre("movies"));
     genres.add(new Genre("mbp"));
     genres.add(new Genre("new-age"));
     genres.add(new Genre("new-release"));
     genres.add(new Genre("opera"));
     genres.add(new Genre("pagode"));
     genres.add(new Genre("party"));
     genres.add(new Genre("philippines-opm"));
     genres.add(new Genre("piano"));
     genres.add(new Genre("pop"));
     genres.add(new Genre("pop-film"));
     genres.add(new Genre("post-dubstep"));
     genres.add(new Genre("power-pop"));
     genres.add(new Genre("progressive-house"));
     genres.add(new Genre("psych-rock"));
     genres.add(new Genre("punk"));
     genres.add(new Genre("punk-rock"));
     genres.add(new Genre("r-n-b"));
     genres.add(new Genre("rainy-day"));
     genres.add(new Genre("rap"));
     genres.add(new Genre("reggae"));
     genres.add(new Genre("reggaeton"));
     genres.add(new Genre("road-trip"));
     genres.add(new Genre("rock"));
     genres.add(new Genre("rock-n-roll"));
     genres.add(new Genre("rockability"));
     genres.add(new Genre("romance"));
     genres.add(new Genre("sad"));
     genres.add(new Genre("salsa"));
     genres.add(new Genre("samba"));
     genres.add(new Genre("sertanejo"));
     genres.add(new Genre("show-tunes"));
     genres.add(new Genre("singer-songwriter"));
     genres.add(new Genre("ska"));
     genres.add(new Genre("sleep"));
     genres.add(new Genre("songwriter"));
     genres.add(new Genre("soul"));
     genres.add(new Genre("soundtracks"));
     genres.add(new Genre("spanish"));
     genres.add(new Genre("study"));
     genres.add(new Genre("summer"));
     genres.add(new Genre("swedish"));
     genres.add(new Genre("synth-pop"));
     genres.add(new Genre("tango"));
     genres.add(new Genre("techno"));
     genres.add(new Genre("trance"));
     genres.add(new Genre("trip-hop"));
     genres.add(new Genre("turkish"));
     genres.add(new Genre("work-out"));
     genres.add(new Genre("world-music"));
      em.getTransaction().begin();
      for (Genre genre:genres) {
          em.persist(genre);
      }

      em.getTransaction().commit();

  }
  public static void main(String[] args) {
    populateUsers();
  }

}
