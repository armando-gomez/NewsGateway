package com.armandogomez.newsgateway;

public class Source {

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
}
