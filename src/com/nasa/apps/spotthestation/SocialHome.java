package com.nasa.apps.spotthestation;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;

public class SocialHome extends Activity implements ConnectionCallbacks, OnConnectionFailedListener {

	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	final int TWITTER_AUTH_REQUEST_CODE = 443343;
	private PlusClient mPlusClient;
	private ConnectionResult mConnectionResult;
	private boolean isSignOnRequiredAtStart = false;
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	private OnClickListener onPlusClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			Constants.logMessage("Attempting to connect since button was clicked");
			if (view.getId() == R.id.sign_in_button && !mPlusClient.isConnected()) {
				if (mConnectionResult == null) {
					mPlusClient.connect();
				} else {
					try {
						mConnectionResult.startResolutionForResult(SocialHome.this, REQUEST_CODE_RESOLVE_ERR);
					} catch (SendIntentException e) {
						// Try connecting again.
						mConnectionResult = null;
						mPlusClient.connect();
					}
				}
			}
		}
	};

	private OnClickListener mOnTwitterClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			startTwitterAuth();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.social_choose_account);
		updatePrefDependentValues();
		setupActionBar();
		initPlusClient();
		initViews();
	}

	protected void startTwitterAuth() {
		
	}

	private void initViews() {
		findViewById(R.id.twitter_sign_in).setOnClickListener(mOnTwitterClickListener);
	}

	private void updatePrefDependentValues() {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		isSignOnRequiredAtStart = mPreferences.getBoolean("is_sign_on_required_at_launch", false);		
	}

	private void initPlusClient() {
		mPlusClient = new PlusClient.Builder(this, this, this)
		.setVisibleActivities("http://schemas.google.com/AddActivity", "http://schemas.google.com/BuyActivity")
		.build();	 
		findViewById(R.id.sign_in_button).setOnClickListener(onPlusClickListener);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.social_home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(this, "Signed Out!", Toast.LENGTH_LONG).show();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (isSignOnRequiredAtStart) {
			Constants.logMessage("Attempting to connect since sign on is required");
			mPlusClient.connect();
		}        
	}

	@Override
	protected void onStop() {
		super.onStop();
		mPlusClient.disconnect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
			}
		}
		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
			mConnectionResult = null;
			mPlusClient.connect();
		}
		else if (requestCode == TWITTER_AUTH_REQUEST_CODE) {
			if (responseCode == Activity.RESULT_OK) {
				String oauthVerifier = (String) intent.getExtras().get("oauth_verifier");
				
			}
		}
	}

	void onOAuthVerifier(String oauthVerifier) {
		setContentView(R.layout.loading);
		//TwitterManager.get().getOAuthAccessToken(mRequestToken, oauthVerifier, mGetOAuthAccessTokenCallback);
	}

	@Override
	public void onConnected() {
		String accountName = mPlusClient.getAccountName();
		isSignOnRequiredAtStart = true;
		mEditor = mPreferences.edit();
		mEditor.putBoolean("is_sign_on_required_at_launch", true);
		mEditor.commit();
		Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
	}

}
