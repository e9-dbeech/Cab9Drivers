package com.e9ine.cab9.driver.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.activities.MainActivity;
import com.e9ine.cab9.driver.adapters.BookingAdapter;
import com.e9ine.cab9.driver.api.AsyncGetGoogleMatrix;
import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.ServerModel;
import com.e9ine.cab9.driver.services.LocationMonitor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by David on 05/02/14 for com.e9ine.cab9.driver.fragments.
 */
public class BookingsUpcomingListFragment extends ListFragment {
	public static final String ARG_DRIVERID = "driverId";
	public static final String ARG_FROM = "from";
	public static final String ARG_TO = "to";

	private ListView mShiftsList;

	private ArrayList<Booking> mBookings = new ArrayList<Booking>();
	private BookingAdapter mBookingAdapter;

	private Booking.Api.UpcomingBookingsTask mGetBookingsTask;
	AsyncGetGoogleMatrix googleMatrixTask;

	public static BookingsUpcomingListFragment newInstance(){
		return new BookingsUpcomingListFragment();
	}
	private BookingsUpcomingListFragment() {}

	public static Bundle defaultArguments() {
		Bundle result = new Bundle();
		result.putInt(ARG_DRIVERID, ApplicationClass.getCurrentUser().UserID);
		return result;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_booking_table, container, false);

		mBookingAdapter = new BookingAdapter(getActivity(), mBookings, true);
		setListAdapter(mBookingAdapter);

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		if (getArguments() != null){
			fetchBookings(getArguments());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mGetBookingsTask != null) {
			mGetBookingsTask.cancel(true);
			mGetBookingsTask = null;
		}
	}

	private void fetchBookings(final Bundle arguments) {
		final Booking.LocalDB db = new Booking.LocalDB(getActivity());
		setupForBookings(db.getUpcomingBookings());

		mGetBookingsTask = new Booking.Api.UpcomingBookingsTask(){
			@Override
			protected void onPreExecute() {
				if (arguments.containsKey(ARG_DRIVERID))
					mQueryValues.put(ARG_DRIVERID, String.valueOf(arguments.getInt(ARG_DRIVERID)));
			}

			@Override
			protected void onResultReturned(ArrayList<Booking> result) {
				db.updateOrInsertManyBookings(result);
				setupForBookings(db.getUpcomingBookings());
				getDistancesForBookings(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {
			}
		};
		mGetBookingsTask.execute();
	}

	private void setupForBookings(ArrayList<Booking> result) {
		mBookings.clear();
		mBookings.addAll(result);
		if (mBookingAdapter != null) {
			mBookingAdapter.notifyDataSetChanged();
		}

	}

	private void reSetupForBookings() {
		if (mBookingAdapter != null) {
			mBookingAdapter.notifyDataSetChanged();
		}

	}

	private void getDistancesForBookings(final ArrayList<Booking> result) {
		Location l = LocationMonitor.lastReceivedLocation;
		final LatLng ll = new LatLng(l.getLatitude(), l.getLongitude());
		final ArrayList<LatLng> latLngs = new ArrayList<LatLng>();
		for (Booking b : result) {
			if (!TextUtils.isEmpty(b.EncodedJourney)) {
				List<LatLng> points = PolyUtil.decode(b.EncodedJourney);
				if (points.size() > 0)
					latLngs.add(points.get(0));
			}
		}

		googleMatrixTask = new AsyncGetGoogleMatrix() {
			@Override
			protected void onPreExecute() {
				mOrigin = ll;
				mDestinations.addAll(latLngs);
			}

			@Override
			protected void onPostExecute(JSONObject jsonObject) {
				super.onPostExecute(jsonObject);
				handleDistancesResult(jsonObject);
			}
		};
		googleMatrixTask.execute();
	}

	private void handleDistancesResult(JSONObject jsonObject) {
		try {
			if (jsonObject.getString("status").equals("OK")) {
				JSONArray rows = jsonObject.getJSONArray("rows");
				JSONObject elements = rows.getJSONObject(0);
				JSONArray results = elements.getJSONArray("elements");
				Booking.LocalDB db = new Booking.LocalDB(getActivity());
				for (int i = 0; i < mBookings.size(); i++) {
					Booking b = mBookings.get(i);
					JSONObject obj = results.getJSONObject(i);
					if (obj.getString("status").equals("OK")) {
						int Km = obj.getJSONObject("distance").getInt("value");
						b.Distance = Math.round(Km / 1000 * 0.621371192 * 100.0) / 100.0;
						db.updateOrInsertBooking(b);
					}
				}
				reSetupForBookings();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Booking booking = mBookingAdapter.getItem(position);
		Bundle args = new Bundle();
		args.putLong(BookingsViewFragment.ARG_BOOKING_ID, booking.ID);
		((MainActivity) getActivity()).selectFragment(MainActivity.BOOKINGS_VIEW_FRAGMENT, args);
	}
}
