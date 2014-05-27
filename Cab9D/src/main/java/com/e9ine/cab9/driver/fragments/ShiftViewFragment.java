package com.e9ine.cab9.driver.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.activities.MainActivity;
import com.e9ine.cab9.driver.model.Shift;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by David on 06/01/14 for com.e9ine.cab9.driver.fragments.
 */
public class ShiftViewFragment extends BaseFragment {
	public static final String ARG_SHIFTID = "shiftid";

	View mRootView;

	View mShiftInfo;
	TextView mShiftDate;
	TextView mCountUpText;
	TextView mStartTimeText;
	TextView mBookingsDial;
	TextView mAcceptedDial;
	TextView mMissedDial;
	TextView mRejectedDial;
	Button mPreviousShiftsBtn;
	Button mBookingsBtn;
	Button mEndShiftBtn;
	Button mChangeDetailsBtn;

	Long mStartTime;
	Shift.Api.GetShiftByIdTask mGetShiftByIdTask;

	Timer clockTimer;

	public static ShiftViewFragment newInstance(){
		return new ShiftViewFragment();
	}
	private ShiftViewFragment() {}

	public static Bundle defaultArguments(){
		Bundle result = new Bundle();
		result.putLong(ARG_SHIFTID, ApplicationClass.getCurrentDriver().CurrentShiftID);
		return result;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = inflater.inflate(R.layout.fragment_shifts_view, container, false);

		mShiftInfo = mRootView.findViewById(R.id.shifts_info_box);
		mShiftDate = (TextView) mRootView.findViewById(R.id.shifts_text_date);
		mCountUpText = (TextView) mRootView.findViewById(R.id.shifts_text_length);
		mStartTimeText = (TextView) mRootView.findViewById(R.id.shifts_text_start);
		mBookingsDial = (TextView) mRootView.findViewById(R.id.shifts_dial_bookings);
		mAcceptedDial = (TextView) mRootView.findViewById(R.id.shifts_dial_accepted);
		mMissedDial = (TextView) mRootView.findViewById(R.id.shifts_dial_missed);
		mRejectedDial = (TextView) mRootView.findViewById(R.id.shifts_dial_rejected);

		mPreviousShiftsBtn = (Button) mRootView.findViewById(R.id.shifts_button_previous);
		mBookingsBtn = (Button) mRootView.findViewById(R.id.shifts_button_viewbookings);
		mEndShiftBtn = (Button) mRootView.findViewById(R.id.shifts_button_endshift);
		mChangeDetailsBtn = (Button) mRootView.findViewById(R.id.shifts_button_details);

		return mRootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getArguments() != null && getArguments().containsKey(ARG_SHIFTID) && getArguments().getLong(ARG_SHIFTID) != ApplicationClass.getCurrentDriver().CurrentShiftID) {
			fetchShift(getArguments().getLong(ARG_SHIFTID));
		} else {
			setupForShift(ApplicationClass.getCurrentShift());
			fetchShift(ApplicationClass.getCurrentDriver().CurrentShiftID);
		}
	}


	private void fetchShift(long currentShiftID) {
		final Shift.LocalDB db = new Shift.LocalDB(getActivity());
		Shift shift = db.getShiftByID(currentShiftID);
		if (shift != null) {
			setupForShift(shift);
		}

		mGetShiftByIdTask = new Shift.Api.GetShiftByIdTask() {
			@Override
			protected void onResultReturned(Shift result) {
				db.updateOrInsertShift(result);
				setupForShift(result);
			}

			@Override
			protected void onErrorResponse(int failureCode) {
			}
		};
		mGetShiftByIdTask.execute(currentShiftID);
	}

	private void setupForShift(final Shift shift) {
		if (shift == null) return;

		if (getActivity() != null)
		mShiftInfo.setBackgroundColor(getResources().getColor((shift.Active) ? R.color.theme_orange : R.color.theme_green));
		mEndShiftBtn.setVisibility((shift.Active) ? View.VISIBLE : View.GONE);
		mChangeDetailsBtn.setVisibility((shift.Active) ? View.VISIBLE : View.GONE);

		mShiftDate.setText(shortTimeFormat.format(shift.ShiftStart));
		mBookingsDial.setText(String.format("%02d", shift.TotalBookings));
		mAcceptedDial.setText(String.format("%02d", shift.TotalAccepted));
		mMissedDial.setText(String.format("%02d", shift.TotalMissed));
		mRejectedDial.setText(String.format("%02d", shift.TotalRejected));
		if (shift.Active) {
			mStartTimeText.setText("Started: " + shortTimeFormat.format(shift.ShiftStart));
			mStartTime = shift.ShiftStart.getTime();
			clockTimer = new Timer();
			RunClockTask task = new RunClockTask();
			task.run();
			clockTimer.schedule(task, 1000, 1000);
		} else {
			if (clockTimer != null) {
				clockTimer.cancel();
				clockTimer = null;
			}
			mStartTimeText.setText(shortTimeFormat.format(shift.ShiftStart) + " - " + shortTimeFormat.format(shift.ShiftEnd));
			long diff = shift.ShiftEnd.getTime() - shift.ShiftStart.getTime();
			final long diffSeconds = diff / 1000 % 60;
			final long diffMinutes = diff / (60 * 1000) % 60;
			final long diffHours =diff / (60 * 60 * 1000);
			mCountUpText.setText(String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
		}

		mBookingsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle args = new Bundle();
				args.putLong(BookingsListFragment.ARG_SHIFTID, shift.ID);
				((MainActivity) getActivity()).selectFragment(MainActivity.BOOKINGS_TABLE_FRAGMENT, args);
			}
		});

		mPreviousShiftsBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((MainActivity) getActivity()).selectFragment(MainActivity.SHIFT_TABLE_FRAGMENT, null);
			}
		});
	}

	private class RunClockTask extends TimerTask {
		@Override
		public void run() {
			Date date = new Date();
			long diff = date.getTime() - mStartTime;
			final long diffSeconds = diff / 1000 % 60;
			final long diffMinutes = diff / (60 * 1000) % 60;
			final long diffHours = diff / (60 * 60 * 1000);
			getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					mCountUpText.setText(String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
				}
			});
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mGetShiftByIdTask != null) {
			mGetShiftByIdTask.cancel(true);
			mGetShiftByIdTask = null;
		}
		if (clockTimer != null) {
			clockTimer.cancel();
			clockTimer = null;
		}
	}
}
