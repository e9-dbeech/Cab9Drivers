package com.e9ine.cab9.driver.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.BookingOffer;

/**
 * Created by David on 11/02/14 for com.e9ine.cab9.driver.activities.
 */
public class BookingOfferActivity extends Activity {
	public static final String BOOKING_OFFER_ID_EXTRA = "bookingOfferId";
	public static final String BOOKING_ID_EXTRA = "bookingId";
	public static final String FROM_EXTRA = "origin";
	public static final String TO_EXTRA = "destin";
	public static final String FARE_EXTRA = "fare";
	public static final String METHOD_EXTRA = "method";
	public static final String DATE_EXTRA = "date";

	TextView from;
	TextView to;
	TextView fare;
	TextView date;
	Button accept;
	Button reject;

	BookingOffer.Api.MarkAsRead readAttempt;
	BookingOffer.Api.AcceptBookingOffer acceptAttempt;
	BookingOffer.Api.RejectBookingOffer rejectAttempt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offer);

		from = (TextView) findViewById(R.id.offer_text_from);
		to = (TextView) findViewById(R.id.offer_text_to);
		fare = (TextView) findViewById(R.id.offer_text_fare);
		date = (TextView) findViewById(R.id.offer_text_date);
		accept = (Button) findViewById(R.id.offer_button_accept);
		reject = (Button) findViewById(R.id.offer_button_reject);

		Bundle extras = getIntent().getExtras();
		from.setText(extras.getString(FROM_EXTRA));
		to.setText(extras.getString(FROM_EXTRA));
		fare.setText("£" + extras.getString(FARE_EXTRA) + " (" + extras.getString(METHOD_EXTRA) + ")");
		date.setText(extras.getString(DATE_EXTRA));

		final Activity that = this;
		final String bookingOfferId = extras.getString(BOOKING_OFFER_ID_EXTRA);

		accept.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				acceptAttempt = new BookingOffer.Api.AcceptBookingOffer() {
					@Override
					protected void onPreExecute() {
						mQueryValues.put("offerid", bookingOfferId);
					}

					@Override
					protected void onResultReturned(Booking result) {
						that.finish();
					}

					@Override
					protected void onErrorResponse(int failureCode) {
						that.finish();
					}
				};
				acceptAttempt.execute();
			}
		});

		reject.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rejectAttempt = new BookingOffer.Api.RejectBookingOffer() {
					@Override
					protected void onPreExecute() {
						mQueryValues.put("offerid", bookingOfferId);
						mQueryValues.put("reason", "Non Supplied");
					}

					@Override
					protected void onResultReturned(Void result) {
						that.finish();
					}

					@Override
					protected void onErrorResponse(int failureCode) {
						that.finish();
					}
				};
				rejectAttempt.execute();
			}
		});


		readAttempt = new BookingOffer.Api.MarkAsRead() {
			@Override
			protected void onPreExecute() {
				mQueryValues.put("offerid", bookingOfferId);
			}

			@Override
			protected void onResultReturned(Void result) {

			}

			@Override
			protected void onErrorResponse(int failureCode) {

			}
		};
		readAttempt.execute();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (readAttempt != null) {
			readAttempt.cancel(true);
			readAttempt = null;
		}
		if (acceptAttempt != null) {
			acceptAttempt.cancel(true);
			acceptAttempt = null;
		}
		if (rejectAttempt != null) {
			rejectAttempt.cancel(true);
			rejectAttempt = null;
		}
	}
}
