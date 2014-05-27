package com.e9ine.cab9.driver.services;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.e9ine.cab9.driver.ApplicationClass;

import java.util.Date;

/**
 * Created by David on 06/02/14 for com.e9ine.cab9.driver.services.
 */
public class LocationMonitor implements LocationListener {
	public static LocationMonitor instance = null;

	public static Location lastReceivedLocation = null;
	public static Date lastReceivedTime = null;

	LocationReceiver mLocationReceiver = null;

	public static LocationMonitor getInstance(){
		if (instance == null)
			instance = new LocationMonitor();
		return instance;
	}
	private LocationMonitor(){}

	@Override
	public void onLocationChanged(Location location) {
		lastReceivedLocation = location;
		lastReceivedTime = new Date();
		if (mLocationReceiver != null) {
			mLocationReceiver.onLocationChanged(lastReceivedLocation, lastReceivedTime);
		}
		ApplicationClass.newLocation(location);
	}

	public void setLocationReceiver(LocationReceiver receiver) {
		mLocationReceiver = receiver;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}
}
