package com.nasa.apps.spotthestation.fragments;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nasa.apps.spotthestation.Constants;
import com.nasa.apps.spotthestation.R;
import com.nasa.apps.spotthestation.SignUpActivity;
import com.nasa.apps.spotthestation.interfaces.OnRegisterButtonListener;

public class RegisterPageFourFragment extends Fragment {
	
	private String mRegisterCode, mEmailAddress;
	private EditText mEmailInput, mCodeInput;
	private Button mCancelButton, mCompleteButton;
	private OnRegisterButtonListener mCallback;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	/**
	 * Exact amount of digits each registration code has.
	 */
	private static final int REGISTER_DIGITS = 8;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_button:
				mCallback.onButtonClick(v.getId());
				break;
			case R.id.right_button:
				showConfirmDialog();
				break;
			default:
				break;
			}
		}
	};

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
			if (s.length() == REGISTER_DIGITS) {
				mCompleteButton.setEnabled(true);
			}
			else {
				mCompleteButton.setEnabled(false);
			}
		}
	};
	
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (OnRegisterButtonListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnRegisterButtonListener");
        }
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();
		if (args != null) {
			if (args.containsKey("url")) {
				mRegisterCode = args.getString("url");
			}
		}

		mPreferences = getActivity().getSharedPreferences(SignUpActivity.PREFS_REGISTRATION, Context.MODE_PRIVATE);
		mEmailAddress = mPreferences.getString(RegisterPageTwoFragment.EXTRA_EMAIL, "");		
	}

	@Override
	public void onDestroy() {
		if (mCodeInput != null) {
			mCodeInput.removeTextChangedListener(watcher);
		}		
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_register_four, null);	

		mEmailInput = (EditText) view.findViewById(R.id.confirm_email);
		mCodeInput = (EditText) view.findViewById(R.id.confirm_code);
		
		mCancelButton = (Button) view.findViewById(R.id.left_button);
		mCompleteButton = (Button) view.findViewById(R.id.right_button);

		mCancelButton.setOnClickListener(mOnClickListener);
		mCompleteButton.setOnClickListener(mOnClickListener);
		
		mCancelButton.setText(getString(R.string.cancel));
		mCompleteButton.setText(getString(R.string.complete));
		
		mCompleteButton.setEnabled(false);
		
		mCodeInput.addTextChangedListener(watcher);

		if (mRegisterCode != null) {			
			Constants.logMessage("Code was: " + mRegisterCode);
			if (mRegisterCode.contains("token=")) {
				mRegisterCode = mRegisterCode.substring(mRegisterCode.indexOf("=") + 1,mRegisterCode.length());
				mCodeInput.setText(mRegisterCode);
				view.findViewById(R.id.TextView2).setVisibility(View.GONE);
			}
			else {
				((TextView)view.findViewById(R.id.TextView1)).setText(getString(R.string.thanks_for_registering) + " " + mEmailAddress);
			}							
		}
		else {
			view.findViewById(R.id.TextView2).setVisibility(View.GONE);
		}

		if (mEmailAddress != null) {			
			mEmailInput.setText(mEmailAddress);
		}

		return view;
	}

	/**
	 * Show a dialog to confirm the submission of the entered data. 
	 */
	protected void showConfirmDialog() {

		if (mCodeInput.getText().length() != REGISTER_DIGITS) {
			return;
		}

		AlertDialog.Builder mBuilder = new Builder(getActivity());
		mBuilder.setTitle(getString(R.string.alerts_active))
		.setMessage(getString(R.string.register_congratulations))
		.setPositiveButton(getString(R.string.done), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCodeInput.setText("");
				mEmailInput.setText("");
				mEditor = mPreferences.edit();
				mEditor.clear();
				mEditor.commit();
				mCallback.onButtonClick(R.id.right_button);
				dialog.dismiss();
			}
		})
		.create().show();		
	}
}