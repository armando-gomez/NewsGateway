package com.armandogomez.newsgateway;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

public class ArticleFragment extends Fragment {

	public ArticleFragment(){}

	public static ArticleFragment newInstance(Article article, int index, int max) {
		ArticleFragment fragment = new ArticleFragment();
		Bundle bundle = new Bundle(1);
		bundle.putSerializable("ARTICLE_DATA", article);
		bundle.putSerializable("INDEX", index);
		bundle.putSerializable("TOTAL_COUNT", max);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);

		if(getArguments() == null) {
			return fragment_layout;
		}

		final Article currArticle = (Article) getArguments().getSerializable("ARTICLE_DATA");

		if(currArticle == null) {
			return fragment_layout;
		}

		int index = getArguments().getInt("INDEX");
		int total = getArguments().getInt("TOTAL_COUNT");

		TextView headline = fragment_layout.findViewById(R.id.article_headline);
		if(currArticle.getTitle() == null) {
			headline.setVisibility(View.GONE);
		} else {
			headline.setText(currArticle.getTitle());
		}

		TextView date_view = fragment_layout.findViewById(R.id.article_date);
		if(currArticle.getPublishedAt() == null) {
			date_view.setVisibility(View.GONE);
		} else {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("LLL dd, yyyy HH:mm");
			try {
				Date d = Date.from(Instant.parse(currArticle.getPublishedAt()));
				date_view.setText(simpleDateFormat.format(d).toString());
			} catch (Exception e) {
				e.printStackTrace();
				date_view.setVisibility(View.GONE);
			}
		}

		TextView author = fragment_layout.findViewById(R.id.article_authors);
		if(currArticle.getAuthor() == null) {
			author.setVisibility(View.GONE);
		} else {
			author.setText(currArticle.getAuthor());
		}

		ImageView article_image = fragment_layout.findViewById(R.id.article_image);
		article_image.setImageBitmap(currArticle.getImage());

		TextView article_text = fragment_layout.findViewById(R.id.article_text);
		if(currArticle.getDescription() == null) {
			article_text.setVisibility(View.GONE);
		} else {
			article_text.setText(currArticle.getDescription());
		}

		TextView article_count = fragment_layout.findViewById(R.id.article_count);
		article_count.setText(index + " of " + total);

		article_image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				visitArticle(currArticle.getUrl());
			}
		});

		headline.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				visitArticle(currArticle.getUrl());
			}
		});

		article_text.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				visitArticle(currArticle.getUrl());
			}
		});

		return fragment_layout;
	}

	public void visitArticle(String url) {
		Uri articleUri = Uri.parse(url);

		Intent intent = new Intent(Intent.ACTION_VIEW, articleUri);
		startActivity(intent);
	}
}
