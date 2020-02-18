package com.armandogomez.newsgateway;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Article implements Serializable {
	private String author;
	private String title;
	private String description;
	private String url;
	private Bitmap image;
	private String publishedAt;

	Article(String _author, String _title, String _description, String _url, Bitmap _image, String _publishedAt) {
		author = _author;
		title = _title;
		description = _description;
		url = _url;
		image = _image;
		publishedAt = _publishedAt;
	}

	public String getTitle() {
		return title;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public String getUrl() {
		return url;
	}

	public Bitmap getImage() {
		return image;
	}

	public String getPublishedAt() {
		return publishedAt;
	}
}
