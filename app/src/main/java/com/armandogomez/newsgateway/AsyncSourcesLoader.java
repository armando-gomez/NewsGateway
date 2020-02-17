package com.armandogomez.newsgateway;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

public class AsyncSourcesLoader extends AsyncTask<String, Integer, String> {
	private static final String TAG = "AsyncSourcesLoader";
	@SuppressLint("StaticFieldLeak")
	private MainActivity mainActivity;

	private static final String dataURL = "https://newsapi.org/v2/sources?apiKey=";
	private static final String apiKey = "";


	@Override
	protected void onPostExecute(String s) {

	}

	@Override
	protected String doInBackground(String... params) {
		return null;
	}
}
