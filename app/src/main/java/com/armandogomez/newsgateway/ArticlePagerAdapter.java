package com.armandogomez.newsgateway;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ArticlePagerAdapter extends FragmentPagerAdapter {
	private long baseId = 0;
	private List<Fragment> fragments;

	ArticlePagerAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
	}

	@Override
	public int getItemPosition(@NonNull Object object) {
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public long getItemId(int position) {
		// give an ID different from position when position has been changed
		return baseId + position;
	}

	void notifyChangeInPosition(int n) {
		// shift the ID returned by getItemId outside the range of all previous fragments
		baseId += getCount() + n;
	}
}
