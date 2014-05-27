package com.e9ine.cab9.driver.fragments;



import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.activities.MainActivity;
import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.ServerModel;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class BookingInProgressFragment extends Fragment {

	public static BookingInProgressFragment newInstance(){
		return new BookingInProgressFragment();
	}
    private BookingInProgressFragment() {
    }

	View mRootView;

	TextView mBookingId;
	TextView mFrom;
	TextView mTo;
	TextView mBookingTime;
	TextView mPassengerName;
	TextView mContact;
	TextView mFare;
	LinearLayout mStep1;
	LinearLayout mStep2;
	LinearLayout mStep3;
	LinearLayout mStep4;
	LinearLayout mFinished;
	Button mArrived;
	Button mPob;
	Button mNoShow;
	Button mClearing;
	Button mCompleted;
	Button mClose;
	Button mTurnByTurn;

	MapView mMapView;
	GoogleMap googleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_booking_inprogress, container, false);

	    mBookingId = (TextView) mRootView.findViewById(R.id.prog_id);
	    mFrom = (TextView) mRootView.findViewById(R.id.prog_from);
	    mTo = (TextView) mRootView.findViewById(R.id.prog_to);
	    mBookingTime = (TextView) mRootView.findViewById(R.id.prog_time);
	    mPassengerName = (TextView) mRootView.findViewById(R.id.prog_name);
	    mContact = (TextView) mRootView.findViewById(R.id.prog_contact);
	    mFare = (TextView) mRootView.findViewById(R.id.prog_fare);
	    mMapView = (MapView) mRootView.findViewById(R.id.prog_map_map);
	    mStep1 = (LinearLayout) mRootView.findViewById(R.id.prog_step1);
	    mStep2 = (LinearLayout) mRootView.findViewById(R.id.prog_step2);
	    mStep3 = (LinearLayout) mRootView.findViewById(R.id.prog_step3);
	    mStep4 = (LinearLayout) mRootView.findViewById(R.id.prog_step4);
	    mFinished = (LinearLayout) mRootView.findViewById(R.id.prog_finished);
	    mArrived = (Button) mRootView.findViewById(R.id.prog_status_arrived);
	    mPob = (Button) mRootView.findViewById(R.id.prog_status_pob);
	    mNoShow = (Button) mRootView.findViewById(R.id.prog_status_noshow);
	    mClearing = (Button) mRootView.findViewById(R.id.prog_status_clearing);
	    mCompleted = (Button) mRootView.findViewById(R.id.prog_status_completed);
	    mClose = (Button) mRootView.findViewById(R.id.prog_close);
	    mTurnByTurn = (Button) mRootView.findViewById(R.id.turn_by_turn);

	    return mRootView;
    }

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mMapView.onCreate(savedInstanceState);

	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		googleMap = mMapView.getMap();
		final ViewTreeObserver obs = mMapView.getViewTreeObserver();
		if (obs != null)
			obs.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					setupBooking(ApplicationClass.getCurrentBooking());
					if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						obs.removeGlobalOnLayoutListener(this);
					} else {
						obs.removeOnGlobalLayoutListener(this);
					}
				}
			});
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		mMapView.onLowMemory();
	}

	private void setupBooking(final Booking currentBooking) {
		if (currentBooking == null) {
			MainActivity act = (MainActivity) getActivity();
			act.selectFragment(MainActivity.BOOKINGS_CURRENT_FRAGMENT, null);
		} else {
			switch(currentBooking.Status){
				case 3:
					mStep1.setVisibility(View.VISIBLE);
					mStep2.setVisibility(View.GONE);
					mStep3.setVisibility(View.GONE);
					mStep4.setVisibility(View.GONE);
					mFinished.setVisibility(View.GONE);
					break;
				case 4:
					mStep1.setVisibility(View.INVISIBLE);
					mStep2.setVisibility(View.VISIBLE);
					mStep3.setVisibility(View.GONE);
					mStep4.setVisibility(View.GONE);
					mFinished.setVisibility(View.GONE);
					break;
				case 5:
					mStep1.setVisibility(View.INVISIBLE);
					mStep2.setVisibility(View.GONE);
					mStep3.setVisibility(View.VISIBLE);
					mStep4.setVisibility(View.GONE);
					mFinished.setVisibility(View.GONE);
					break;
				case 6:
					mStep1.setVisibility(View.INVISIBLE);
					mStep2.setVisibility(View.GONE);
					mStep3.setVisibility(View.GONE);
					mStep4.setVisibility(View.VISIBLE);
					mFinished.setVisibility(View.GONE);
					break;
				case 7:
					mStep1.setVisibility(View.INVISIBLE);
					mStep2.setVisibility(View.GONE);
					mStep3.setVisibility(View.GONE);
					mStep4.setVisibility(View.GONE);
					mFinished.setVisibility(View.VISIBLE);
					break;
				case 8:
					mStep1.setVisibility(View.INVISIBLE);
					mStep2.setVisibility(View.VISIBLE);
					mStep3.setVisibility(View.GONE);
					mStep4.setVisibility(View.GONE);
					mFinished.setVisibility(View.GONE);
					break;
				default:
					mStep1.setVisibility(View.INVISIBLE);
					mStep2.setVisibility(View.GONE);
					mStep3.setVisibility(View.GONE);
					mStep4.setVisibility(View.GONE);
					mFinished.setVisibility(View.VISIBLE);
					break;
			}
			mArrived.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleArrivedClick();
				}
			});
			mPob.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handlePobClick();
				}
			});
			mNoShow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleNoShowClick();
				}
			});
			mClearing.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleClearingClick();
				}
			});
			mCompleted.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleCompletedClick();
				}
			});
			mClose.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleCloseClick();
				}
			});
			mTurnByTurn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if (!TextUtils.isEmpty(currentBooking.EncodedJourney)) {
							List<LatLng> points = PolyUtil.decode(currentBooking.EncodedJourney);
							LatLng start = points.get(0);
							LatLng end = points.get(points.size() - 1);
							String origin = String.valueOf(start.latitude) + ',' + String.valueOf(start.longitude);
							String destin = String.valueOf(end.latitude) + ',' + String.valueOf(end.longitude);
							Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
									Uri.parse("http://maps.google.com/maps?saddr=" + origin + "&daddr=" + destin));
							startActivity(intent);
						}
					}
					catch (Exception exc) {}
				}
			});

			mBookingId.setText("#" + currentBooking.LocalID);
			mFrom.setText(currentBooking.From);
			mTo.setText(currentBooking.To);
			String type;
			if (currentBooking.PaymentMethod.equals(1))
				type = "(Cash) ";
			else
				type = "(Account) ";
			DecimalFormat twoPlaces = new DecimalFormat("#.##");
			mFare.setText(type + twoPlaces.format(currentBooking.ActualFare));

			mPassengerName.setText(currentBooking.PassengerName);
			mContact.setText(currentBooking.ContactNumber);
			mBookingTime.setText(ServerModel.PRETTY_DATE.format(currentBooking.BookedDateTime));

		}

	}

	private void handleCloseClick() {
		MainActivity act = (MainActivity) getActivity();
		act.selectFragment(MainActivity.BOOKINGS_CURRENT_FRAGMENT, null);
	}

	private void handleCompletedClick() {
		updateStatus(7);
	}

	private void handleClearingClick() {
		updateStatus(6);
	}

	private void handleNoShowClick() {
		updateStatus(-2);
	}

	private void handlePobClick() {
		updateStatus(5);
	}

	private void handleArrivedClick() {
		updateStatus(8);
	}

	private void updateStatus(final Integer status) {
		Booking.Api.UpdateStatusTask updateStatusTask = new Booking.Api.UpdateStatusTask() {
			@Override
			protected void onPreExecute() {
				mQueryValues.put("bookingId", ApplicationClass.getCurrentBooking().ID.toString());
				mQueryValues.put("status", status.toString());
			}

			@Override
			protected void onResultReturned(Booking result) {
				if (result.Status != 7) {
					ApplicationClass.setCurrentBooking(result);
				} else {
					ApplicationClass.setCurrentBooking(null);
				}
				setupBooking(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {

			}
		};
		updateStatusTask.execute();
	}
}
