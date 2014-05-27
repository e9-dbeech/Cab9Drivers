package com.e9ine.cab9.driver.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.fragments.BaseFragment;
import com.e9ine.cab9.driver.model.Shift;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by David on 04/02/14 for com.e9ine.cab9.driver.adapters.
 */
public class ShiftAdapter extends ArrayAdapter<Shift>{

	private Context mContext;
	private ArrayList<Shift> mShifts;

	public ShiftAdapter(Context context, ArrayList<Shift> shifts) {
		super(context, R.layout.include_shift_list_item, shifts);
		mContext = context;
		mShifts = shifts;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).ID;
	}

	@Override
	public Shift getItem(int position) {
		return mShifts.get(getCount() - 1 - position);
		//To reverse
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Shift current = getItem(position);
		View target;
		if (convertView != null) {
			target = convertView;
		} else {
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			target = inflater.inflate(R.layout.include_shift_list_item, parent, false);
		}
		//Font setting is too slow.
		TextView title = ((TextView) target.findViewById(R.id.shift_list_item_title));
		title.setText(BaseFragment.shortDateFormat.format(current.ShiftStart));

		//TextView subtitle = ((TextView) target.findViewById(R.id.shift_list_item_subtitle));
		//subtitle.setText(String.format("%d Miles Driven", current.MileageEnd - current.MileageStart));

		TextView total = ((TextView) target.findViewById(R.id.shift_list_item_dial));
		total.setText(String.format("%02d", current.TotalAccepted));

		if (current.Active) {
			total.setBackgroundResource(R.drawable.theme_dial_orange);
		} else {

			total.setBackgroundResource(R.drawable.theme_dial_green);
		}

		return target;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
