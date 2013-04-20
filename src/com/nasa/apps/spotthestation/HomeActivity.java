package com.nasa.apps.spotthestation;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SpinnerAdapter;

import com.nasa.apps.spotthestation.adapters.SlidingMenuAdapter;
import com.nasa.apps.spotthestation.database.CountryDbManager;
import com.nasa.apps.spotthestation.fragments.CrewInfoFragment;
import com.nasa.apps.spotthestation.fragments.CrewInfoFragment.OnCrewMemberSelected;
import com.nasa.apps.spotthestation.fragments.CrewMemberDetailFragment;
import com.nasa.apps.spotthestation.fragments.NewsArticleFragment;
import com.nasa.apps.spotthestation.fragments.NewsFeedFragment;
import com.nasa.apps.spotthestation.fragments.NewsFeedFragment.OnNewsArticleSelected;
import com.nasa.apps.spotthestation.fragments.RegisterExtendedOrDeleteFragment;
import com.nasa.apps.spotthestation.fragments.RegisterHomeFragment;
import com.nasa.apps.spotthestation.fragments.RegisterHomeFragment.OnRegisterItemSelectedListener;
import com.nasa.apps.spotthestation.fragments.RegisterPageFourFragment;
import com.nasa.apps.spotthestation.fragments.ViewExampleAlertFragment;
import com.nasa.apps.spotthestation.interfaces.OnRegisterButtonListener;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.slidingmenu.lib.SlidingMenu.OnOpenedListener;

