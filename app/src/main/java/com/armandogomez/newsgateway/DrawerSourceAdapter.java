package com.armandogomez.newsgateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerSourceAdapter extends ArrayAdapter<Source> {

	public DrawerSourceAdapter(Context context, int textViewResourceId, ArrayList<Source> items) {
		super(context, textViewResourceId, items);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = convertView;
		if(listItemView == null) {
			listItemView = LayoutInflater.from(this.getContext()).inflate(R.layout.drawer_item, parent, false);
		}

		Source source = getItem(position);

		TextView nameTextView = (TextView) listItemView.findViewById(R.id.text_view);

		nameTextView.setText(source.getName());

		return listItemView;
	}
}
