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

      Genre genre = new Genre("acoustic");
      Genre genre1 = new Genre("afrobeat");
      Genre genre2 = new Genre("alt-rock");
      Genre genre3 = new Genre("alternative");
      Genre genre4 = new Genre("ambient");
      em.getTransaction().begin();


              /*
              INSERT INTO GENRE VALUES ('acoustic');
INSERT INTO GENRE VALUES ('afrobeat');
INSERT INTO GENRE VALUES ('alt-rock');
INSERT INTO GENRE VALUES ('alternative');
INSERT INTO GENRE VALUES ('ambient');
               */


      em.persist(genre);
      em.persist(genre1);
      em.persist(genre2);
      em.persist(genre3);
      em.persist(genre4);

      em.getTransaction().commit();

  }
  public static void main(String[] args) {
    populateUsers();
  }

}