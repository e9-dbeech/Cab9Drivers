package com.e9ine.cab9.driver.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.activities.MainActivity;
import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.Driver;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBoundsCreator;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by David on 05/02/14 for com.e9ine.cab9.driver.fragments.
 */
public class BookingsViewFragment extends Fragment {
	public static final String ARG_BOOKING_ID = "bookingid";

	TextView mBookingId;
	TextView mFrom;
	TextView mTo;
	TextView mBookingTime;
	TextView mPassengerName;
	TextView mFare;
	TextView mVehicle;
	MapView mMapView;
	GoogleMap googleMap;
	Button onRouteButton;

	Booking.Api.GetBookingByIdTask mGetBookingByIdTask;
	Booking mBooking;

	public static BookingsViewFragment newInstance()
	{
		return new BookingsViewFragment();
	}
	private BookingsViewFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_booking_view, container, false);

		mMapView = (MapView) rootView.findViewById(R.id.booking_map_map);
		mBookingId = (TextView) rootView.findViewById(R.id.bookings_text_id);
		mFrom = (TextView) rootView.findViewById(R.id.bookings_text_from);
		mTo = (TextView) rootView.findViewById(R.id.bookings_text_to);
		mBookingTime = (TextView) rootView.findViewById(R.id.bookings_text_time);
		mPassengerName = (TextView) rootView.findViewById(R.id.bookings_text_passenger);
		mVehicle = (TextView) rootView.findViewById(R.id.bookings_text_vehicle);
		mFare = (TextView) rootView.findViewById(R.id.bookings_text_fare);
		onRouteButton = (Button) rootView.findViewById(R.id.booking_button_onroute);

		return rootView;
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
				fetchBooking(getArguments().getLong(ARG_BOOKING_ID));
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
		if (mGetBookingByIdTask != null) {
			mGetBookingByIdTask.cancel(true);
			mGetBookingByIdTask = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();

		if (mUpdateStatusTask != null) {
			mUpdateStatusTask.cancel(true);
			mUpdateStatusTask = null;
		}
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

	public void fetchBooking(final long bookingid) {
		final Booking.LocalDB db = new Booking.LocalDB(getActivity());
		Booking booking = db.getBookingByID(bookingid);
		setupForBooking(booking);
		mGetBookingByIdTask = new Booking.Api.GetBookingByIdTask() {
			@Override
			protected void onPreExecute() {
				mQueryValues.put(ARG_BOOKING_ID, String.valueOf(bookingid));
			}

			@Override
			protected void onResultReturned(Booking result) {
				db.updateOrInsertBooking(result);
				setupForBooking(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {

			}
		};
		mGetBookingByIdTask.execute();
	}

	public void setupForBooking(Booking booking) {
		//TODO: Google call to get estimated time
		mBooking = booking;
		mBookingId.setText(String.valueOf(booking.LocalID));
		mFrom.setText(booking.From);
		mTo.setText(booking.To);
		mBookingTime.setText(BaseFragment.longDateTimeFormat.format(booking.BookedDateTime));
		mPassengerName.setText(booking.PassengerName);
		DecimalFormat twoPlaces = new DecimalFormat("#.##");
		mFare.setText("£" + twoPlaces.format(booking.ActualFare));
		mVehicle.setText("Vehicle");

		if (booking.Status != 2)
			onRouteButton.setVisibility(View.GONE);
		else {
			onRouteButton.setVisibility(View.VISIBLE);
			onRouteButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					handleOnRouteNowClick();
				}
			});
		}

		googleMap = mMapView.getMap();
		googleMap.clear();
		List<LatLng> points = PolyUtil.decode(booking.EncodedJourney);
		googleMap.addPolyline(new PolylineOptions()
			.addAll(points));

		LatLngBounds.Builder builder = LatLngBounds.builder();
		for (LatLng point : points){
			builder.include(point);
		}

		googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 20));
	}

	Booking.Api.UpdateStatusTask mUpdateStatusTask;
	public void handleOnRouteNowClick(){
		//TODO: checks to see if can commence job
		mUpdateStatusTask = new Booking.Api.UpdateStatusTask() {
			@Override
			protected void onPreExecute() {
				mQueryValues.put("bookingId", mBooking.ID.toString());
				mQueryValues.put("status", "4");
			}

			@Override
			protected void onResultReturned(Booking result) {
				handleStatusChanged(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {

			}
		};
		mUpdateStatusTask.execute();
	}

	private void handleStatusChanged(Booking target) {
		Booking.LocalDB db = new Booking.LocalDB(getActivity());
		db.updateOrInsertBooking(target);
		Driver driver = ApplicationClass.getCurrentDriver();
		driver.CurrentBookingID = target.ID;
		ApplicationClass.setCurrentDriver(driver, false);
		ApplicationClass.setCurrentBooking(target);
		MainActivity act = (MainActivity) getActivity();
		act.selectFragment(MainActivity.BOOKINGS_INPROGRESS_FRAGMENT, null);
	}
}
