package facades;

import com.google.gson.*;
import privateKeys.PrivateApiKeys;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import static privateKeys.PrivateApiKeys.API_KEY_GIPHY;

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
        String url = "https://api.giphy.com/v1/gifs/trending" + API_KEY_GIPHY;

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

}
