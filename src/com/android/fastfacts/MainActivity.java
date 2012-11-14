//dfgfgfg
package com.android.fastfacts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements LocationListener {
	
    private LocationManager locationManager;
    private Location gpsLocation = null;
    private String provider;
    
	LocationsData locationsDataSource = new LocationsData(this);
	DatabaseHelper myDbHelper = new DatabaseHelper(this);

    TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView)findViewById(R.id.textView1);
		
		// This creates the database and allows you to read it.
		databaseCreation();

        // This gets the location and puts it in the variable 'gpsLocation'
        gettingLocation();
        
		// This is how you get the list of the locations (Don't forget to open the source and then close it)
        locationsDataSource.open();
		List<Locations> listOfAllLocations = locationsDataSource.getAllLocations();
		locationsDataSource.close();
		
        // This is how you get the latitude and longitude, this is just for an example.
        double one = gpsLocation.getLatitude();
        double two = gpsLocation.getLongitude();
        
        tv.setText("" + one + ", " + two);
		
	}

	// Method that gets the location
	private void gettingLocation() {	
        // Get the LocationManager object.
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (locationManager.isProviderEnabled(provider)) {
        	// This right here actually gets the location.
        	gpsLocation = locationManager.getLastKnownLocation(provider);
        }
        
        // If we did not get the gps location then a toast will show up.
        if (gpsLocation != null) {
        	System.out.println("Provider " + provider + " has been selected.");
        } else {
        	Toast.makeText(this, "GPS location could not be determined", Toast.LENGTH_SHORT).show();
        }
	}

	// Method to "create" the database (It actually just copies our database to the user's phone and uses that)
	private void databaseCreation() throws Error {
		myDbHelper = new DatabaseHelper(this);

		try {
			myDbHelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		myDbHelper.openDataBase();
	}
	
	// On Start this checks if the gps in enabled on the device.
    @Override
    protected void onStart() {
        super.onStart();
        
        // Checks if the GPS setting is currently enabled on the device.
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        final boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!gpsEnabled) {
            // Builds an alert dialog here that requests that the user enable gps.
            new EnableGpsDialogFragment().show(getSupportFragmentManager(), "enableGpsDialog");
        }
    }
    
    // This creates a dialog that asks if the user want to turn the gps on
    // and if he does then it will start the setting intent
    private class EnableGpsDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.enable_gps)
                    .setMessage(R.string.enable_gps_dialog)
                    .setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(settingsIntent);
                        }
                    })
                    .create();
        }
    }
  
    // Removes the locationlistener updates when Activity is stoped.
    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(this);
    }

    // Removes the locationlistener updates when Activity is paused.
    @Override
    protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
    }
    
    // Requests updates at startup.
    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }
    
    // Location Listener
	public void onLocationChanged(Location location) {
		// TODO If the location changes we should put stuff here.
	}

	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider, Toast.LENGTH_SHORT).show();
	}

	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider, Toast.LENGTH_SHORT).show();
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
}


