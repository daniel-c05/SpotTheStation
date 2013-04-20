package com.nasa.apps.spotthestation.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nasa.apps.spotthestation.Constants;

@SuppressLint("SdCardPath")
public class CountryDbHelper extends SQLiteOpenHelper {

	/**
	 * This path is hardcoded, during the first time application opens
	 * we copy a database file from the assets folder into this folder. 
	 */
	public final static String DB_PATH = "/data/data/com.nasa.apps.spotthestation/databases/";
	private static final int DB_VERSION = 1;
	public static String DB_NAME = "registration.db";
	public static final String TABLE_COUNTRIES = "countries";
	public static final String TABLE_REGIONS = "regions";
	public static final String TABLE_CITIES = "cities";
	public static final String DEFAULT_SORT_ORDER = "_id";
	
	/**
	 * Projection used for cursor adapter, we only care for the country. 
	 */
	public static final String [] PROJECTION_COUNTRIES = {
		"_id",
		"country"
	};
	
	/**
	 * Projection used for cursor adapter, we only care about the region. 
	 */
	public static final String [] PROJECTION_REGIONS = {
		"_id",
		"region"
	};
	
	/**
	 * Projection used for cursor adapter, we only care about the city.  
	 */
	public static final String [] PROJECTION_CITIES = {
		"_id",
		"city"
	};
	
	/**
	 * Table with only countries
	 */
	private static final String CREATE_TABLE_COUNTRIES = "create table " + TABLE_COUNTRIES 
			+ "(_id numeric, " 
			+ "country text);"
			;
	
	/**
	 * Table that contains regions per country
	 */
	private static final String CREATE_TABLE_REGIONS = "create table " + TABLE_REGIONS 
			+ "(_id numeric, " 
			+ "country text, "
			+ "region text);"
			;
	
	/**
	 * Table that contains cities, per regions, per country. 
	 */
	private static final String CREATE_TABLE_CITIES = "create table " + TABLE_CITIES 
			+ "(_id numeric, " 
			+ "country text, "
			+ "region text, "
			+ "city text);"
			;
	
	public CountryDbHelper (Context context) {
	    super(context, DB_NAME, null, DB_VERSION );
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Constants.logMessage("Creating database from scratch?");
		//Create both of the tables as part of the databases
		try {
			db.execSQL(CREATE_TABLE_COUNTRIES);
			db.execSQL(CREATE_TABLE_REGIONS);
			db.execSQL(CREATE_TABLE_CITIES);
		}
		catch (Exception e) {
			Log.w("SpotTheStation", "Error while creating db: " + e.toString());
		}		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Constants.logMessage("Upgrading db from " + oldVersion + " to new version: " + newVersion);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_COUNTRIES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REGIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_CITIES);
	    onCreate(db);
	}
}