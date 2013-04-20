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

public class RegisterHomeFragment extends Fragment {

	private ListView mRegisterOptions;
	private OnRegisterItemSelectedListener mCallback;
	
	public interface OnRegisterItemSelectedListener {
		public void onItemSelected (int pos);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnRegisterItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
	}

	private OnItemClickListener mItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {
			mCallback.onItemSelected(pos);
		}		
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_home, null);		
		mRegisterOptions = (ListView) view.findViewById(R.id.register_options_list);
		mRegisterOptions.setOnItemClickListener(mItemClickListener);
		return view;
	}

}
