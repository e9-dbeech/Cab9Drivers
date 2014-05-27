package com.e9ine.cab9.driver.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class BookingOfferBroadcastReceiver extends WakefulBroadcastReceiver {
    public BookingOfferBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
	    ComponentName comp = new ComponentName(context.getPackageName(),
			    BookingOfferService.class.getName());
	    // Start the service, keeping the device awake while it is launching.
	    startWakefulService(context, (intent.setComponent(comp)));
	    setResultCode(Activity.RESULT_OK);
    }
}
