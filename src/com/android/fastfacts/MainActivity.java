/**
 * This is our Main Activity that handles with getting the gps location of the phone and
 * then creates our list view from the google places that we get.
 * 
 * @author Vitaliy Zheltov, Adam Formenti
 */
package com.android.fastfacts;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity implements LocationListener {
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Google Places
	gPlaceAccess googlePlaces;

	// Places List
	PlacesList nearPlaces;

	// Progress dialog
	ProgressDialog pDialog;
	
	// Places Listview
	ListView lv;
	
	// ListItems data
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
	
	// KEY Strings
	public static String KEY_REFERENCE = "reference"; // id of the place
	public static String KEY_NAME = "name"; // name of the place
	public static String KEY_VICINITY = "vicinity"; // Place area name
	
    private LocationManager locationManager;
    private Location gpsLocation = null;
    private String provider;
    
	DatabaseHelper myDbHelper = new DatabaseHelper(this);

    TextView tv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		lv = (ListView) findViewById(R.id.listOfPlaces);
		
		// This creates the database and allows you to read it.
		databaseCreation();

        // This gets the location and puts it in the variable 'gpsLocation'
        gettingLocation();
    
	
		// This calls background Async task to load Google Places
		// After getting places from Google all the data is shown in listview
		new LoadPlaces().execute();
		
		// This is on item click listener which listens if an item on the list has been clicked.
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	// Getting our reference from the selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
                String name = ((TextView) view.findViewById(R.id.name)).getText().toString();
                
                // Starting new intent
                Intent singlePlaceActivity = new Intent(getApplicationContext(), SinglePlaceActivity.class);
                
                // Sending place name id to single place activity
                singlePlaceActivity.putExtra("Name", name);           
                
                // Sending place reference id to single place activity
                singlePlaceActivity.putExtra(KEY_REFERENCE, reference);
                
                startActivity(singlePlaceActivity);
            }
        });
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
        pDialog.dismiss();
    }

    // Removes the locationlistener updates when Activity is paused.
    @Override
    protected void onPause() {
      super.onPause();
      locationManager.removeUpdates(this);
      pDialog.dismiss();
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
	
	/**
	 * Background Async Task to Load Google places
	 * */
	class LoadPlaces extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// Creating Places class object
			googlePlaces = new gPlaceAccess();
			
			try {
				// Separeate your place types by PIPE symbol "|"
				// or null for everything
				String types = "restaurant|gas_station|cafe";
				
				// Radius in meters
				double radius = 1000;
				
				// Get nearest places
				nearPlaces = googlePlaces.search(gpsLocation.getLatitude(), gpsLocation.getLongitude(), radius, types);
				

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * and show the data in UI
		 * Always use runOnUiThread(new Runnable()) to update UI from background
		 * thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// Dismisses the dialog after getting all products
			pDialog.dismiss();
			// Updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					// Get json response status
					String status = nearPlaces.status;
					
					// Check for all possible status
					if(status.equals("OK")){
						// Successfully got places details
						if (nearPlaces.results != null) {
							// loop through each place
							for (Place p : nearPlaces.results) {
								HashMap<String, String> map = new HashMap<String, String>();
								
								// Place reference won't display in listview - it will be hidden
								// Place reference is used to get "place full details"
								map.put(KEY_REFERENCE, p.reference);
								
								// Place name
								map.put(KEY_NAME, p.name);
								
								// GOING TO PUT OTHER STUFF IN HERE
								// Place vicinity
								//map.put(KEY_VICINITY, p.vicinity);
								
								
								// adding HashMap to ArrayList
								placesListItems.add(map);
							}
							// list adapter
							ListAdapter adapter = new SimpleAdapter(MainActivity.this, placesListItems,
					                R.layout.list_item,
					                new String[] { KEY_REFERENCE, KEY_NAME}, new int[] {
					                        R.id.reference, R.id.name });
							
							// Adding data into listview
							lv.setAdapter(adapter);
						}
					}
					else if(status.equals("ZERO_RESULTS")){
						// Zero results found
						alert.showAlertDialog(MainActivity.this, "Near Places",
								"Sorry no places found. Try to change the types of places",
								false);
					}
					else if(status.equals("UNKNOWN_ERROR"))
					{
						alert.showAlertDialog(MainActivity.this, "Places Error",
								"Sorry unknown error occured.",
								false);
					}
					else if(status.equals("OVER_QUERY_LIMIT"))
					{
						alert.showAlertDialog(MainActivity.this, "Places Error",
								"Sorry query limit to google places is reached",
								false);
					}
					else if(status.equals("REQUEST_DENIED"))
					{
						alert.showAlertDialog(MainActivity.this, "Places Error",
								"Sorry error occured. Request is denied",
								false);
					}
					else if(status.equals("INVALID_REQUEST"))
					{
						alert.showAlertDialog(MainActivity.this, "Places Error",
								"Sorry error occured. Invalid Request",
								false);
					}
					else
					{
						alert.showAlertDialog(MainActivity.this, "Places Error",
								"Sorry error occured.",
								false);
					}
				}
			});

		}

	}
}


