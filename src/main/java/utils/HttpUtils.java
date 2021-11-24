package utils;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpUtils {

    public static String fetchAnySeq(String url1) throws IOException {
        URL url = new URL(url1);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
return "";
    }

    public static String fetchData(String _url) throws MalformedURLException, IOException {
        URL url = new URL(_url);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        //con.setRequestProperty("Accept", "application/json;charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("User-Agent", "server"); //remember if you are using SWAPI
        Scanner scan = new Scanner(con.getInputStream());
        String jsonStr = "";
        while (scan.hasNext()) {
            jsonStr += scan.nextLine();
        }
        scan.close();
        return jsonStr;
    }

    //Fra tutorne
    public static List<String> fetchMany(String[] urls) throws InterruptedException, WebApplicationException {

        List<Future<String>> futures = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(urls.length);

        for (String url : urls) {
            Callable<String> callable = new FetchCallable(url);
            Future<String> future = executorService.submit(callable);
            futures.add(future);
        }

        executorService.shutdown();

        List<String> list = new ArrayList<>();
        for (Future<String> fut : futures) {
            try {
                list.add(fut.get());
            } catch (ExecutionException ex) {
                Logger.getLogger(HttpUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return list;
    }
}
