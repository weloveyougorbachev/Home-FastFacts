package com.android.fastfacts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	LocationsData locationsDataSource = new LocationsData(this);
	DatabaseHelper myDbHelper = new DatabaseHelper(this);


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// This creates the database and allows you to read it.
		databaseCreation();
		
		// This is how you get the list of the locations
		List<Location> listOfAllLocations = locationsDataSource.getAllLocations();
	}

	
	private void databaseCreation() throws Error {
		myDbHelper = new DatabaseHelper(this);

		try {
			myDbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		myDbHelper.openDataBase();
	}
}
