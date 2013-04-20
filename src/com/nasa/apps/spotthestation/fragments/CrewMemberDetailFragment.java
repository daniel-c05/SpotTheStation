package com.nasa.apps.spotthestation.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nasa.apps.spotthestation.R;

public class CrewMemberDetailFragment extends Fragment {
	
	TextView mMainInfo, mSpaceFlightTraining;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.crew_member_bio, null);		
		mMainInfo = (TextView) view.findViewById(R.id.main_description);
		mSpaceFlightTraining = (TextView) view.findViewById(R.id.spacelight_training);
		
		mMainInfo.setText(Html.fromHtml(getString(R.string.dummy_main_crew_member_info)));
		mSpaceFlightTraining.setText(Html.fromHtml(getString(R.string.dummy_spacelight_training)));
		
		return view;
	}
}
