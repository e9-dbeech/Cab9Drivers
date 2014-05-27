package com.e9ine.cab9.driver.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.activities.MainActivity;
import com.e9ine.cab9.driver.adapters.BookingAdapter;
import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.ServerModel;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by David on 05/02/14 for com.e9ine.cab9.driver.fragments.
 */
public class BookingsListFragment extends ListFragment {
	public static final String ARG_DRIVERID = "driverid";
	public static final String ARG_SHIFTID = "shiftid";
	public static final String ARG_FROM = "from";
	public static final String ARG_TO = "to";

	private ListView mShiftsList;

	private ArrayList<Booking> mBookings = new ArrayList<Booking>();
	private BookingAdapter mBookingAdapter;

	private Booking.Api.GetBookingsTask mGetBookingsTask;

	public static BookingsListFragment newInstance(){
		return new BookingsListFragment();
	}
	private BookingsListFragment() {}

	public static Bundle defaultArguments() {
		Bundle result = new Bundle();
		result.putInt(ARG_DRIVERID, ApplicationClass.getCurrentUser().UserID);
		result.putLong(ARG_SHIFTID, ApplicationClass.getCurrentDriver().CurrentShiftID);
		return result;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_booking_table, container, false);

		mBookingAdapter = new BookingAdapter(getActivity(), mBookings, false);
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
		setupForBookings(db.getBookingsForShift(arguments.getLong(ARG_SHIFTID)));

		mGetBookingsTask = new Booking.Api.GetBookingsTask(){
			@Override
			protected void onPreExecute() {
				if (arguments.containsKey(ARG_DRIVERID))
					mQueryValues.put(ARG_DRIVERID, String.valueOf(arguments.getInt(ARG_DRIVERID)));

				if (arguments.containsKey(ARG_SHIFTID))
					mQueryValues.put(ARG_SHIFTID, String.valueOf(arguments.getLong(ARG_SHIFTID)));

				if (arguments.containsKey(ARG_FROM))
					mQueryValues.put(ARG_FROM, ServerModel.ISO_8601.format(new Date(arguments.getLong(ARG_FROM))));

				if (arguments.containsKey(ARG_TO))
					mQueryValues.put(ARG_TO, ServerModel.ISO_8601.format(new Date(arguments.getLong(ARG_TO))));
			}

			@Override
			protected void onResultReturned(ArrayList<Booking> result) {
				db.updateOrInsertManyBookings(result);
				setupForBookings(db.getBookingsForShift(arguments.getLong(ARG_SHIFTID)));
			}

			@Override
			protected void onErrorResponse(int failureCode) {

			}
		};
		mGetBookingsTask.execute();
	}

	private void setupForBookings(ArrayList<Booking> result) {
		mGetBookingsTask = null;
		mBookings.clear();
		mBookings.addAll(result);
		if (mBookingAdapter != null) {
			mBookingAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Booking booking = mBookingAdapter.getItem(position);
		long temp = id;
		Bundle args = new Bundle();
		args.putLong(BookingsViewFragment.ARG_BOOKING_ID, booking.ID);
		((MainActivity) getActivity()).selectFragment(MainActivity.BOOKINGS_VIEW_FRAGMENT, args);
	}
}
