package com.nasa.apps.spotthestation;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.nasa.apps.spotthestation.fragments.SightingGalleryFragment;

public class MapActivity extends Activity {

	private static final int DEFAULT_ZOOM = 14;
	private static final int SHOW_MAP = 0;
	private static final int SHOW_GALLERY = 1;

	private MapFragment mMap;
	private GoogleMap mGoogleMap;
	private GoogleMapOptions mMapOptions;
	private Fragment mGalleryFragment;
	private LatLng mLatLng;
	
	private int mPrimaryFragment = SHOW_MAP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_empty_container);
		setupActionBar();
		obtainLocation();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		switch (mPrimaryFragment) {
		case SHOW_MAP:
			setupMap(false);
			break;
		case SHOW_GALLERY:
			setupGallery();
			break;
		default:
			break;
		}		
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	private void setupGallery() {
		if (mGalleryFragment == null) {
			mGalleryFragment = new SightingGalleryFragment();
		}
		
		mPrimaryFragment = SHOW_GALLERY;		
		
		FragmentTransaction fragmentTransaction =
				getFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.container, mGalleryFragment);
		fragmentTransaction.commit();
	}

	/**
	 * Obtain location
	 */
	private void obtainLocation() {
		//Get the system service
		LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		//Last known location, first we try with the GPS
		Location mLocation = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (mLocation == null) {
			//GPS was turned off, let's settle for Network Provider
			mLocation = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if (mLocation != null) {
				mLatLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
			}			
		}		
	}

	/**
	 * 
	 * @param force Used to force re-setup of map, used when configuration changes happen. 
	 */
	private void setupMap(boolean force) {
		if (mMap == null || force) {
			Constants.logMessage("Setting up maps");
			//setup the map options
			mMapOptions = new GoogleMapOptions();
			mMapOptions.mapType(GoogleMap.MAP_TYPE_NORMAL);
			if (mLatLng != null) {
				mMapOptions.camera(CameraPosition.fromLatLngZoom(mLatLng, DEFAULT_ZOOM));
			}			

			mMap = MapFragment.newInstance(mMapOptions);	
			
			mPrimaryFragment = SHOW_MAP;

			FragmentTransaction fragmentTransaction =
					getFragmentManager().beginTransaction();
			fragmentTransaction.replace(R.id.container, mMap);
			fragmentTransaction.commit();

			mGoogleMap = mMap.getMap();
			

			if (mGoogleMap != null) {
				Constants.logMessage("Maps was not null");
				mGoogleMap.setMyLocationEnabled(true);
			}
		}
	}

	private void setupActionBar() {
		getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_gallery:
			switch (mPrimaryFragment) {
			case SHOW_MAP:	
				mPrimaryFragment = SHOW_GALLERY;
				setupGallery();				
				item.setIcon(R.drawable.ic_action_map);
				break;
			case SHOW_GALLERY:		
				mPrimaryFragment = SHOW_MAP;
				setupMap(true);
				item.setIcon(R.drawable.ic_action_tiles_large);
				break;
			default:
				break;
			}	
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {		
		Constants.logMessage("Config changed");
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {			
			switch (mPrimaryFragment) {
			case SHOW_MAP:
				setupMap(true);
				break;
			case SHOW_GALLERY:
				setupGallery();
				break;
			default:
				break;
			}		
		}
		else {
			super.onConfigurationChanged(newConfig);
		}
	}

}
