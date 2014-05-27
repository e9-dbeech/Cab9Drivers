package com.e9ine.cab9.driver.services;

import android.location.Location;

import java.util.Date;

/**
 * Created by David on 06/02/14 for com.e9ine.cab9.driver.services.
 */
public abstract class LocationReceiver {
	public abstract void onLocationChanged(Location location, Date date);
}
