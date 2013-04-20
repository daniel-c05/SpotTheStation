package com.nasa.apps.spotthestation;

import android.util.Log;

public class Constants {
	
	private static final String LOG_TAG = "SpotTheStation";
	
	//Change to true to enable logging of events
	private static final boolean LOG_ENABLED = true;

	public static void logMessage (String message) {
		if (LOG_ENABLED)
			Log.v(LOG_TAG, message);		
	}

}