package facades;

import com.google.gson.*;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.UserNotFoundException;
//import privateKeys.PrivateApiKeys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.*;

//import static privateKeys.PrivateApiKeys.API_KEY_GIPHY;

/**
 * @author lam@cphbusiness.dk
 */
public class GiphyFacade {

    private static EntityManagerFactory emf;
    private static GiphyFacade instance;

    private static HttpURLConnection con;

    private GiphyFacade() {
    }

    /**
     * @param _emf
     * @return the instance of this facade.
     */
    public static GiphyFacade getGiphyFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new GiphyFacade();
        }
        return instance;
    }

    public List<String> getTrendingGifs() throws MalformedURLException {
        String url = "https://api.giphy.com/v1/gifs/trending?api_key=" + System.getenv("GIPHY");
        System.out.println(System.getenv("GIPHY"));



        try {

            URL myurl = new URL(url);
            con = (HttpURLConnection) myurl.openConnection();

            con.setRequestMethod("GET");

            StringBuilder content;

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                String line;
                content = new StringBuilder();

                while ((line = in.readLine()) != null) {

                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }

            JsonObject json = JsonParser.parseString(content.toString()).getAsJsonObject();
            JsonArray dataArray = json.getAsJsonArray("data");
            List<String> imgUrlList = new ArrayList<>();

            int length = dataArray.size()-1;
            for (int i = 0; i < length; i++) {
               // System.out.println(dataArray.get(i));
                JsonObject data = (JsonObject) dataArray.get(i);
                JsonObject image = data.getAsJsonObject("images");
                JsonObject original = image.getAsJsonObject("original");
                String imgUrl = original.get("url").getAsString();
               // System.out.println(imgUrl);
                //creates variation
                long randomNumber = Math.round(Math.random());//create a random number 1 or 0
                if (randomNumber == 0){
                    imgUrlList.add(imgUrl);
                }

            }
           // return content.toString();
            return imgUrlList;

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            con.disconnect();
        }
        return null;
    }

    public String updateProfileGifUrl(String jsonGifUrl, String username) throws  API_Exception {
        EntityManager em = emf.createEntityManager();
        User user;
        String gifUrl;
        try {
            JsonObject json = JsonParser.parseString(jsonGifUrl).getAsJsonObject();
            gifUrl = json.get("gifUrl").getAsString();

        } catch (Exception e) {
            throw new API_Exception("Malformed JSON Suplied", 400, e);
        }
        try {

            user = em.find(User.class, username);
            user.setProfileGifUrl(gifUrl);

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



    public String getProfileGifUrlById(String username){
        EntityManager em = emf.createEntityManager();
        User user = em.find(User.class, username);
        String profileGifUrl = user.getProfileGifUrl();
        if (profileGifUrl == null){
            return "No profile gif url yet.";
        }
        System.out.println("getUserDescriptionById: " + profileGifUrl);
        return profileGifUrl;
    }

}
