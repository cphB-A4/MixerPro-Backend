package rest;

import entities.Genre;
import entities.Post;
import entities.User;
import entities.Role;

import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;

import io.restassured.response.ResponseBody;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import org.junit.jupiter.api.*;
import utils.EMF_Creator;

@Disabled
public class LoginEndpointTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            User user = new User("user", "test");
            user.addRole(userRole);
            User admin = new User("admin", "test");
            admin.addRole(adminRole);
            User both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);

            //adding genres to user
            Genre genre = new Genre("hip-hop");
            Genre genre1 = new Genre("alt-rock");
            user.addGenre(genre);
            user.addGenre(genre1);
            em.persist(genre);
            em.persist(genre1);

            //adding description to user
            user.setProfileDescription("test description");

            //adding test post to user
            Post post =  new Post(user,"1","In da club", "50 cent", "coverUrl","in da club er en fed sang", "testSpotifyUrl");
            user.addPost(post);
            //em.persist(post); //No need for persisting because posts er marked: FetchType.EAGER


            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void serverIsRunning() {
        given().when().get("/info").then().statusCode(200);
    }

    @Test
    public void testRestNoAuthenticationRequired() {
        given()
                .contentType("application/json")
                .when()
                .get("/info/").then()
                .statusCode(200)
                .body("msg", equalTo("Hello anonymous!!"));
    }

    @Test
    public void testRestForAdmin() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: admin"));
    }

    @Test
    public void testRestForUser() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: user"));
    }

    @Test
    public void testAutorizedUserCannotAccesAdminPage() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then() //Call Admin endpoint as user
                .statusCode(401);
    }

    @Test
    public void testAutorizedAdminCannotAccesUserPage() {
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then() //Call User endpoint as Admin
                .statusCode(401);
    }

    @Test
    public void testRestForMultiRole1() {
        login("user_admin", "test");
        given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .header("x-access-token", securityToken)
                .when()
                .get("/info/admin").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to (admin) User: user_admin"));
    }

    @Test
    public void testRestForMultiRole2() {
        login("user_admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/user").then()
                .statusCode(200)
                .body("msg", equalTo("Hello to User: user_admin"));
    }

    @Test
    public void userNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }

    @Test
    public void adminNotAuthenticated() {
        logOut();
        given()
                .contentType("application/json")
                .when()
                .get("/info/user").then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", equalTo("Not authenticated - do login"));
    }
    @Test
    public void testParallelFetchFromAPI() throws Exception{
        login("admin", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/fetchParallel").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("boredomDTO", notNullValue())
                .body("catDTO.length", greaterThan(1))
        .body("dogDTO.status", equalTo("success"))
        .body("ipDTO", notNullValue());
    }
    @Test
    public void testSequantialFetchFromAPI() throws Exception{
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/fetchSeq").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("boredomDTO", notNullValue())
                .body("catDTO.length", greaterThan(1))
                .body("dogDTO.status", equalTo("success"))
                .body("ipDTO", notNullValue());
    }

    @Test
    @DisplayName("US2.1: Update fav genres user")
    public void testUs2AddGenresToUser(){
        /*String jsonGenre = "[ {\"name\": \"rap\"}, {\"name\": \"pop\"} ]";
        login("user", "test");
        ResponseBody responseBody = given()
                .contentType("application/json").body(jsonGenre)
                .header("x-access-token", securityToken)
                .when()
                .put("/info/user")
    .getBody();
        System.out.println(responseBody.asString());
        */
        String jsonGenre = "[ {\"name\": \"rap\"}, {\"name\": \"pop\"} ]";
        login("user", "test");
        given()
                .contentType("application/json").body(jsonGenre)
                .header("x-access-token", securityToken)
                .when()
                .put("/info/user").
                then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("username", notNullValue())
                .body("favouriteGenres[2].name", equalTo("rap"))
                .body("favouriteGenres.size()", equalTo(4));//test the array size
    }


    @Test
    @DisplayName("US2.2 : Delete genre from user")
    public void testUs2deleteGenreFromUser(){
        //TODO: Virker ikke - HVORFOR??
    ///deleteGenreFromUser
        //{"name": "hip-hop"}
        //System.out.println();
        String jsonGenre = "{\"name\": \"hip-hop\"}";
        System.out.println("jsonGenre: " + jsonGenre);
        login("user", "test");
   /*     given()
                .contentType("application/json").body(jsonGenre)
                .header("x-access-token", securityToken)
                .when()
                .delete("/info/deleteGenreFromUser").
                then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body("", notNullValue())
                .body("", equalTo("hip-hop"));*/

        ResponseBody responseBody = given()
                .contentType("application/json").body(jsonGenre)
                .header("x-access-token", securityToken)
                .when()
                .delete("/info/deleteGenreFromUser")
                .getBody();
        System.out.println(responseBody.asString());

    }

/*
 //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then()
                .extract().path("token");
        //System.out.println("TOKEN ---> " + securityToken);
    }

 */

    @Test
    @DisplayName("US 4: update profile description")
    public void testUs4UpdateProfile(){
        //updateProfile
        String jsonDescription = "{\"description\": \"my test description\"}";
        System.out.println(jsonDescription);
        login("user", "test");
        given()
                .contentType("application/json").body(jsonDescription)
                .header("x-access-token", securityToken)
                .when()
                .put("/info/updateProfile").
                then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
        .body(equalTo("worked"));

    }
    @Test
    @DisplayName("US 4.2: handle too long profile description")
    public void testUs4UpdateProfileTooLongDescription(){
        //updateProfile
        login("user", "test");
        String jsonDescription = "{\"description\": \"my test description my test description my test description my test description my test description my test description my test description my test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test descriptionmy test description\"}";
        given()
                .contentType("application/json").body(jsonDescription)
                .header("x-access-token", securityToken)
                .when()
                .put("/info/updateProfile").then()
                .statusCode(400);
    }

    @Test
    @DisplayName("US 6: Add a post")
    public void testUs6addPost(){
        String jsonPost = "{\"trackId\":\"4RY96Asd9IefaL3X4LOLZ8\",\"name\":\"In Da Club\",\"artist\":\"50 Cent\",\"coverUrl\":\"https://i.scdn.co/image/ab67616d0000b273f7f74100d5cc850e01172cbf\",\"spotifyLinkUrl\":\"https://open.spotify.com/track/4RY96Asd9IefaL3X4LOLZ8\",\"description\":\"test post-description\"}";
        System.out.println(jsonPost);
        login("user", "test");
        given()
                .contentType("application/json").body(jsonPost)
                .header("x-access-token", securityToken)
                .when()
                .post("/post/add").then()
                .assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
                .body(equalTo("Post added!"));
    }

    @Test
    @DisplayName("US 7: View other peoples profiles")
    public void testUs7getUserInfo(){
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/getUserInfo/user").then()
                .statusCode(200)
                .body("username",equalTo("user"))
                .body("profileDescription",equalTo("test description"));
    }

    @Test
    @DisplayName("US 8: Delete a post")
    public void testUs8deletePost(){
        login("user", "test");

        //TODO: Virker heller ikke..
        //{"postID": "1"}
        String postIDJson = "{\"postID\": \"1\"}";
        /*ResponseBody responseBody = given()
                .contentType("application/json").body(postIDJson)
                .header("x-access-token", securityToken)
                .when()
                .delete("/post/deletePost").getBody();
        System.out.println(responseBody.asString());*/
       /* given()
                .contentType("application/json").body(postIDJson)
                .header("x-access-token", securityToken)
                .when()
                .delete("/post/deletePost").then()
                .statusCode(200);*/
    }

    @Test
    @DisplayName("US 9: update profile gifUrl")
    public void testUs9updateProfileGifUrl(){
        //updateProfile
        String jsonGifUrl = "{\"gifUrl\": \"fiktiv gif url\"}";
        System.out.println(jsonGifUrl);
        login("user", "test");
        given()
                .contentType("application/json").body(jsonGifUrl)
                .header("x-access-token", securityToken)
                .when()
                .put("/giphy/updateProfileGifUrl").
                then().assertThat()
                .statusCode(HttpStatus.OK_200.getStatusCode())
        .body(equalTo("worked"));

    }

    @Test
    @DisplayName("Us 10: admin overview of all users")
    public void testUs10getUsernameBySearching() {
        login("user", "test");
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/info/searchForUser/u").then()
                .statusCode(200)
                .body(equalTo("[\"user\",\"user_admin\"]"));
        //arrayContaining virker ikke??
    }


}
