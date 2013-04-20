package com.nasa.apps.spotthestation.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter.CursorToStringConverter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;

import com.nasa.apps.spotthestation.Constants;
import com.nasa.apps.spotthestation.R;
import com.nasa.apps.spotthestation.SignUpActivity;
import com.nasa.apps.spotthestation.database.CountryDbManager;
import com.nasa.apps.spotthestation.interfaces.onReviewItemEvent;

public class RegisterPageOneFragment extends Fragment {

	private static final int PAGE_NUM = 1;
	public static final String EXTRA_COUNTRY = "country";
	public static final String EXTRA_CITY = "city";
	public static final String EXTRA_REGION = "region";
	
	private Spinner mCountry, mRegion, mCity;	
	private SimpleCursorAdapter mCountryAdapter, mCityAdapter, mRegionsAdapter;
	private String mSelectedCountry, mSelectedRegion, mSelectedCity;

	private static final String FLAG_NO_CITY = "Select City";

	private static final String [] FROM_COUNTRY = {
		"country"
	};

	private static final int [] TO_COUNTRY = {
		android.R.id.text1
	};

	private static final int DROPDOWN_RES = android.R.layout.simple_spinner_item;

	private static final String [] FROM_REGION = {
		"region"
	};

	private static final String [] FROM_CITY = {
		"city"
	};

	private class SimpleCursorToStringConverter implements CursorToStringConverter {

		String columnName;

		public SimpleCursorToStringConverter (String name) {
			this.columnName = name;
		}

		@Override
		public CharSequence convertToString(Cursor cursor) {
			return cursor.getString(cursor.getColumnIndex(columnName));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_one, null);

		mCountryAdapter = new SimpleCursorAdapter(getActivity(), DROPDOWN_RES, CountryDbManager.getCountries(getActivity()), FROM_COUNTRY, TO_COUNTRY, SimpleCursorAdapter.NO_SELECTION);
		mCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCountryAdapter.setCursorToStringConverter(new SimpleCursorToStringConverter("country"));	
		
		mRegionsAdapter = new SimpleCursorAdapter(getActivity(), DROPDOWN_RES, CountryDbManager.getRegionsForCountry(getActivity(), ""), 
				FROM_REGION, TO_COUNTRY, SimpleCursorAdapter.NO_SELECTION);
		mRegionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mRegionsAdapter.setCursorToStringConverter(new SimpleCursorToStringConverter("region"));

		mCityAdapter = new SimpleCursorAdapter(getActivity(), DROPDOWN_RES, CountryDbManager.getCitiesForCountryAndRegion(getActivity(), "", ""), 
				FROM_CITY, TO_COUNTRY, SimpleCursorAdapter.NO_SELECTION);
		mCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		mCityAdapter.setCursorToStringConverter(new SimpleCursorToStringConverter("city"));


		mCountry = (Spinner) view.findViewById(R.id.country_selector);
		mRegion = (Spinner) view.findViewById(R.id.region_selector);
		mCity = (Spinner) view.findViewById(R.id.city_selector);
		
		mCountry.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int pos, long arg3) {	
				mSelectedCountry = mCountryAdapter.getCursorToStringConverter().convertToString((Cursor) mCountry.getSelectedItem()).toString();
				updateRegionSpinner(mSelectedCountry);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		mRegion.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mSelectedRegion = mRegionsAdapter.getCursorToStringConverter().convertToString((Cursor) mRegion.getSelectedItem()).toString();
				updateCitySpinner(mSelectedCountry, mSelectedRegion);				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		mCity.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				mSelectedCity = mCityAdapter.getCursorToStringConverter().convertToString((Cursor) mCity.getSelectedItem()).toString();
				boolean allowNextPage = true;
				if (mSelectedCity.contains(FLAG_NO_CITY)) {
					//If no city is selected yet, only the default one, or the default one is set again. 
					allowNextPage = false;
				}
				onReviewItemEvent activity = (onReviewItemEvent) getActivity();
				activity.onFlagChanged(PAGE_NUM, allowNextPage);			
				Bundle formData = new Bundle();
				formData.putString(EXTRA_COUNTRY, mSelectedCountry);
				formData.putString(EXTRA_CITY, mSelectedCity);
				formData.putString(EXTRA_REGION, mSelectedRegion);
				activity.onFormDataChanged(SignUpActivity.ITEM_LOCATION, formData);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}

		});

		mCountry.setAdapter(mCountryAdapter);
		mRegion.setAdapter(mRegionsAdapter);
		mCity.setAdapter(mCityAdapter);

		return view;
	}

	protected void updateCitySpinner(String mCountry, String mRegion) {
		Constants.logMessage("Item selected: " + mRegion);
		mCityAdapter.swapCursor(CountryDbManager.getCitiesForCountryAndRegion(getActivity(), mCountry, mRegion));
		mCityAdapter.notifyDataSetChanged();
	}

	protected void updateRegionSpinner(String item) {
		Constants.logMessage("Item selected: " + item);
		mRegionsAdapter.swapCursor(CountryDbManager.getRegionsForCountry(getActivity(), item));
		mRegionsAdapter.notifyDataSetChanged();		
	}

}
