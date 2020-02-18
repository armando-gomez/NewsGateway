package com.armandogomez.newsgateway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class DrawerSourceAdapter extends ArrayAdapter<Source> {

	private HashMap<String, Integer> colorMap = new HashMap<>();

	public DrawerSourceAdapter(Context context, int textViewResourceId, ArrayList<Source> items, HashMap<String, Integer> cm) {
		super(context, textViewResourceId, items);
		colorMap = cm;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View listItemView = convertView;
		if(listItemView == null) {
			listItemView = LayoutInflater.from(this.getContext()).inflate(R.layout.drawer_item, parent, false);
		}

		Source source = getItem(position);

		TextView nameTextView = (TextView) listItemView.findViewById(R.id.text_view);
		int color_id = colorMap.get(source.getCategory());

		nameTextView.setText(source.getName());
		nameTextView.setTextColor(color_id);

		return listItemView;
	}
}
