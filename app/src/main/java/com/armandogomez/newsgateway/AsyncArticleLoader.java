package com.armandogomez.newsgateway;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import static java.net.HttpURLConnection.HTTP_OK;

public class AsyncArticleLoader extends AsyncTask<String, Integer, ArrayList<Article>>{

	private static final String TAG = "AsyncArticleLoader";
	@SuppressLint("StaticFieldLeak")
	private MainActivity mainActivity;
	private String selectedSource;
	private static HashMap<String, ArrayList<Article>> cachedArticles = new HashMap<>();

	private static final String dataURL = "https://newsapi.org/v2/top-headlines?";
	private static final String apiKey = "501a6795c44b40028f89ca265b6de912";

	AsyncArticleLoader(MainActivity ma) {
		mainActivity = ma;
	}

	@Override
	protected void onPostExecute(ArrayList<Article> articleList) {
		mainActivity.setArticles(articleList);
	}

	@Override
	protected ArrayList<Article> doInBackground(String... params) {
		selectedSource = params[0];

		if(cachedArticles.containsKey(selectedSource)) {
			return cachedArticles.get(selectedSource);
		}

		Uri dataUri = Uri.parse(dataURL);
		String urlToUse = dataUri.toString() + "sources=" + selectedSource + "&apiKey=" + apiKey;

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
		} catch(Exception e ) {
			e.printStackTrace();
			return null;
		}

		ArrayList<Article> articleList = parseJSON(sb.toString());
		cachedArticles.put(selectedSource, articleList);
		return articleList;
	}

	private ArrayList<Article> parseJSON(String s) {
		ArrayList<Article> articleList = new ArrayList<>();
		try {
			JSONObject jsonObject = new JSONObject(s);
			JSONArray jsonArray = jsonObject.getJSONArray("articles");

			for(int i=0; i < jsonArray.length(); i++) {
				JSONObject articleObj = (JSONObject) jsonArray.get(i);

				String author = articleObj.getString("author");
				String title = articleObj.getString("title");
				String description = articleObj.getString("description");
				String url = articleObj.getString("url");
				String urlToImage = articleObj.getString("urlToImage");
				String publishedAt = articleObj.getString("publishedAt");

				if(author.isEmpty() || author.equals("null")) {
					author = null;
				}

				if(title.isEmpty() || title.equals("null")) {
					title = null;
				}

				if(description.isEmpty() || description.equals("null")) {
					description = null;
				}

				if(url.isEmpty() || url.equals("null")) {
					url = null;
				}
				Bitmap bitmap = null;
				if(urlToImage.isEmpty() || urlToImage.equals("null")) {
					bitmap = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.brokenimage);
				} else {
					try {
						bitmap = BitmapFactory.decodeStream((InputStream) new URL(urlToImage).getContent());
					} catch (Exception e) {
						bitmap = BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.brokenimage);
					}
				}

				if(publishedAt.isEmpty() || publishedAt.equals("null")) {
					publishedAt = null;
				}

				articleList.add(new Article(author, title, description, url, bitmap, publishedAt));
			}

			return articleList;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
