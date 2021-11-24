package utils;

import java.util.concurrent.Callable;

public class FetchCallable implements Callable<String>{

    private String url;

    public FetchCallable(String url) {
        this.url = url;
    }

    @Override
    public String call() throws Exception {
        String str = HttpUtils.fetchData(url);
        return str;

    }



}