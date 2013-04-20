package com.nasa.apps.spotthestation.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.nasa.apps.spotthestation.Constants;
import com.nasa.apps.spotthestation.R;
import com.nasa.apps.spotthestation.SignUpActivity;
import com.nasa.apps.spotthestation.interfaces.onReviewItemEvent;

public class RegisterPageThreeFragment extends Fragment {

	private static final int PAGE_NUM = 3;

	private TextView mLocation, mEmail;
	private CheckBox mCheckBox1, mCheckBox2;
	private boolean mIsCheckOneDone, mIsCheckTwoDone, mAllowNextStep;

	private SharedPreferences mPreferences;

	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			switch (buttonView.getId()) {
			case R.id.review_check_one:
				mIsCheckOneDone = isChecked;
				break;
			case R.id.review_check_two:
				mIsCheckTwoDone = isChecked;
				break;
			default:
				break;
			}

			if (mIsCheckOneDone && mIsCheckTwoDone) {
				mAllowNextStep = true;
			}
			else {
				mAllowNextStep = false;
			}
			onReviewItemEvent activity = (onReviewItemEvent) getActivity();
			activity.onFlagChanged(PAGE_NUM, mAllowNextStep);	 
		}
	};

	private OnClickListener mOnReviewItemClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int what = -1;
			switch (v.getId()) {
			case R.id.review_email:
				what = SignUpActivity.ITEM_EMAIL;
				break;
			case R.id.review_location:
				what = SignUpActivity.ITEM_LOCATION;
				break;
			default:
				break;
			}
			onReviewItemEvent activity = (onReviewItemEvent) getActivity();
			activity.onReviewItem(what);
		}
	};

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mPreferences = getActivity().getSharedPreferences(SignUpActivity.PREFS_REGISTRATION, Context.MODE_PRIVATE);
	};

	private void updatePrefDependentValues() {
		mEmail.setText(mPreferences.getString(RegisterPageTwoFragment.EXTRA_EMAIL, ""));

		String country = mPreferences.getString(RegisterPageOneFragment.EXTRA_COUNTRY, "");
		String region = mPreferences.getString(RegisterPageOneFragment.EXTRA_REGION, "");
		String city = mPreferences.getString(RegisterPageOneFragment.EXTRA_CITY, "");

		mLocation.setText(country + ", " + region + ", " + city);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_register_three, null);	

		mLocation = (TextView) view.findViewById(R.id.review_location);
		mEmail = (TextView) view.findViewById(R.id.review_email);
		mCheckBox1 = (CheckBox) view.findViewById(R.id.review_check_one);
		mCheckBox2 = (CheckBox) view.findViewById(R.id.review_check_two);

		mLocation.setOnClickListener(mOnReviewItemClickListener);
		mEmail.setOnClickListener(mOnReviewItemClickListener);
		mCheckBox1.setOnCheckedChangeListener(mCheckedChangeListener);
		mCheckBox2.setOnCheckedChangeListener(mCheckedChangeListener);

		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//Call during onResume in case the values were modified. 
		Constants.logMessage("Updating pref dependent stuff");
		updatePrefDependentValues();
	}
	
}
