package com.nasa.apps.spotthestation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.nasa.apps.spotthestation.fragments.RegisterPageFourFragment;
import com.nasa.apps.spotthestation.fragments.RegisterPageOneFragment;
import com.nasa.apps.spotthestation.fragments.RegisterPageThreeFragment;
import com.nasa.apps.spotthestation.fragments.RegisterPageTwoFragment;
import com.nasa.apps.spotthestation.interfaces.onReviewItemEvent;
import com.viewpagerindicator.UnderlinePageIndicator;

public class SignUpActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, onReviewItemEvent {

	private static final int WORKFLOW_PAGES = 3;
	
	public static final int ITEM_EMAIL = 0;
	public static final int ITEM_LOCATION = 1;
	
	public static final String PREFS_REGISTRATION = "registration-data";
	
	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;

	private ViewPager mViewPager;
	private UnderlinePageIndicator mIndicator;
	private ScreenSlidePagerAdapter mPagerAdapter;
	private Button mNextButton, mPrevButton;

	private RegisterPageOneFragment mPageOneFragment;
	private RegisterPageTwoFragment mPageTwoFragment;
	private RegisterPageThreeFragment mPageThreeFragment;
	private RegisterPageFourFragment mPageFourFragment;

	/**
	 * Flags, when set to true, scrolling to next step will be allowed 
	 */
	private boolean mAllowSecondStep = false, mAllowReview = false, mAllowCodeSubmission = false;
	private boolean isInEditMode = false;
	
	//User info, used for the review fragment. 
	private String mEmail, mCountry, mRegion, mCity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		
		mPreferences = getSharedPreferences(PREFS_REGISTRATION, MODE_PRIVATE);
		
		String url = getIntent().getDataString();
		
		if (url != null) {
			Constants.logMessage(url);
			if (url.contains("complete.cfm?")) {
				Intent confirmCode = new Intent(this, HomeActivity.class);
				confirmCode.putExtra("url", url);
				startActivity(confirmCode);
			}
		}
		
