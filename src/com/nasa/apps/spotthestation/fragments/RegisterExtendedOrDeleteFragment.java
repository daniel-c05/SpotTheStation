package com.nasa.apps.spotthestation.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.nasa.apps.spotthestation.R;
import com.nasa.apps.spotthestation.interfaces.OnRegisterButtonListener;

public class RegisterExtendedOrDeleteFragment extends Fragment {
	
	private static final String FLAG_REQ_CHAR = "@";
	
	private AutoCompleteTextView mEmailInput;
	private EditText mConfirmEmail;
	private Button mCancelButton, mCompleteButton;
	private OnRegisterButtonListener mCallback;
	
	private View.OnClickListener mOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.left_button:
				mCallback.onButtonClick(v.getId());
				break;
			case R.id.right_button:
				
				break;
			default:
				break;
			}
		}
	};
	
	private final TextWatcher mWatcher = new TextWatcher() {

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
                    + " must implement OnHeadlineSelectedListener");
        }
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_register_extend_delete, null);
		
		mEmailInput = (AutoCompleteTextView) view.findViewById(R.id.extend_email);
		mConfirmEmail = (EditText) view.findViewById(R.id.extend_confirm_email);
		
		mCancelButton = (Button) view.findViewById(R.id.left_button);
		mCompleteButton = (Button) view.findViewById(R.id.right_button);
		
		mCancelButton.setText(getString(R.string.cancel));
		mCompleteButton.setText(getString(R.string.request_code));
		
		mCancelButton.setOnClickListener(mOnClickListener);
		mCompleteButton.setOnClickListener(mOnClickListener);
		//Disable the complete button for now
		mCompleteButton.setEnabled(false);
		
		mConfirmEmail.addTextChangedListener(mWatcher);
		
		return view;	
		
	}
	
	@Override
	public void onDestroy() {
		if (mConfirmEmail != null) {
			mConfirmEmail.removeTextChangedListener(mWatcher);
		}		
		super.onDestroy();
	}
	
}
