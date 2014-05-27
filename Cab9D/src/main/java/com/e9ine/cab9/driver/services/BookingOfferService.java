package com.e9ine.cab9.driver.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.activities.BookingOfferActivity;
import com.e9ine.cab9.driver.activities.MainActivity;
import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class BookingOfferService extends IntentService {
	private static int notificationId = 999999;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public BookingOfferService() {
		super("BookingOfferService");
	}

    @Override
    protected void onHandleIntent(Intent intent) {
	    Bundle extras = intent.getExtras();
	    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
	    String messageType = gcm.getMessageType(intent);

	    if (!extras.isEmpty()) {
		    if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
			    mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

			    Intent newOfferIntent = new Intent(this, BookingOfferActivity.class);
			    newOfferIntent.setAction("NewOfferAction");
			    newOfferIntent.putExtra(BookingOfferActivity.FROM_EXTRA, intent.getStringExtra(BookingOfferActivity.FROM_EXTRA));
			    newOfferIntent.putExtra(BookingOfferActivity.TO_EXTRA, intent.getStringExtra(BookingOfferActivity.TO_EXTRA));
			    newOfferIntent.putExtra(BookingOfferActivity.FARE_EXTRA, intent.getStringExtra(BookingOfferActivity.FARE_EXTRA));
			    newOfferIntent.putExtra(BookingOfferActivity.BOOKING_OFFER_ID_EXTRA, intent.getStringExtra(BookingOfferActivity.BOOKING_OFFER_ID_EXTRA));
			    newOfferIntent.putExtra(BookingOfferActivity.BOOKING_ID_EXTRA, intent.getStringExtra(BookingOfferActivity.BOOKING_ID_EXTRA));
			    newOfferIntent.putExtra(BookingOfferActivity.METHOD_EXTRA, intent.getStringExtra(BookingOfferActivity.METHOD_EXTRA));
			    newOfferIntent.putExtra(BookingOfferActivity.DATE_EXTRA, intent.getStringExtra(BookingOfferActivity.DATE_EXTRA));
			    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, newOfferIntent, 0);

			    newOfferIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    startActivity(newOfferIntent);


			    NotificationCompat.Builder mBuilder =
					    new NotificationCompat.Builder(this)
							    .setSmallIcon(R.drawable.ic_launcher)
							    .setContentTitle("New Booking Offer")
							    .setStyle(new NotificationCompat.BigTextStyle().bigText("You have a new booking offer."))
							    .setContentText("You have a new booking offer.");

			    mBuilder.setContentIntent(contentIntent);
			    mNotificationManager.notify(notificationId, mBuilder.build());


		    }
	    }


	    BookingOfferBroadcastReceiver.completeWakefulIntent(intent);
    }
}
