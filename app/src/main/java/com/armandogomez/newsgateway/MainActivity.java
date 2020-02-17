package com.armandogomez.newsgateway;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	private Menu menu;

	private HashMap<String, ArrayList> topicsData = new HashMap<>();
	private HashMap<String, ArrayList> countriesData = new HashMap<>();
	private HashMap<String, ArrayList> languagesData = new HashMap<>();

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

		if(topicsData.isEmpty() && countriesData.isEmpty() && languagesData.isEmpty()) {
			new AsyncSourcesLoader(this).execute();
		}
	}

	public void dataDownloadFailed() {
		Toast.makeText(this, "Failed to download source data", Toast.LENGTH_LONG).show();
	}

	private void selectItem(int position) {
		Toast.makeText(this, "clicked on " + position, Toast.LENGTH_LONG).show();
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
					topicsData.put(category, new ArrayList<>());
				}
				topicsData.get(category).add(source);
			}

			if(language != null) {
				if(!languagesData.containsKey(language)) {
					languagesData.put(language, new ArrayList<>());
				}
				languagesData.get(language).add(source);
			}

			if(country != null) {
				if(!countriesData.containsKey(country)) {
					countriesData.put(country, new ArrayList<>());
				}
				countriesData.get(country).add(source);
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
	}
}
