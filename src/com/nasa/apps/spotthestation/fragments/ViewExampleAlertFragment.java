package com.nasa.apps.spotthestation.fragments;

import uk.co.senab.photoview.PhotoViewAttacher;

import com.nasa.apps.spotthestation.R;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewExampleAlertFragment extends Fragment {
	
	OnClickListener mOnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.right_button:
				showExampleImage();
				break;
			default:
				break;
			}
		}
	};
	
	PhotoViewAttacher mAttacher;	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_example_alert, null);
		TextView mContent = (TextView) view.findViewById(R.id.example_content);
		TextView mHeader = (TextView) view.findViewById(R.id.example_header);
		
		Button mRightButton = (Button) view.findViewById(R.id.right_button);
		
		mRightButton.setText(getString(R.string.show_me));
				
		mRightButton.setOnClickListener(mOnClickListener);
		
		mContent.setText(Html.fromHtml(getString(R.string.example_info)));
		mHeader.setText(Html.fromHtml(getString(R.string.example_title)));
		return view;
	}

	protected void showExampleImage() {
		
		ImageView mImageView = new ImageView(getActivity());
		mImageView.setImageResource(R.drawable.example_alert_image);
		mAttacher = new PhotoViewAttacher(mImageView);
		
		AlertDialog.Builder mBuilder = new Builder(getActivity());
		mBuilder.setTitle(getString(R.string.visualization))
		.setView(mImageView)
		.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();		
			}
		})
		.create().show();		
	}
	
}
