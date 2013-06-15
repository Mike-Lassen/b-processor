package com.bprocessor.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.bprocessor.Sketch;

public class ModelClient {

    public long save(Sketch  model) throws Exception {
        URL url = new URL("http://localhost:8080/modelserver/models/");
        HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
        urlc.setRequestProperty("Content-Type", "application/json");
        urlc.setDoOutput(true);
        urlc.setRequestMethod("POST");
        urlc.setAllowUserInteraction(false);
        OutputStream output = urlc.getOutputStream();
        Persistence.serialize(model, output);
        output.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        String line = reader.readLine();
        reader.close();
        return Long.valueOf(line);
    }
    public Sketch get(long id) throws Exception {
    	URL url = new URL("http://localhost:8080/modelserver/models/" + String.valueOf(id));
    	HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
    	urlc.setRequestMethod("GET");
    	BufferedReader reader = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        String line = reader.readLine();
        reader.close();
        System.out.println(line);
        return null;
    }
    public void update(long id, Sketch model) {

    }
    public void delete(long id) {

    }

}
