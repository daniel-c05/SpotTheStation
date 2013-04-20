package com.nasa.apps.spotthestation.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nasa.apps.spotthestation.R;
import com.viewpagerindicator.TitlePageIndicator;

public class CrewInfoFragment extends Fragment {
	
	private OnCrewMemberSelected mCallback;
	private ViewPager mViewPager;
	private TitlePageIndicator mIndicator;
	LayoutInflater mInflater;
	Resources mResources;
	
	public interface OnCrewMemberSelected {
		public void onCrewMemberSelected (String name);
	}
	
	@Override
	public void onAttach(Activity activity) {
		mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResources = activity.getResources();		
		try {
			mCallback = (OnCrewMemberSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCrewMemberSelected");
        }
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.crew_info_container, null);		
		mViewPager = (ViewPager) view.findViewById(R.id.pager);
		mIndicator = (TitlePageIndicator) view.findViewById(R.id.pager_indicator);		
		mViewPager.setAdapter(new CrewPagerAdapter());
        mIndicator.setViewPager(mViewPager);
		return view;
	}
	
	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			mCallback.onCrewMemberSelected("blah");
		}		
	};
	
	class CrewPagerAdapter extends PagerAdapter {		

		private final String [] CREW_OPTIONS = {
			"Current",
			"Future",
			"Past"
		};
		
		private final int [] EXP_TITLES = {
				R.string.dummy_current_expedition,
				R.string.dummy_future_expedition,
				R.string.dummy_past_expedition
			};
		
		private final int [] MEMBER_ARRAY = {
				R.array.dummy_crew_current_members,
				R.array.dummy_crew_future_members,
				R.array.dummy_crew_past_members
			};

		@Override
		public int getCount() {
			return CREW_OPTIONS.length;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			return CREW_OPTIONS[position];
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {			
			LinearLayout mMainView = (LinearLayout) mInflater.inflate(R.layout.expedition_info_container, null);
			TextView expeditionInfo = (TextView) mMainView.findViewById(R.id.expedition_info);
			expeditionInfo.setText(Html.fromHtml(getString(EXP_TITLES[position])));
			ListView mListView = (ListView) mMainView.findViewById(android.R.id.list);
			mListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.crew_member_title, R.id.crew_member, mResources.getStringArray(MEMBER_ARRAY[position])));
			mListView.setOnItemClickListener(mOnItemClickListener);
			container.addView(mMainView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			return mMainView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
	
}
