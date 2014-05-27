package com.e9ine.cab9.driver.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.fragments.BaseFragment;
import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.ServerModel;
import com.e9ine.cab9.driver.model.Shift;

import java.util.ArrayList;

/**
 * Created by David on 04/02/14 for com.e9ine.cab9.driver.adapters.
 */
public class BookingAdapter extends ArrayAdapter<Booking>{

	private Context mContext;
	private ArrayList<Booking> mBookings;
	private boolean mUpcoming;

	public BookingAdapter(Context context, ArrayList<Booking> bookings, Boolean upcoming) {
		super(context, R.layout.include_booking_list_item, bookings);
		mContext = context;
		mBookings = bookings;
		mUpcoming = upcoming;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).ID;
	}

	@Override
	public Booking getItem(int position) {
		return mBookings.get(getCount() - 1 - position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Booking current = getItem(position);
		View target;
		if (convertView != null) {
			target = convertView;
		} else {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			target = inflater.inflate(R.layout.include_booking_list_item, parent, false);
		}

		TextView title = ((TextView) target.findViewById(R.id.booking_list_item_id));
		title.setText(String.valueOf(current.LocalID));

		TextView subtitle = ((TextView) target.findViewById(R.id.booking_list_item_subtitle));
		subtitle.setText(current.From);

		if (!mUpcoming) {
			TextView time = ((TextView) target.findViewById(R.id.booking_list_item_time));
			long val = current.BookedDateTime.getTime();
			long diffSeconds = val / 1000 % 60;
			long diffMinutes = val / (60 * 1000) % 60;
			long diffHours = val / (60 * 60 * 1000) % 24;
			time.setText(String.format("%02d:%02d:%02d", diffHours, diffMinutes, diffSeconds));
			TextView distance = ((TextView) target.findViewById(R.id.booking_list_dist));
			distance.setText("");
		} else {
			TextView time = ((TextView) target.findViewById(R.id.booking_list_item_time));
			time.setText(ServerModel.PRETTY_DATE.format(current.BookedDateTime));
			TextView distance = ((TextView) target.findViewById(R.id.booking_list_dist));
			if (current.Distance != 0D)
				distance.setText(current.Distance.toString() + "m");
			else
				distance.setText("");
		}


		return target;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
