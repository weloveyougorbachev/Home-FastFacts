package com.android.fastfacts;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class LocationsData {
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	private String[] allColumns = {DatabaseHelper.colID, DatabaseHelper.colAdress, DatabaseHelper.colName, DatabaseHelper.colLatitude, DatabaseHelper.colLongitude};

	public LocationsData(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	/**
	 * This method gets all of the locations that are in the database.
	 *
	 * @return List<Location>
	 */
	public List<Location> getAllLocations() {
		List<Location> listOfLocations = new ArrayList<Location>();
		Cursor cursor = database.query(DatabaseHelper.locationTable, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			listOfLocations.add(cursorToLocation(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return listOfLocations;
	}
	
	/** TODO THIS METHOD IS NOT DONE
	 * This method gets the five closest locations to the given longitude and latitude.
	 * 
	 * @param lon
	 * @param lat
	 * @return List<Location>
	 */
	public List<Location> getFiveClosestLocations(float lon, float lat) {
		List<Location> listOfLocations = new ArrayList<Location>();
		Cursor cursor = database.query(DatabaseHelper.locationTable, allColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			listOfLocations.add(cursorToLocation(cursor));
			cursor.moveToNext();
		}
		cursor.close();
		return listOfLocations;
	}
	
	/**
	 * This method returns the Location object that a passed cursor object points to.
	 * 
	 * @author Vitaliy Zheltov
	 * @param cursor
	 * @return The Location object that the cursor points to.
	 */
	private Location cursorToLocation(Cursor cursor) {
		Location location = new Location();
		
		location.setId(cursor.getInt(0));
		location.setAdress(cursor.getString(1));
		location.setName(cursor.getString(2));
		location.setLat(cursor.getFloat(3));
		location.setLon(cursor.getFloat(4));
		
		return location;
	}
}
