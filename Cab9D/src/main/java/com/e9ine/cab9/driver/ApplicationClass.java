package com.e9ine.cab9.driver;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.Credentials;
import com.e9ine.cab9.driver.model.Driver;
import com.e9ine.cab9.driver.model.Shift;
import com.e9ine.cab9.driver.model.User;
import com.e9ine.cab9.driver.services.LocationService;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.MapsInitializer;

/**
 * Created by David on 06/01/14 for com.e9ine.cab9.driver.
 */
public class ApplicationClass extends Application {
	private static ApplicationClass instance;
	private static User currentUser;
	private static Driver currentDriver;
	private static Shift currentShift;
	private static Booking currentBooking;

	private static LocationService locationService = null;

	private static Driver.Api.GetDriverTask mGetDriverTask;
	private static Shift.Api.GetShiftByIdTask mGetShiftByIdTask;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		try {
			MapsInitializer.initialize(this);
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		LocationService.endLocationMonitoring(this);
	}

	public static ApplicationClass getInstance () {
		return instance;
	}

	public static void setCurrentUser(final User user, Boolean fetchDriverToo) {
		currentUser = user;
		if (fetchDriverToo) {
			if (mGetDriverTask != null) {
				mGetDriverTask.cancel(true);
				mGetDriverTask = null;
			}
			if (user.UserType.equalsIgnoreCase("Driver") && user.UserID != -1) {
				mGetDriverTask = new Driver.Api.GetDriverTask() {
					@Override
					protected void onPreExecute() {
						mQueryValues.put("driverid", user.UserID.toString());
					}

					@Override
					protected void onResultReturned(Driver result) {
						setCurrentDriver(result, true);
					}

					@Override
					protected void onErrorResponse(int failureCode) {
						Log.d("ApplicationClass", String.format("Error fetching driver object, errorCode: %d", failureCode));
					}
				};
				mGetDriverTask.execute();
			}
		}
		Credentials.registerForCloudMessages();
	}

	public static User getCurrentUser() {
		return currentUser;
	}

	public static Booking getCurrentBooking(){
		return currentBooking;
	}

	public static void setCurrentBooking(Booking booking){
		currentBooking = booking;
	}

	public static void setCurrentDriver(Driver driver, Boolean fetchCurrentShiftToo) {
		currentDriver = driver;
		if (fetchCurrentShiftToo) {
			if (mGetShiftByIdTask != null) {
				mGetShiftByIdTask.cancel(true);
				mGetShiftByIdTask = null;
			}
			if (driver.CurrentShiftID != -1) {
				mGetShiftByIdTask = new Shift.Api.GetShiftByIdTask() {
					@Override
					protected void onResultReturned(Shift result) {
						setCurrentShift(result);
					}

					@Override
					protected void onErrorResponse(int failureCode) {
						Log.d("ApplicationClass", String.format("Error fetching shift object, errorCode: %d", failureCode));
					}
				};
				mGetShiftByIdTask.execute(driver.CurrentShiftID);
			}
		}
		LocationService.startLocationMonitoring(ApplicationClass.getInstance());
	}

	public static Driver getCurrentDriver() {
		return currentDriver;
	}

	public static void setCurrentShift(Shift shift) {
		currentShift = shift;
	}

	public static Shift getCurrentShift() {
		return currentShift;
	}

	public static void newLocation(Location location) {
		if (currentShift != null && location != null) {
			Shift.Api.UpdatePositionTask positionTask = new Shift.Api.UpdatePositionTask() {
				@Override
				protected void onResultReturned(Shift.PositionUpdate result) {
				}

				@Override
				protected void onErrorResponse(int failureCode) {
				}
			};
			positionTask.execute(new Shift.PositionUpdate(currentShift.ID, location.getLatitude(), location.getLongitude()));
		}
	}
}
