package com.armandogomez.newsgateway;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncSourcesLoader extends AsyncTask<String, Integer, String> {
	private static final String TAG = "AsyncSourcesLoader";
	@SuppressLint("StaticFieldLeak")
	private MainActivity mainActivity;

	private static final String dataURL = "https://newsapi.org/v2/sources?apiKey=";
	private static final String apiKey = "501a6795c44b40028f89ca265b6de912";


	AsyncSourcesLoader(MainActivity ma) {
		mainActivity = ma;
	}

	@Override
	protected void onPostExecute(String s) {
		if(s == null) {
			mainActivity.dataDownloadFailed();
		}

		HashMap<String, Source> sourcesMap = parseJSON(s);
		if(sourcesMap != null) {
			mainActivity.setupSources(sourcesMap);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		Uri dataUri = Uri.parse(dataURL);
		String urlToUse = dataUri.toString() + apiKey;

		StringBuilder sb = new StringBuilder();
		try {
			URL url = new URL(urlToUse);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");

			Log.d(TAG, "doInBackground: " + conn.getResponseCode());

			if (conn.getResponseCode() == HTTP_OK) {
				InputStream is = conn.getInputStream();
				BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line).append('\n');
				}
			} else {
				return null;
			}
			Log.d(TAG, "doInBackground: " + sb.toString());

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return sb.toString();
	}

	private HashMap<String, Source> parseJSON(String s) {
		HashMap<String, Source> sourcesMap = new HashMap<>();

		try {
			JSONObject jsonObject = new JSONObject(s);
			JSONArray jsonArray = jsonObject.getJSONArray("sources");

			for(int i=0; i < jsonArray.length(); i++) {
				JSONObject sourceObject = (JSONObject) jsonArray.get(i);
				String id = sourceObject.getString("id");
				String name = sourceObject.getString("name");
				String category = sourceObject.getString("category");
				String language = sourceObject.getString("language");
				String country = sourceObject.getString("country");

				if(!sourcesMap.containsKey(id)) {
					sourcesMap.put(id, new Source(id, name, category, language, country));
				}
			}
			return sourcesMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
