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
import com.e9ine.cab9.driver.adapters.ShiftAdapter;
import com.e9ine.cab9.driver.model.ServerModel;
import com.e9ine.cab9.driver.model.Shift;

import java.util.ArrayList;
import java.util.Date;

public class ShiftsListFragment extends ListFragment {
    public static final String ARG_DRIVERID = "driverid";
	public static final String ARG_FROM = "from";
	public static final String ARG_TO = "to";

	private ArrayList<Shift> mShifts = new ArrayList<Shift>();
	private ShiftAdapter mShiftAdapter;

	private Shift.Api.GetShiftsTask mGetShiftsTask;

	public static Bundle defaultArguments(){
		Bundle result = new Bundle();
		result.putInt(ARG_DRIVERID, ApplicationClass.getCurrentDriver().ID);
		result.putLong(ARG_FROM, new Date().getTime() - 2630000000L);
		return result;
	}

	public static ShiftsListFragment newInstance(){
		return new ShiftsListFragment();
	}
	private ShiftsListFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_shifts_table, container, false);

		mShiftAdapter = new ShiftAdapter(getActivity(), mShifts);
		setListAdapter(mShiftAdapter);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getArguments() != null && getArguments().containsKey(ARG_DRIVERID)){
			fetchShifts(getArguments());
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mGetShiftsTask != null) {
			mGetShiftsTask.cancel(true);
			mGetShiftsTask = null;
		}
	}

	public void fetchShifts(Bundle arguments) {
		final Integer driverid = arguments.getInt(ARG_DRIVERID, -1);
		long from = arguments.getLong(ARG_FROM, -1);
		long to = arguments.getLong(ARG_TO, -1);
		final Date dFrom = (from != -1) ? new Date(from) : null;
		final Date dTo = (to != -1) ? new Date(to) : null;

		final Shift.LocalDB db = new Shift.LocalDB(getActivity());
		mShifts.clear();
		mShifts.addAll(db.getAllShifts());
		if (mShiftAdapter != null) {
			mShiftAdapter.notifyDataSetChanged();
		}

		mGetShiftsTask = new Shift.Api.GetShiftsTask() {
			@Override
			protected void onPreExecute() {
				mQueryValues.put(ARG_DRIVERID, driverid.toString());
				if (dFrom != null)
					mQueryValues.put(ARG_FROM, ServerModel.ISO_8601.format(dFrom));
				if (dTo != null)
					mQueryValues.put(ARG_TO, ServerModel.ISO_8601.format(dTo));
				//Show Spinner
			}

			@Override
			protected void onResultReturned(ArrayList<Shift> result) {
				db.updateOrInsertManyShifts(result);
				mShifts.clear();
				mShifts.addAll(db.getAllShifts());
				if (mShiftAdapter != null) {
					mShiftAdapter.notifyDataSetChanged();
				}
			}

			@Override
			protected void onErrorResponse(int failureCode) {}
		};
		mGetShiftsTask.execute();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Shift shift = mShiftAdapter.getItem(position);
		Bundle args = new Bundle();
		args.putLong(ShiftViewFragment.ARG_SHIFTID, shift.ID);
		((MainActivity) getActivity()).selectFragment(MainActivity.SHIFT_VIEW_FRAGMENT, args);
	}
}
