package com.nasa.apps.spotthestation.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.nasa.apps.spotthestation.R;
import com.nasa.apps.spotthestation.adapters.NewsAdapter;
import com.nasa.apps.spotthestation.utils.HtmlHelper;

public class NewsFeedFragment extends Fragment {
	
	private static final String NASA_FEED = "http://www.nasa.gov/mission_pages/station/main/index.html";
	
	public interface OnNewsArticleSelected {
		public void onNewsArticleSelected (int pos, String identifier);
	}
	
	NewsAdapter mAdapter;
	ListView mListView;
	
	private OnNewsArticleSelected mCallback;
	
	
	@Override
	public void onAttach(Activity activity) {
		
		mAdapter = HtmlHelper.getNewsArticleAdapter(activity, NASA_FEED);
		
		activity.getWindow().setBackgroundDrawableResource(R.color.bg_light_gray);
		try {
			mCallback = (OnNewsArticleSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNewsArticleSelected");
        }
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
	 
		View view = inflater.inflate(R.layout.news_list, null);
		
		mListView = (ListView) view.findViewById(android.R.id.list);
		mListView.setEmptyView(view.findViewById(android.R.id.empty));
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mCallback.onNewsArticleSelected(arg2, (String)mAdapter.getItem(arg2));
			}			
		});
		
		return view;
	}
	
}
