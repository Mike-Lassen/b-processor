package com.bprocessor.io;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.bprocessor.Sketch;

public class ModelClient {

	public long save(Sketch  model) throws Exception {
		URL url = new URL("http://localhost:8080/modelserver/models/");
		URLConnection urlc = url.openConnection();
		urlc.setRequestProperty("Content-Type", "application/json");
		urlc.setDoOutput(true);
        urlc.setAllowUserInteraction(false);
        OutputStream output = urlc.getOutputStream();
        Persistence.serialize(model, output);
        output.close();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
        String line = null;
        while ((line=reader.readLine())!=null) {
            System.out.println(line);
        }
        reader.close();
		return 0;
	}
	public Sketch get(long id) {
		return null;
	}
	public void update(long id, Sketch model) {
		
	}
	public void delete(long id) {
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
