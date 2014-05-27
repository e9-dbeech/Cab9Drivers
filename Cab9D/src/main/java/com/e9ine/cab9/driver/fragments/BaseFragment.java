package com.e9ine.cab9.driver.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.e9ine.cab9.driver.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by David on 03/02/14 for com.e9ine.cab9.driver.fragments.
 */
public class BaseFragment extends Fragment {
	public static final SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat shortTimeFormat = new SimpleDateFormat("HH:mm");
	public static final SimpleDateFormat longDateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		SetFonts(view);
	}

	private void SetFonts(View view) {
		ViewGroup group = (ViewGroup) view.getParent();
		Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/helveticaneue.ttf");
		ArrayList<View> views = getViewsByTag(group, getString(R.string.font_helvetica));
		for(View v : views){
			if (v instanceof TextView) {
				((TextView) v).setTypeface(font);
			}
			if (v instanceof Button) {
				((Button) v).setTypeface(font);
			}
		}
	}

	private static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
		ArrayList<View> views = new ArrayList<View>();
		if (root == null) return views;
		final int childCount = root.getChildCount();
		for (int i = 0; i < childCount; i++) {
			final View child = root.getChildAt(i);
			if (child instanceof ViewGroup) {
				views.addAll(getViewsByTag((ViewGroup) child, tag));
			}
			final Object tagObj = child.getTag();
			if (tagObj != null && tagObj.equals(tag)) {
				views.add(child);
			}
		}
		return views;
	}
}
