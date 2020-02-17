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

		HashMap<String, HashSet<String>> sourcesMap = parseJSON(s);
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

	private HashMap<String, HashSet<String>> parseJSON(String s) {
		HashMap<String, HashSet<String>> sourcesMap = new HashMap<>();

		try {
			JSONObject jsonObject = new JSONObject(s);
			JSONArray jsonArray = jsonObject.getJSONArray("sources");

			for(int i=0; i < jsonArray.length(); i++) {
				JSONObject source = (JSONObject) jsonArray.get(i);
				String category = source.getString("category");
				String name = source.getString("name");

				if(sourcesMap.containsKey(category)) {
					sourcesMap.get(category).add(name);
				} else {
					sourcesMap.put(category, new HashSet<String>());
					sourcesMap.get(category).add(name);
				}
			}
			return sourcesMap;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
