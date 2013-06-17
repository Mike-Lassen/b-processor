package com.bprocessor.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.bprocessor.Sketch;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ModelClient {
	
    public int save(Sketch  model) throws Exception {
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
        int uid = Integer.valueOf(line);
        model.setUid(uid);
        return uid;
    }
    public Sketch get(long id) throws Exception {
    	URL url = new URL("http://localhost:8080/modelserver/models/" + String.valueOf(id));
    	HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
    	urlc.setRequestMethod("GET");
    	Sketch sketch = Persistence.unserialize(urlc.getInputStream());
        return sketch;
    }
    public void update(long id, Sketch model) throws Exception {
    	URL url = new URL("http://localhost:8080/modelserver/models/" + String.valueOf(id));
    	HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
    	urlc.setRequestProperty("Content-Type", "application/json");
        urlc.setDoOutput(true);
        urlc.setRequestMethod("PUT");
        urlc.setAllowUserInteraction(false);
        OutputStream output = urlc.getOutputStream();
        Persistence.serialize(model, output);
        output.close();
        int response = urlc.getResponseCode();
        String message = urlc.getResponseMessage();
        System.out.println("response " + response + " - " + message);
    }
    public void delete(long id) throws Exception {
    	URL url = new URL("http://localhost:8080/modelserver/models/" + String.valueOf(id));
    	HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
    	urlc.setRequestMethod("DELETE");
    	urlc.connect();
    	int response = urlc.getResponseCode();
        String message = urlc.getResponseMessage();
        System.out.println("response " + response + " - " + message);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Sketch> getAll() throws Exception {
    	URL url = new URL("http://localhost:8080/modelserver/models/");
    	HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
    	urlc.setRequestMethod("GET");
    	List<Sketch> sketches = new LinkedList<Sketch>();
        ObjectMapper mapper = new ObjectMapper();
        List<Map> infos = mapper.readValue(urlc.getInputStream(), List.class);
        for (Map object : infos) {
        	int id = (Integer) object.get("id");
        	Sketch sketch = get(id);
        	sketch.setUid(id);
        	sketches.add(sketch);
        }
    	return sketches;
    }
}