public class HomeActivity extends FragmentActivity implements OnRegisterItemSelectedListener, 
	OnRegisterButtonListener, OnNewsArticleSelected, OnCrewMemberSelected {

	private static final int SIGHTINGS = 0;
	private static final int ALERTS = 1;
	private static final int ABOUT_ISS = 2;
	private static final int SOCIAL = 3;

	private static final int SIGN_UP = 5;
	private static final int EXTEND_OR_DELETE = 6;
	private static final int RECEIVED_CODE = 7;
	private static final int VIEW_EXAMPLE = 8;
	private static final int HOME = 4;
	
	private static final int ARTICLE_VIEW = 10;
	private static final int ABOUT_CREW = 11;
	private static final int NEWS_LIST = 9;
	
	private static final int CREW_MEMBER_BIO = 12;

	SharedPreferences mPreferences;
	SharedPreferences.Editor mEditor;
	boolean isFirstBoot;
	private SlidingMenu mSlidingMenu;
	private SlidingMenuAdapter mSlidingMenuAdapter;
	private ListView mSlidingMenuList;
	private int mCurrentItem = ABOUT_ISS;
	private int mSubFragment;

	FragmentTransaction mFragmentTransaction;	
	Fragment mPrimaryFragment;

	ActionBar mActionBar;

	private SpinnerAdapter mSpinnerAdapter;

	private OnNavigationListener mNavigationCallback = new OnNavigationListener() {

		@Override
		public boolean onNavigationItemSelected(int itemPosition, long itemId) {			
			switch (itemPosition) {
			case 0:	//News List
				Constants.logMessage("Setting News List again");
				mSubFragment = NEWS_LIST;
				mPrimaryFragment = new NewsFeedFragment();
				setPrimaryFragment(R.string.latest_news);
				break;
			case 1:	//Experiments, no Source For this yet.
				break;
			case 2:	//Crew's Info
				mSubFragment = ABOUT_CREW;
				mPrimaryFragment = new CrewInfoFragment();
				setPrimaryFragment(R.string.latest_news);
				break;
			default:
				break;
			}
			return true;
		}
	};

	public void onBackPressed() {
		switch (mSubFragment) {
		case EXTEND_OR_DELETE:
		case RECEIVED_CODE:
		case VIEW_EXAMPLE:
			mSubFragment = HOME;
			mPrimaryFragment = new RegisterHomeFragment();
			setPrimaryFragment(R.string.register_for_alerts);
			break;
		case ARTICLE_VIEW:
			mSubFragment = NEWS_LIST;
			mPrimaryFragment = new NewsFeedFragment();
			setPrimaryFragment(R.string.latest_news);	
			break;
		case ABOUT_CREW:
			mActionBar.setSelectedNavigationItem(0);
			break;
		case CREW_MEMBER_BIO:
			mSubFragment = ABOUT_CREW;
			mPrimaryFragment = new CrewInfoFragment();
			setPrimaryFragment(R.string.latest_news);
			break;
		default:
			super.onBackPressed();
			break;
		}
	};

	private OnItemClickListener mOnSlideItemListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
				long arg3) {

			if (mCurrentItem == pos) {
				//If the current item selected is reselected, do nothing. 
				return;
			}

			//Hide the SlidingMenu
			mSlidingMenu.toggle();
			//Update the ActionBar
			updateActionBar(pos);

			switch (pos) {
			case SIGHTINGS:
				mCurrentItem = SIGHTINGS;
				mSubFragment = HOME;	
				showMapActivity();							
				break;
			case ALERTS:
				mPrimaryFragment = new RegisterHomeFragment();
				mCurrentItem = ALERTS;
				mSubFragment = HOME;
				setPrimaryFragment(R.string.register_for_alerts);					
				break;
			case ABOUT_ISS:				
				mPrimaryFragment = new NewsFeedFragment();
				mCurrentItem = ABOUT_ISS;
				mSubFragment = -1;
				setPrimaryFragment(R.string.latest_news);
				break;
			case SOCIAL:
				showSocial();	
				break;
			default:
				break;
			}	
		}

	};

	private void updateActionBar (int pos) {
		switch (pos) {
		case SIGHTINGS:
			mActionBar.setDisplayShowTitleEnabled(true);
			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case ALERTS:
			mActionBar.setDisplayShowTitleEnabled(true);
			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			break;
		case ABOUT_ISS:
			mActionBar.setDisplayShowTitleEnabled(false);
			mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);			
			mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_about_iss, android.R.layout.simple_spinner_dropdown_item);			
			mActionBar.setListNavigationCallbacks(mSpinnerAdapter, mNavigationCallback);
			break;
		case SOCIAL:

			break;
		default:
			break;
		}
	}

	protected void showSocial() {
		Intent social = new Intent(this, SocialHome.class);
		startActivity(social);
	}

	protected void showMapActivity() {
		Intent sighting = new Intent(this, MapActivity.class);
		startActivity(sighting);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updatePrefDependentValues();
		setContentView(R.layout.activity_empty_container);

		mActionBar = getActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMenu(R.layout.sliding_drawer);		
		mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		mSlidingMenu.setFadeEnabled(false);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		mSlidingMenu.setOnOpenedListener(mOnOpenedListener);
		mSlidingMenu.setOnClosedListener(mOnClosedListener);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);

		mSlidingMenuAdapter = new SlidingMenuAdapter(this);		

		mSlidingMenuList = (ListView) findViewById(R.id.sliding_drawer);
		mSlidingMenuList.setAdapter(mSlidingMenuAdapter);
		mSlidingMenuList.setOnItemClickListener(mOnSlideItemListener);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			if (extras.containsKey("url")) {
				mPrimaryFragment = new RegisterPageFourFragment();
				mCurrentItem = ALERTS;
				mSubFragment = RECEIVED_CODE;
				setPrimaryFragment(R.string.received_code);
				Bundle args = new Bundle();
				args.putString("url", extras.getString("url"));
				mPrimaryFragment.setArguments(args);
			}
		}
		//default page
		updateActionBar(mCurrentItem);	
	}

	private void setPrimaryFragment (int title) {
		mFragmentTransaction = getSupportFragmentManager().beginTransaction(); 
		mFragmentTransaction.replace(R.id.container, mPrimaryFragment);
		mFragmentTransaction.commit();
		mActionBar.setTitle(title);
	}

	/**
	 * Used to know if this is the first time the app is launched. 
	 */
	private void updatePrefDependentValues() {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		//Default Value is true, since the default value is only set when the preference doesn't exist yet. 
		isFirstBoot = mPreferences.getBoolean(getString(R.string.pref_key_first_boot), true);
		if (isFirstBoot) { 
			//If it is the first time the app is launched, then copy the database from the asset folder. 
			CountryDbManager.copyDataBase(this);
			mEditor = mPreferences.edit();
			mEditor.putBoolean(getString(R.string.pref_key_first_boot), false);
			mEditor.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mSlidingMenu.toggle();
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}

	OnOpenedListener mOnOpenedListener = new OnOpenedListener() {

		@Override
		public void onOpened() {
			Constants.logMessage("Menu is now opened");
			mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		}
	};

	OnClosedListener mOnClosedListener = new OnClosedListener() {

		@Override
		public void onClosed() {
			Constants.logMessage("Menu is now closed");
			mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	};

	@Override
	public void onItemSelected(int pos) {
		Intent mIntent = new Intent();
		switch (pos) {
		case SIGN_UP - SIGN_UP:
			mIntent.setClass(this, SignUpActivity.class);
			startActivity(mIntent);
			break;
		case EXTEND_OR_DELETE - SIGN_UP:
			mPrimaryFragment = new RegisterExtendedOrDeleteFragment();
			mSubFragment = EXTEND_OR_DELETE;
			setPrimaryFragment(R.string.extend_or_delete_alerts);
			break;
		case RECEIVED_CODE - SIGN_UP:
			mPrimaryFragment = new RegisterPageFourFragment();
			mSubFragment = RECEIVED_CODE;
			setPrimaryFragment(R.string.received_code);
			break;
		case VIEW_EXAMPLE - SIGN_UP:
			mPrimaryFragment = new ViewExampleAlertFragment();
			mSubFragment = VIEW_EXAMPLE;
			setPrimaryFragment(R.string.example_alert);
			break;
		default:
			break;
		}
	}

	@Override
	public void onButtonClick(int id) {
		mSubFragment = HOME;
		mPrimaryFragment = new RegisterHomeFragment();
		setPrimaryFragment(R.string.register_for_alerts);
	}

	@Override
	public void onNewsArticleSelected(int pos, String identifier) {
		mPrimaryFragment = new NewsArticleFragment();
		mSubFragment = ARTICLE_VIEW;
		setPrimaryFragment(R.string.example_alert);
	}

	@Override
	public void onCrewMemberSelected(String name) {
		mPrimaryFragment = new CrewMemberDetailFragment();
		mSubFragment = CREW_MEMBER_BIO;
		setPrimaryFragment(R.string.example_alert);
	}

}