package com.e9ine.cab9.driver.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e9ine.cab9.driver.ApplicationClass;
import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.fragments.AccountsFragment;
import com.e9ine.cab9.driver.fragments.BookingInProgressFragment;
import com.e9ine.cab9.driver.fragments.BookingsListFragment;
import com.e9ine.cab9.driver.fragments.BookingsUpcomingListFragment;
import com.e9ine.cab9.driver.fragments.BookingsViewFragment;
import com.e9ine.cab9.driver.fragments.DashboardFragment;
import com.e9ine.cab9.driver.fragments.GoogleMapFragment;
import com.e9ine.cab9.driver.fragments.NavigationDrawerFragment;
import com.e9ine.cab9.driver.fragments.ProfileFragment;
import com.e9ine.cab9.driver.fragments.ShiftStartFragment;
import com.e9ine.cab9.driver.fragments.ShiftViewFragment;
import com.e9ine.cab9.driver.fragments.ShiftsListFragment;
import com.e9ine.cab9.driver.model.Credentials;
import com.e9ine.cab9.driver.services.LocationService;

/**
 * Created by David on 06/01/14 for com.e9ine.cab9.driver.activities.
 */
public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks {
	private static final String LOG_TAG = "MAIN_ACTIVITY";

	public static final String FRAGMENT_EXTRA = "SCREEN";
	public static final int DASHBOARD_FRAGMENT = 0;
	public static final int MAP_FRAGMENT = 100;
	public static final int SHIFT_VIEW_FRAGMENT = 201;
	public static final int SHIFT_TABLE_FRAGMENT = 202;
	public static final int BOOKINGS_VIEW_FRAGMENT = 301;
	public static final int BOOKINGS_TABLE_FRAGMENT = 302;
	public static final int BOOKINGS_INPROGRESS_FRAGMENT = 303;
	public static final int BOOKINGS_CURRENT_FRAGMENT = 304;
	public static final int ACCOUNTS_FRAGMENT = 4;
	public static final int PROFILE_FRAGMENT = 5;
	public static final int BOOKING_FRAGMENT = 6;

	private NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;

	RelativeLayout spinner_overlay;
	ProgressBar spinner;
	TextView spinner_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.i(LOG_TAG, "Main Activity Creating.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		spinner_overlay = (RelativeLayout) findViewById(R.id.global_spinner_container);
		spinner = (ProgressBar) findViewById(R.id.global_spinner);
		spinner_status = (TextView) findViewById(R.id.global_spinner_message);

		mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		int fragmentId = checkCallingIntentForFragmentId();
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
		selectFragment(fragmentId, null);
	}

	private int checkCallingIntentForFragmentId() {
		Intent callingIntent = getIntent();
		int fragment = DASHBOARD_FRAGMENT;
		if (callingIntent != null) {
			Bundle extras = callingIntent.getExtras();
			if (extras != null) {
				fragment = extras.getInt(FRAGMENT_EXTRA);
				Log.i(LOG_TAG, String.format("Main Activity Fragment Requested: %d", fragment));
			}
		}
		return fragment;
	}

	public void selectFragment(int fragmentId, Bundle args) {
		Log.i(LOG_TAG, String.format("Main Activity Selecting Fragment: %d", fragmentId));
		FragmentManager fragmentManager = getSupportFragmentManager();
		Fragment fragment;
		String backStack;
		int position;
		switch (fragmentId) {
			case DASHBOARD_FRAGMENT:
				fragment = new DashboardFragment();
				backStack = "DASHBOARD";
				position = 0;
				break;
			case MAP_FRAGMENT:
				fragment = new GoogleMapFragment();
				backStack = "MAP";
				position = 1;
				break;
			case SHIFT_TABLE_FRAGMENT:
				if (args == null)
					args = ShiftsListFragment.defaultArguments();
				fragment = ShiftsListFragment.newInstance();
				backStack = "SHIFT_TABLE";
				position = 2;
				break;
			case SHIFT_VIEW_FRAGMENT:
				if (args == null) {
					if (ApplicationClass.getCurrentDriver().CurrentShiftID == -1) {
						fragment = ShiftStartFragment.newInstance();
						backStack = "SHIFT_NEW";
						position = 2;
						break;
					}
				}
				fragment = ShiftViewFragment.newInstance();
				backStack = "SHIFT_VIEW";
				position = 2;
				break;
			case BOOKINGS_TABLE_FRAGMENT:
				if (args == null)
					args = BookingsListFragment.defaultArguments();
				fragment = BookingsListFragment.newInstance();
				backStack = "BOOKINGS_TABLE";
				position = 3;
				break;
			case BOOKINGS_VIEW_FRAGMENT:
				//if (args == null)
				//	args = BookingsViewFragment.defaultArguments();
				fragment = BookingsViewFragment.newInstance();
				backStack = "BOOKINGS_VIEW";
				position = 3;
				break;
			case BOOKINGS_INPROGRESS_FRAGMENT:
				fragment = BookingInProgressFragment.newInstance();
				backStack = "CURRENT_BOOKING_VIEW";
				position = 3;
				break;
			case ACCOUNTS_FRAGMENT:
				fragment = AccountsFragment.newInstance();
				backStack = "ACCOUNTS";
				position = 4;
				break;
			case PROFILE_FRAGMENT:
				fragment = ProfileFragment.newInstance(ApplicationClass.getCurrentDriver().ID);
				backStack = "PROFILE";
				position = 5;
				break;
			case BOOKINGS_CURRENT_FRAGMENT:
				fragment = BookingsUpcomingListFragment.newInstance();
				args = BookingsUpcomingListFragment.defaultArguments();
				backStack = "UPCOMING";
				position = 3;
				break;
			default:
				fragment = new DashboardFragment();
				backStack = "DASHBOARD";
				position = 0;
				break;
		}
		fragment.setArguments(args);
		FragmentTransaction transaction = fragmentManager.beginTransaction()
				.setCustomAnimations(R.anim.fragment_slide_in, R.anim.fragment_slide_out)
				.replace(R.id.fragment_container, fragment);

		if (backStack != null)
			transaction.addToBackStack(backStack);

		transaction.commit();
		mNavigationDrawerFragment.selectPosition(position);
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		int pos;
		switch (position) {
			case 1:
				pos = MAP_FRAGMENT;
				break;
			case 2:
				pos = SHIFT_VIEW_FRAGMENT;
				break;
			case 3:
				pos = BOOKINGS_CURRENT_FRAGMENT;
				break;
			default:
				pos = position;
				break;
		}
		selectFragment(pos, null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			getMenuInflater().inflate(R.menu.menu_main, menu);
			restoreActionBar();
			return true;
		}
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case R.id.menu_main_logout:
				handleMenuLogout();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void handleMenuLogout() {
		LocationService.endLocationMonitoring(this);
		ApplicationClass.setCurrentShift(null);
		ApplicationClass.setCurrentDriver(null, false);
		ApplicationClass.setCurrentUser(null, false);
		Credentials.setCurrent(null);
		Credentials.putStoredCredentials(null);
		System.exit(0);
	}

	public void restoreActionBar() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	public void manageSpinner(Boolean show, String msg) {
		if (show) {
			View focus = getCurrentFocus();
			if (focus != null)
				focus.clearFocus();
			spinner_overlay.setVisibility(View.VISIBLE);
			spinner_status.setText(msg);
		} else {
			spinner_overlay.setVisibility(View.INVISIBLE);
			spinner_status.setText("");
		}
	}
}
