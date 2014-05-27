package com.e9ine.cab9.driver.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.e9ine.cab9.driver.R;
import com.e9ine.cab9.driver.activities.MainActivity;
import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.services.LocationMonitor;
import com.e9ine.cab9.driver.services.LocationReceiver;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by David on 03/01/14 for com.e9ine.cab9.drivers.fragments.
 */
public class GoogleMapFragment extends Fragment {
	public static final int CURRENT_LOCATION = 100;
	public static final int BASE_LOCATION = 200;
	public static final int NEXT_LOCATION = 300;

	private int mTracking = CURRENT_LOCATION;
	private MapView mMapView;
	private GoogleMap googleMap;
	private Marker mCurrentPosition;
	ArrayList<Booking> mBookings;
	ArrayList<Marker> mMarkers;

	Button mMyLocation;
	Button mNextLocation;
	Button mBaseLocation;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_map, container, false);
		mMapView = (MapView) v.findViewById(R.id.map_map_map);
		mMyLocation = (Button) v.findViewById(R.id.map_button_location);
		mBaseLocation = (Button) v.findViewById(R.id.map_button_base);
		mNextLocation = (Button) v.findViewById(R.id.map_button_next);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mMapView.onCreate(savedInstanceState);
		mMyLocation.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurrentPosition != null) {
					scrollToLocation(mCurrentPosition.getPosition());
				}
			}
		});

		Booking.LocalDB db = new Booking.LocalDB(getActivity());
		mBookings = db.getUpcomingBookings();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void scrollToLocation(LatLng location) {
		if (googleMap != null && location != null) {
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16.0f));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		googleMap = mMapView.getMap();
		LocationMonitor lm = LocationMonitor.getInstance();
		lm.setLocationReceiver(new LocationReceiver() {
			@Override
			public void onLocationChanged(Location location, Date date) {
				handleLocationUpdate(location, date);
			}
		});
		if (LocationMonitor.lastReceivedLocation != null) {
			Location l = LocationMonitor.lastReceivedLocation;
			LatLng ll = new LatLng(l.getLatitude(), l.getLongitude());
			scrollToLocation(ll);
			mCurrentPosition = googleMap.addMarker(new MarkerOptions()
			.position(ll)
			.title("Current Position")
			.snippet("You are here."));
		}

		googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker marker) {
				return handleMarkerClicked(marker);
			}
		});
		mMarkers  = new ArrayList<Marker>();
		for (Booking b : mBookings) {
			if (!TextUtils.isEmpty(b.EncodedJourney)) {
				List<LatLng> points = PolyUtil.decode(b.EncodedJourney);
				mMarkers.add(googleMap.addMarker(new MarkerOptions()
						.position(points.get(0))
						.title(b.ID.toString())
						.snippet(b.From)
						.draggable(false)));
			}
		}
	}

	private boolean handleMarkerClicked(Marker marker) {
		MainActivity act = (MainActivity) getActivity();
		Bundle args = new Bundle();
		int index = mMarkers.indexOf(marker);
		Booking target = mBookings.get(index);
		args.putLong(BookingsViewFragment.ARG_BOOKING_ID, target.ID);
		act.selectFragment(MainActivity.BOOKINGS_VIEW_FRAGMENT, args);
		return true;
	}

	private void handleLocationUpdate(final Location location, Date date) {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
				if (mTracking == CURRENT_LOCATION)
					scrollToLocation(ll);
				if (mCurrentPosition != null)
					mCurrentPosition.setPosition(ll);
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
		googleMap.clear();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
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
}

