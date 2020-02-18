package com.armandogomez.newsgateway;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";

	private HashMap<String, String> countryMap = new HashMap<>();
	private HashMap<String, String> languageMap = new HashMap<>();

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private Menu menu;
	private ArticlePagerAdapter articlePagerAdapter;

	private HashMap<String, ArrayList<Source>> topicsData = new HashMap<>();
	private HashMap<String, ArrayList<Source>> countriesData = new HashMap<>();
	private HashMap<String, ArrayList<Source>> languagesData = new HashMap<>();

	private ArrayList<Source> currentSourcesDisplayed = new ArrayList<>();
	private String currentSubMenuCategory;

	private List<Fragment> fragments = new ArrayList<>();
	private ViewPager pager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		drawerLayout = findViewById(R.id.drawer_layout);
		drawerList = findViewById(R.id.drawer_list);

		drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectItem(position);
				drawerLayout.closeDrawer(drawerList);
			}
		});

		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
		currentSubMenuCategory = "Topics";

		articlePagerAdapter = new ArticlePagerAdapter(getSupportFragmentManager(), fragments);
		pager = findViewById(R.id.viewpager);
		pager.setAdapter(articlePagerAdapter);

		readStaticJSON();

		if(topicsData.isEmpty() && countriesData.isEmpty() && languagesData.isEmpty()) {
			new AsyncSourcesLoader(this).execute();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		drawerToggle.onConfigurationChanged(newConfig);
	}

	public void dataDownloadFailed() {
		Toast.makeText(this, "Failed to download source data", Toast.LENGTH_LONG).show();
	}

	private void selectItem(int position) {
		pager.setBackground(null);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		menu = m;
		return true;
	}

	public void setupSources(HashMap<String, Source> sourcesMap) {
		topicsData.clear();
		countriesData.clear();
		languagesData.clear();

		for(String source: sourcesMap.keySet()) {
			String category = sourcesMap.get(source).getCategory();
			String language = sourcesMap.get(source).getLanguage();
			String country = sourcesMap.get(source).getCountry();

			if(category != null) {
				if(!topicsData.containsKey(category)) {
					topicsData.put(category, new ArrayList<Source>());
				}
				topicsData.get(category).add(sourcesMap.get(source));
			}

			if(language != null) {
				String name = languageMap.get(language);
				if(!languagesData.containsKey(name)) {
					languagesData.put(name, new ArrayList<Source>());
				}
				languagesData.get(name).add(sourcesMap.get(source));
			}

			if(country != null) {
				String name = countryMap.get(country);
				if(!countriesData.containsKey(name)) {
					countriesData.put(name, new ArrayList<Source>());
				}
				countriesData.get(name).add(sourcesMap.get(source));
			}
		}

		ArrayList<String> topicsList = new ArrayList<>(topicsData.keySet());
		topicsList.add("all");
		Collections.sort(topicsList);
		SubMenu topicsMenu = menu.addSubMenu("Topics");
		for(String s: topicsList) {
			topicsMenu.add(s);
		}

		ArrayList<String> languagesList = new ArrayList<>(languagesData.keySet());
		languagesList.add("all");
		Collections.sort(languagesList);
		SubMenu languagesMenu = menu.addSubMenu("Languages");
		for(String s: languagesList) {
			languagesMenu.add(s);
		}

		ArrayList<String> countriesList = new ArrayList<>(countriesData.keySet());
		countriesList.add("all");
		Collections.sort(countriesList);
		SubMenu countriesMenu = menu.addSubMenu("Countries");
		for(String s: countriesList) {
			countriesMenu.add(s);
		}

		for(String s: topicsList) {
			if(!s.equals("all")) {
				ArrayList<Source> sourcesFromTopic = topicsData.get(s);
				currentSourcesDisplayed.addAll(sourcesFromTopic);
			}
		}

		Collections.sort(currentSourcesDisplayed);

		drawerList.setAdapter(new DrawerSourceAdapter(this, R.layout.drawer_item, currentSourcesDisplayed));

		setTitle(getResources().getString(R.string.app_name) + " (" + currentSourcesDisplayed.size() + ")");

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		if(drawerToggle.onOptionsItemSelected(item)) {
			Log.d(TAG, "onOptionsItemSelected: drawerToggle " + item);
			return true;
		}

		String selection = item.getTitle().toString();
		if(selection.equals("Topics") || selection.equals("Languages") || selection.equals("Countries")) {
			currentSubMenuCategory = selection;
			return super.onOptionsItemSelected(item);
		}

		currentSourcesDisplayed.clear();

		if(currentSubMenuCategory.equals("Topics")) {
			if(selection.equals("all")) {
				for(String s: topicsData.keySet()) {
					if(!s.equals("all")) {
						ArrayList<Source> sourcesFromTopic = topicsData.get(s);
						currentSourcesDisplayed.addAll(sourcesFromTopic);
					}
				}
			} else {
				ArrayList<Source> sourcesFromTopic = topicsData.get(selection);
				currentSourcesDisplayed.addAll(sourcesFromTopic);
			}
		} else if(currentSubMenuCategory.equals("Languages")) {
			if(selection.equals("all")) {
				for(String s: languagesData.keySet()) {
					if(!s.equals("all")) {
						ArrayList<Source> sourcesFromTopic = languagesData.get(s);
						currentSourcesDisplayed.addAll(sourcesFromTopic);
					}
				}
			} else {
				currentSourcesDisplayed.addAll(languagesData.get(selection));
			}
		} else if(currentSubMenuCategory.equals("Countries")) {
			if(selection.equals("all")) {
				for(String s: countriesData.keySet()) {
					if(!s.equals("all")) {
						ArrayList<Source> sourcesFromTopic = countriesData.get(s);
						currentSourcesDisplayed.addAll(sourcesFromTopic);
					}
				}
			} else {
				currentSourcesDisplayed.addAll(countriesData.get(selection));
			}
		}

		Collections.sort(currentSourcesDisplayed);
		((DrawerSourceAdapter) drawerList.getAdapter()).notifyDataSetChanged();
		setTitle(getResources().getString(R.string.app_name) + " (" + currentSourcesDisplayed.size() + ")");

		return super.onOptionsItemSelected(item);
	}

	private void readStaticJSON() {
		InputStream is = getResources().openRawResource(R.raw.country_codes);
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

		parseCountryJSON(writer.toString());

		is = getResources().openRawResource(R.raw.language_codes);
		writer = new StringWriter();
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

		parseLanguageJSON(writer.toString());
	}

	private void parseCountryJSON(String s) {
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

	private void parseLanguageJSON(String s) {
		try {
			JSONObject jsonObject = new JSONObject(s);
			JSONArray jsonArray = (JSONArray) jsonObject.get("languages");
			for(int i=0; i < jsonArray.length(); i++) {
				JSONObject country = (JSONObject) jsonArray.get(i);
				String code = country.getString("code");
				String name = country.getString("name");
				if(!name.isEmpty()) {
					languageMap.put(code, name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
