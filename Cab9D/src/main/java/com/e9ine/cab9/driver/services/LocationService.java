package com.e9ine.cab9.driver.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.HandlerThread;
import android.os.IBinder;

public class LocationService extends Service {
    public static final String ACTION_START_MONITORING = "com.e9ine.cab9.driver.services.action.START_LOCATION_MONITORING";
	public static final String ACTION_END_MONITORING = "com.e9ine.cab9.driver.services.action.END_LOCATION_MONITORING";
	public static final String ACTION_CHANGE_FREQUENCY = "com.e9ine.cab9.driver.services.action.CHANGE_LOCATION_MONITORING_FREQUENCY";

	public static final String EXTRA_FREQUENCY = "FREQUENCY";
	public static final int SHORT_FREQUENCY = 15000;
	public static final int DEFAULT_FREQUENCY = 30000;
	public static final int LONG_FREQUENCY = 45000;

	int mFrequency = DEFAULT_FREQUENCY;
	HandlerThread mHandlerThread;
	LocationMonitor mLocationListener = null;

    public static void startLocationMonitoring(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_START_MONITORING);
        context.startService(intent);
    }

    public static void endLocationMonitoring(Context context) {
        Intent intent = new Intent(context, LocationService.class);
        intent.setAction(ACTION_END_MONITORING);
        context.startService(intent);
    }

	public static void changeLocationMonitoringFrequency(Context context, int seconds) {
		Intent intent = new Intent(context, LocationService.class);
		intent.setAction(ACTION_CHANGE_FREQUENCY);
		intent.putExtra(EXTRA_FREQUENCY, seconds);
		context.startService(intent);
	}

    public LocationService() {
        super();
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mHandlerThread = new HandlerThread("LocationService");
		mHandlerThread.start();
	}

	@Override
	public void onDestroy() {
		mHandlerThread.quit();
		mHandlerThread = null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_START_MONITORING.equals(action)) {
				handleActionStart();
			} else if (ACTION_END_MONITORING.equals(action)) {
				handleActionEnd();
			} else if (ACTION_CHANGE_FREQUENCY.equals(action)) {
				int freq = intent.getIntExtra(EXTRA_FREQUENCY, DEFAULT_FREQUENCY);
				handleChangeFreq(freq);
			}
		}
		return START_REDELIVER_INTENT;
	}

	private void handleActionStart() {
		if(mLocationListener == null) {
			LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
			mLocationListener = LocationMonitor.getInstance();
			LocationMonitor.lastReceivedLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, mFrequency, 10, mLocationListener, mHandlerThread.getLooper());
		}
	}

	private void handleActionEnd() {
		if(mLocationListener != null) {
			LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
			lm.removeUpdates(mLocationListener);
			mLocationListener = null;
		}
		stopSelf();
	}

	private void handleChangeFreq(int freq) {
		mFrequency = freq;
		if(mLocationListener != null) {
			LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
			lm.removeUpdates(mLocationListener);
			mLocationListener = null;
		}
		if(mLocationListener == null) {
			LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
			mLocationListener = LocationMonitor.getInstance();
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, mFrequency, 10, mLocationListener, mHandlerThread.getLooper());
		}
	}

	public void setLocationReceiver(LocationReceiver receiver) {
		if (mLocationListener != null) {
			mLocationListener.setLocationReceiver(receiver);
		}
	}
}
