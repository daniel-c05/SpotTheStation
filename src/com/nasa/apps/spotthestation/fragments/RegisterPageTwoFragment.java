package com.nasa.apps.spotthestation.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.nasa.apps.spotthestation.Constants;
import com.nasa.apps.spotthestation.R;
import com.nasa.apps.spotthestation.SignUpActivity;
import com.nasa.apps.spotthestation.interfaces.onReviewItemEvent;

public class RegisterPageTwoFragment extends Fragment implements OnEditorActionListener {

	private static final int PAGE_NUM = 2;
	private static final String FLAG_REQ_CHAR = "@";
	public static final String EXTRA_EMAIL = "email";

	TextView mLocation, mSmsAlerts;
	AutoCompleteTextView mEmailInput;
	EditText mConfirmEmail;
	private RadioGroup mRadioGroup;
	private int mRadioSelected = 0;

	private final TextWatcher watcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {	
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {	
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			String email = mEmailInput.getText().toString();
			if (s.toString().contains(FLAG_REQ_CHAR) && s.toString().equals(email)) {
				//put the email in a bundle
				Bundle formData = new Bundle();
				formData.putString(EXTRA_EMAIL, email);

				onReviewItemEvent activity = (onReviewItemEvent) getActivity();
				//Notify the activity that the cut off page on the view pager should be updated
				activity.onFlagChanged(PAGE_NUM, true);
				//Notify the activity what the new email is
				activity.onFormDataChanged(SignUpActivity.ITEM_EMAIL, formData);
			}
			else {
				onReviewItemEvent activity = (onReviewItemEvent) getActivity();
				activity.onFlagChanged(PAGE_NUM, false);
			}
		}
	};

	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			mRadioSelected = checkedId;
		}
	};
	
	@Override
	public void onDestroy() {
		if (mConfirmEmail != null) {
			mConfirmEmail.removeTextChangedListener(watcher);
		}		
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_two, null);

		mLocation = (TextView) view.findViewById(R.id.register_title_two);
		mSmsAlerts = (TextView) view.findViewById(R.id.register_sms_alerts);
		mEmailInput = (AutoCompleteTextView) view.findViewById(R.id.register_email);
		mConfirmEmail = (EditText) view.findViewById(R.id.register_confirm_email);
		mRadioGroup = (RadioGroup) view.findViewById(R.id.register_alert_group);

		mRadioGroup.setOnCheckedChangeListener(mCheckedChangeListener);

		((RadioButton) view.findViewById(R.id.register_alert_both)).setChecked(true);

		mConfirmEmail.addTextChangedListener(watcher);

		mSmsAlerts.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showSmsInfoDialog();
			}
		});

		return view;
	}

	protected void showSmsInfoDialog() {
		final Context mContext = getActivity();
		AlertDialog.Builder mBuilder = new Builder(mContext);
		TextView mContent = new TextView(mContext);
		mContent.setPadding(16, 16, 16, 16);		
		mContent.setText(Html.fromHtml(getString(R.string.sms_alerts_long)));
		ScrollView mScrollView = new ScrollView(mContext);
		LayoutParams mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
		mScrollView.setLayoutParams(mParams);
		mScrollView.addView(mContent);
		mBuilder.setView(mScrollView)
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		mBuilder.create().show();
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (EditorInfo.IME_ACTION_NEXT == actionId) {
			moveToNextEditText();
			return true;
		}
		else if (EditorInfo.IME_ACTION_DONE == actionId) {
			hideSoftKeyInput();
			return true;
		}
		return false;
	}

	private void hideSoftKeyInput() {
			
	}

	private void moveToNextEditText() {
		Constants.logMessage("Moving to next input");
		mEmailInput.requestFocus();
		mConfirmEmail.requestFocus();
	}

}
