package com.rns.tiffeat.mobile;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class LocationTracker extends Service implements LocationListener {

	private final Context con;
	boolean isGPSOn = false;
	boolean isNetWorkEnabled = false;
	boolean isLocationEnabled = false;
	// minimum distance to request for location update
	private static final long MIN_DISTANCE_TO_REQUEST_LOCATION = 10; 
	private static final long MIN_TIME_FOR_UPDATES = 1000 * 60 * 2; 	
	private Location location;
	private double latitude, longitude;
	LocationManager locationManager;

	public LocationTracker(Context context) {
		this.con = context;
		checkIfLocationAvailable();
	}

	public Location checkIfLocationAvailable() {
		try {
			locationManager = (LocationManager) con.getSystemService(LOCATION_SERVICE);
			isGPSOn = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetWorkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isGPSOn && !isNetWorkEnabled) {
				isLocationEnabled = false;
				Toast.makeText(con, "No Location Provider is Available", Toast.LENGTH_SHORT).show();
			} else {
				isLocationEnabled = true;
				if (isNetWorkEnabled) {
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_TO_REQUEST_LOCATION, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				if (isGPSOn) {
					locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_TO_REQUEST_LOCATION, this);
					if (locationManager != null) {
						location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return location;
	}

	// call this to stop using location
	public void stopUsingLocation() {
		if (locationManager != null) {
			locationManager.removeUpdates(LocationTracker.this);
		}
	}

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}

	public boolean isLocationEnabled() {
		return this.isLocationEnabled;
	}

	public void askToOnLocation() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(con);
		dialog.setTitle("Settings");
		dialog.setMessage("Location is not Enabled.Do you want to go to settings to enable it?");
		dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				con.startActivity(intent);
			}
		});
		dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		dialog.show();
	}

	@Override
	public void onLocationChanged(Location location) {

	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
