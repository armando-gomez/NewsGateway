package com.armandogomez.newsgateway;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Source implements Comparable<Source> {

	private String id;
	private String name;
	private String category;
	private String language;
	private String country;

	Source(String id, String name, String category, String language, String country) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.language = language;
		this.country = country;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getLanguage() {
		return language;
	}

	public String getCountry() {
		return country;
	}

	@Override
	public int compareTo(Source s) {
		return this.getName().compareTo(s.getName());
	}
}
