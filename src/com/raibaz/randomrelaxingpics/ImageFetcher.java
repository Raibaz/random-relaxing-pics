package com.raibaz.randomrelaxingpics;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class ImageFetcher extends AsyncTask<Void, Void, String> {

	private static final String FETCH_URL = "http://randompics-server.appspot.com/";
	protected Exception e;	
	
	protected ProgressDialog progress;
							
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		this.progress.setMessage("Loading...");
		this.progress.show();
	}


	public String doInBackground(Void... voids) {
		
		try {
						
			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000);
			client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000);
			HttpGet get = new HttpGet(FETCH_URL);
			HttpResponse resp = client.execute(get);
			HttpEntity entity = resp.getEntity();
			
			String response = EntityUtils.toString(entity);
					
			Log.i("ImageFetcher", response);
			
			String imageUrl = extractUrlFromResponse(response);
			
			return Utils.buildHtmlWithImg(imageUrl);
			
		} catch (Exception e) {			
			e.printStackTrace();
			this.e = e;
			return "";
		}				
				
	}
				

	private static String extractUrlFromResponse(String response) {
		try {
			JSONArray arr = new JSONArray(response);
			JSONObject firstElem = (JSONObject)arr.get(0);
			return firstElem.getString("Url");
		} catch (JSONException je) {
			je.printStackTrace();
			return "";
		}
	}
	
}
