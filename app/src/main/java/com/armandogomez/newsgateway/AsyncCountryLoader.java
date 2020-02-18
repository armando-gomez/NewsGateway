package com.armandogomez.newsgateway;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

public class AsyncCountryLoader extends AsyncTask<String, Integer, String> {
	private static final String TAG = "AsyncCountryLoader";
	@SuppressLint("StaticFieldLeak")
	private MainActivity mainActivity;

	private static HashMap<String, String> countryMap = new HashMap<>();

	AsyncCountryLoader(MainActivity ma) {
		mainActivity = ma;
	}

	@Override
	protected void onPostExecute(String s) {
		if(s != null) {
			parseJSON(s);
		}
	}

	@Override
	protected String doInBackground(String... params) {
		InputStream is = mainActivity.getResources().openRawResource(R.raw.country_codes);
		Writer writer = new StringWriter();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			String line = reader.readLine();
			while(line != null) {
				writer.write(line);
				line = reader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return writer.toString();
	}

	private void parseJSON(String s) {
		try {
			JSONObject jsonObject = new JSONObject(s);
			JSONArray jsonArray = (JSONArray) jsonObject.get("countries");
			for(int i=0; i < jsonArray.length(); i++) {
				JSONObject country = (JSONObject) jsonArray.get(i);
				String code = country.getString("code");
				String name = country.getString("name");
				if(!name.isEmpty()) {
					countryMap.put(code, name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String getCountryName(String s) {
		return countryMap.get(s);
	}
}
