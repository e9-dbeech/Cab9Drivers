package com.e9ine.cab9.driver.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.e9ine.cab9.driver.R;

/**
 * Created by David on 07/02/14 for com.e9ine.cab9.driver.fragments.
 */
public class ShiftStartFragment extends Fragment {
	public static ShiftStartFragment newInstance() {
		return new ShiftStartFragment();
	}
	private ShiftStartFragment(){}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_shift_new, container, false);
	}
}