		setupActionBar();
		setViews();
	}

	private OnClickListener mNextButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final int pos = mViewPager.getCurrentItem();
			if (pos == 2) {
				showReceivedCodeFragment();
			}
			else {
				mViewPager.setCurrentItem(pos + 1);
			}
		}
	};

	private OnClickListener mPrevButtonListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			final int pos = mViewPager.getCurrentItem();
			mViewPager.setCurrentItem(pos - 1);
		}
	};

	private void setViews() {
		mViewPager = (ViewPager) findViewById(R.id.register_pager);
		mIndicator = (UnderlinePageIndicator) findViewById(R.id.register_pager_indicator);
		mNextButton = (Button) findViewById(R.id.right_button);
		mPrevButton = (Button) findViewById(R.id.left_button);
		
		//We want to keep all 4 fragments in memory, they are light-weighted so memory consumption wouldn't be so bad. 
		mViewPager.setOffscreenPageLimit(4);

		mPageOneFragment = new RegisterPageOneFragment();
		mPageTwoFragment = new RegisterPageTwoFragment();
		mPageThreeFragment = new RegisterPageThreeFragment();
		mPageFourFragment = new RegisterPageFourFragment();


		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPagerAdapter.setCutOffPage(0);

		mViewPager.setAdapter(mPagerAdapter);			
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);

		mNextButton.setOnClickListener(mNextButtonListener);
		mPrevButton.setOnClickListener(mPrevButtonListener);

		updateButtonBar();
	}

	/**
	 * Show a dialog to confirm the submission of the entered data. 
	 */
	protected void showReceivedCodeFragment() {
		
		Intent intent = new Intent(SignUpActivity.this, HomeActivity.class);
		intent.putExtra("url", "");
		startActivity(intent);
		SignUpActivity.this.finish();							
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	protected void onStop() {
		super.onStop();
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

	/**
	 * Simple Pager implementation set for to the ViewPager.
	 */
	class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

		/**
		 * Cut off the pager on the last required page. 
		 */
		private int mCutOffPage;

		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public ScreenSlidePagerAdapter(FragmentManager fm, int count) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return mPageOneFragment;
			case 1:
				return mPageTwoFragment;
			case 2:
				return mPageThreeFragment;
			case 3: 
				return mPageFourFragment;
			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return Math.min(mCutOffPage + 1,  WORKFLOW_PAGES);
		}

		public void setCutOffPage(int cutOffPage) {
			if (cutOffPage < 0) {
				cutOffPage = Integer.MAX_VALUE;
			}
			mCutOffPage = cutOffPage;
			mIndicator.notifyDataSetChanged();
		}

		public int getCutOffPage() {
			return mCutOffPage;
		}
	}


	@Override
	public void onPageScrollStateChanged(int arg0) {		
	}


	public void updateCutOffPage(int pageNum, boolean done) {
		if (done) {
			mPagerAdapter.setCutOffPage(pageNum);
		}
		else {
			mPagerAdapter.setCutOffPage(pageNum -1);
		}
		updateButtonBar();
	}

	public void updateButtonBar() {
		int position = mViewPager.getCurrentItem();
		switch (position) {
		case 0:
			//Previous is not activated on viewPager's first position.
			mPrevButton.setEnabled(false);
			mPrevButton.setText("");
			mNextButton.setText(getString(R.string.next));
			mNextButton.setEnabled(mAllowSecondStep);
			break;
		case 1:
			//Previous is now activated, regardless of whether page is completed or not 
			mPrevButton.setEnabled(true);
			mPrevButton.setText(getString(R.string.previous));
			mNextButton.setText(getString(R.string.next));
			mNextButton.setEnabled(mAllowReview);
			break;
		case 2:
			//Previous and Next are now activated, regardless of whether page is completed or not 
			mPrevButton.setEnabled(true);
			mPrevButton.setText(getString(R.string.previous));
			mNextButton.setEnabled(mAllowCodeSubmission);
			mNextButton.setText(getString(R.string.agree));
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {		
	}

	@Override
	public void onPageSelected(int pos) {
		updateButtonBar();
	}

	@Override
	public void onFlagChanged(int pageNum, boolean value) {
		Constants.logMessage("Flag Changed, updating completed value of page: " + pageNum + " to: " + value);
		if (isInEditMode) {
			//If user is fixing information that was not correct earlier, simply don't do anything.
			return;
		}
		switch (pageNum) {
		case 1:
			mAllowSecondStep = value;
			updateCutOffPage(pageNum, value);
			break;
		case 2:
			mAllowReview = value;
			updateCutOffPage(pageNum, value);
			break;
		case 3: 
			mAllowCodeSubmission = value;
			updateCutOffPage(pageNum, value);
			break;
		default:
			//do nothing
			break;
		}
	}

	@Override
	public void onReviewItem(int which) {
		isInEditMode = true;
		switch (which) {
		case ITEM_EMAIL:
			mViewPager.setCurrentItem(1);
			break;
		case ITEM_LOCATION:
			mViewPager.setCurrentItem(0);
			break;
		default:
			break;
		}
	}

	@Override
	public void onFormDataChanged(int which, Bundle info) {
		
		Constants.logMessage("Saving Stuff");
		
		switch (which) {
		case ITEM_EMAIL:
			mEmail = info.getString(RegisterPageTwoFragment.EXTRA_EMAIL);
			if (isInEditMode) {
				((TextView) findViewById(R.id.review_email)).setText(mEmail);
			}
			break;
		case ITEM_LOCATION:
			mCity = info.getString(RegisterPageOneFragment.EXTRA_CITY);
			mRegion = info.getString(RegisterPageOneFragment.EXTRA_REGION);
			mCountry = info.getString(RegisterPageOneFragment.EXTRA_COUNTRY);
			if (isInEditMode) {
				((TextView) findViewById(R.id.review_location)).setText(mCountry + ", " + mRegion + ", " + mCity);
			}
			break;
		default:
			break;
		}
		
		mEditor = mPreferences.edit();
		mEditor.putString(RegisterPageTwoFragment.EXTRA_EMAIL, mEmail);
		mEditor.putString(RegisterPageOneFragment.EXTRA_CITY, mCity);
		mEditor.putString(RegisterPageOneFragment.EXTRA_COUNTRY, mCountry);
		mEditor.putString(RegisterPageOneFragment.EXTRA_REGION, mRegion);
		mEditor.commit();
		
	}

}
