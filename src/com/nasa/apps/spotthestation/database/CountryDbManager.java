package com.nasa.apps.spotthestation.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.nasa.apps.spotthestation.Constants;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CountryDbManager {

	private static CountryDbHelper mHelper;
	private static SQLiteDatabase mDatabase;
	
	private static final String FLAG_NO_COUNTRY = "Select Country";
	private static final String FLAG_NO_REGION = "Select Region";
	private static final String FLAG_NO_STATE = "Select State";

	private static void open (Context context) {
		if (mDatabase != null && mDatabase.isOpen()) {
			return;
		}
		try {
			mHelper = new CountryDbHelper(context);
			mDatabase = mHelper.getReadableDatabase();
		} catch (Exception e) {
			Constants.logMessage(e.toString());
		}
	}

	private static void close () {
		if (mDatabase != null) {
			Constants.logMessage("Closing db");
			mDatabase.close();
		}		
	}

	public static Cursor getCountries (final Context context) {

		if (mDatabase == null || !mDatabase.isOpen()) {
			Constants.logMessage("DB was null, opening");
			open(context);
		}


		return mDatabase.query(CountryDbHelper.TABLE_COUNTRIES, CountryDbHelper.PROJECTION_COUNTRIES, null, null, null, null, CountryDbHelper.DEFAULT_SORT_ORDER);	

	}

	public static Cursor getRegionsForCountry (final Context context, String country) {
		if (mDatabase == null || !mDatabase.isOpen())
			open(context);		

		String where = "country" + " = '" + country +  "'";
		
		Constants.logMessage("Trying to get regions for country: " + country);

		return mDatabase.query(CountryDbHelper.TABLE_REGIONS, CountryDbHelper.PROJECTION_REGIONS, where, null, null, null, CountryDbHelper.DEFAULT_SORT_ORDER);				
	}
	
	public static Cursor getCitiesForCountryAndRegion (final Context context, String country, String region) {
		if (mDatabase == null || !mDatabase.isOpen())
			open(context);		
		
		if (region.contains(FLAG_NO_REGION) || region.contains(FLAG_NO_STATE)) {
			country = FLAG_NO_COUNTRY;
		}

		String where = "country" + " = '" + country +  "' and " 
		+ "region" + " = '" + region + "'";
		
		Constants.logMessage("Trying to get cities for country: " + country + " and region: " + region);

		return mDatabase.query(CountryDbHelper.TABLE_CITIES, CountryDbHelper.PROJECTION_CITIES, where, null, null, null, CountryDbHelper.DEFAULT_SORT_ORDER);				
	}

	public static void copyDataBase(final Context context){
		
		if (mDatabase == null || !mDatabase.isOpen()) {
			Constants.logMessage("DB was null, opening");
			open(context);
		}

		try{
			InputStream assestDB = context.getAssets().open(CountryDbHelper.DB_NAME);

			OutputStream appDB = new FileOutputStream(CountryDbHelper.DB_PATH +  CountryDbHelper.DB_NAME,false);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = assestDB.read(buffer)) > 0) {
				appDB.write(buffer, 0, length);
			}

			appDB.flush();
			appDB.close();
			assestDB.close();
			Constants.logMessage("DB copied succesfully");
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private static int getRowPos (final Context context, String [] projection, String table, String where) {
		if (mDatabase == null || !mDatabase.isOpen()) {
			Constants.logMessage("DB was null, opening");
			open(context);
		}
		
		int pos = -1;
		
		final Cursor mCursor = mDatabase.query(table, projection, where, null, null, null, CountryDbHelper.DEFAULT_SORT_ORDER);
		
		if (mCursor != null) {
			if (mCursor.moveToFirst()) {
				pos  = mCursor.getInt(0) - 1;
			} 
		}
		
		Constants.logMessage("Position for data: " + pos);
		
		return pos;		
	}
	
	public static int getRowForCountry (final Context context, String country) {
		if (mDatabase == null || !mDatabase.isOpen()) {
			Constants.logMessage("DB was null, opening");
			open(context);
		}
		
		final String where = "country" + " = '" + country +  "'";
		
		return getRowPos(context, CountryDbHelper.PROJECTION_COUNTRIES, CountryDbHelper.TABLE_COUNTRIES, where);		
	}
	
	public static int getRowForRegion (final Context context, String country, String region) {
		if (mDatabase == null || !mDatabase.isOpen()) {
			Constants.logMessage("DB was null, opening");
			open(context);
		}
		
		final String where = "country" + " = '" + country +  "' and " 
				+ "region" + " = '" + region + "'";
		
		return getRowPos(context, CountryDbHelper.PROJECTION_REGIONS, CountryDbHelper.TABLE_REGIONS, where);		
	}
	
	public static int getRowForCity (final Context context, String country, String region, String city) {
		if (mDatabase == null || !mDatabase.isOpen()) {
			Constants.logMessage("DB was null, opening");
			open(context);
		}
		
		final String where = "country" + " = '" + country +  "' and " 
				+ "region" + " = '" + region +  "' and "
				+ "city" + " = '" + city + "'";
		
		return getRowPos(context, CountryDbHelper.PROJECTION_CITIES, CountryDbHelper.TABLE_CITIES, where);		
	}

	public static void clearDb(final Context context) {
		if (mDatabase == null || !mDatabase.isOpen()) {
			Constants.logMessage("DB was null, opening");
			open(context);
		}
		
		boolean deleted = SQLiteDatabase.deleteDatabase(new File(CountryDbHelper.DB_PATH  + CountryDbHelper.DB_NAME));
		
		Constants.logMessage("Was db deleted? " + deleted);
		
	}

}
