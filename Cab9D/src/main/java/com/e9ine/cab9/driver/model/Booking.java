package com.e9ine.cab9.driver.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.e9ine.cab9.driver.api.AsyncGet;
import com.e9ine.cab9.driver.api.AsyncGetById;
import com.e9ine.cab9.driver.api.AsyncGetSingle;
import com.e9ine.cab9.driver.api.AsyncPut;
import com.e9ine.cab9.driver.database.DatabaseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by David on 03/01/14 for com.e9ine.cab9.drivers.model.
 */
public class Booking extends ServerModel {
	public Long ID;
	public Integer CompanyID;
	public Long LocalID;
	public int Priority;
	public int Status;
	public Long ShiftID;
	public Date BookedDateTime;
	public Integer ClientID;
	public String PassengerName;
	public String ContactNumber;
	public String From;
	public String To;
	public String Via;
	public String EncodedJourney;
	public Integer PAX;
	public Integer BAX;
	public Integer CarType;
	public Boolean WheelChair;
	public Boolean InfantSeat;
	public Boolean ChildSeat;
	public Boolean Pets;
	public String Notes;
	public String DriverDispatchNotes;
	public Integer DriverID;
	public Integer VehicleID;
	public Integer PaymentMethod;
	public String PaymentRef;
	public Double ActualFare;
	public Date Editstamp;
	public Date LeadTime;
	public Boolean AutoDispatch;
	public Date POBTime;
	public Date ClearTime;
	public String EstimatedTime;
	public String Originator;
	public Double Distance;

	public Booking(JSONObject json) {
		ID = json.optLong(Fields.ID, -1);
		CompanyID = json.optInt(Fields.COMPANY_ID, -1);
		LocalID= json.optLong(Fields.LOCAL_ID, -1);
		Priority = json.optInt(Fields.PRIORITY, -1);
		Status = json.optInt(Fields.STATUS, -1);
		ShiftID = json.optLong(Fields.SHIFT_ID, -1);
		BookedDateTime = optDate(json, Fields.BOOKED_DATE_TIME, null);
		ClientID = json.optInt(Fields.CLIENT_ID, -1);
		PassengerName = json.optString(Fields.PASSENGER_NAME, "");
		ContactNumber = json.optString(Fields.CONTACT_NUMBER, "");
		From = json.optString(Fields.FROM, "");
		To = json.optString(Fields.TO, "");
		Via = json.optString(Fields.VIA, "");
		EncodedJourney = json.optString(Fields.ENCODED_JOURNEY, "");
		PAX = json.optInt(Fields.PAX, -1);
		BAX = json.optInt(Fields.BAX, -1);
		CarType = json.optInt(Fields.CAR_TYPE, -1);
		WheelChair = json.optBoolean(Fields.WHEELCHAIR, false);
		InfantSeat = json.optBoolean(Fields.INFANT_SEAT, false);
		ChildSeat = json.optBoolean(Fields.CHILD_SEAT, false);
		Pets = json.optBoolean(Fields.PETS, false);
		Notes = json.optString(Fields.NOTES, "");
		DriverDispatchNotes = json.optString(Fields.DRIVER_DISPATCH_NOTES, "");
		DriverID = json.optInt(Fields.DRIVER_ID, -1);
		VehicleID = json.optInt(Fields.VEHICLE_ID, -1);
		PaymentMethod = json.optInt(Fields.PAYMENT_METHOD, -1);
		PaymentRef = json.optString(Fields.PAYMENT_REF, "");
		ActualFare = json.optDouble(Fields.ACTUAL_FARE, 0.00);
		Editstamp = optDate(json, Fields.EDIT_STAMP, null);
		LeadTime = optDate(json, Fields.LEAD_TIME, null);
		AutoDispatch = json.optBoolean(Fields.AUTO_DISPATCH, false);
		POBTime = optDate(json, Fields.POB_TIME, null);
		ClearTime = optDate(json, Fields.CLEAR_TIME, null);
		EstimatedTime = json.optString(Fields.ESTIMATED_TIME, "00:00:00");
		Originator = json.optString(Fields.ORIGINATOR, "");
		Distance = 0D;
	}

	public Booking(Cursor cursor) {
		ID = GetLongFromCursor(cursor, Fields.ID, -1);
		CompanyID = GetIntFromCursor(cursor, Fields.COMPANY_ID, -1);
		LocalID= GetLongFromCursor(cursor, Fields.LOCAL_ID, -1);
		Priority = GetIntFromCursor(cursor, Fields.PRIORITY, -1);
		Status = GetIntFromCursor(cursor, Fields.STATUS, -1);
		ShiftID = GetLongFromCursor(cursor, Fields.SHIFT_ID, -1);
		BookedDateTime = GetDateFromCursor(cursor, Fields.BOOKED_DATE_TIME, null);
		ClientID = GetIntFromCursor(cursor, Fields.CLIENT_ID, -1);
		PassengerName = GetStringFromCursor(cursor, Fields.PASSENGER_NAME, "");
		ContactNumber = GetStringFromCursor(cursor, Fields.CONTACT_NUMBER, "");
		From = GetStringFromCursor(cursor, Fields.FROM + "Field", "");
		To = GetStringFromCursor(cursor, Fields.TO + "Field", "");
		Via = GetStringFromCursor(cursor, Fields.VIA, "");
		EncodedJourney = GetStringFromCursor(cursor, Fields.ENCODED_JOURNEY, "");
		PAX = GetIntFromCursor(cursor, Fields.PAX, -1);
		BAX = GetIntFromCursor(cursor, Fields.BAX, -1);
		CarType = GetIntFromCursor(cursor, Fields.CAR_TYPE, -1);
		WheelChair = GetBooleanFromCursor(cursor, Fields.WHEELCHAIR, false);
		InfantSeat = GetBooleanFromCursor(cursor, Fields.INFANT_SEAT, false);
		ChildSeat = GetBooleanFromCursor(cursor, Fields.CHILD_SEAT, false);
		Pets = GetBooleanFromCursor(cursor, Fields.PETS, false);
		Notes = GetStringFromCursor(cursor, Fields.NOTES, "");
		DriverDispatchNotes = GetStringFromCursor(cursor, Fields.DRIVER_DISPATCH_NOTES, "");
		DriverID = GetIntFromCursor(cursor, Fields.DRIVER_ID, -1);
		VehicleID = GetIntFromCursor(cursor, Fields.VEHICLE_ID, -1);
		PaymentMethod = GetIntFromCursor(cursor, Fields.PAYMENT_METHOD, -1);
		PaymentRef = GetStringFromCursor(cursor, Fields.PAYMENT_REF, "");
		ActualFare = GetDoubleFromCursor(cursor, Fields.ACTUAL_FARE, 0.00);
		Editstamp = GetDateFromCursor(cursor, Fields.EDIT_STAMP, null);
		LeadTime = GetDateFromCursor(cursor, Fields.LEAD_TIME, null);
		AutoDispatch = GetBooleanFromCursor(cursor, Fields.AUTO_DISPATCH, false);
		POBTime = GetDateFromCursor(cursor, Fields.POB_TIME, null);
		ClearTime = GetDateFromCursor(cursor, Fields.CLEAR_TIME, null);
		EstimatedTime = GetStringFromCursor(cursor, Fields.ESTIMATED_TIME, "00:00:00");
		Originator = GetStringFromCursor(cursor, Fields.ORIGINATOR, "");
		Distance = GetDoubleFromCursor(cursor, Fields.DISTANCE, 0.00);
	}

	@Override
	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		TryPut(result, Fields.ID, ID);
		TryPut(result, Fields.COMPANY_ID, CompanyID);
		TryPut(result, Fields.LOCAL_ID, LocalID);
		TryPut(result, Fields.PRIORITY, Priority);
		TryPut(result, Fields.STATUS, Status);
		TryPut(result, Fields.SHIFT_ID, ShiftID);
		TryPut(result, Fields.BOOKED_DATE_TIME, BookedDateTime);
		TryPut(result, Fields.CLIENT_ID, ClientID);
		TryPut(result, Fields.PASSENGER_NAME, PassengerName);
		TryPut(result, Fields.CONTACT_NUMBER, ContactNumber);
		TryPut(result, Fields.FROM, From);
		TryPut(result, Fields.TO, To);
		TryPut(result, Fields.VIA, Via);
		TryPut(result, Fields.ENCODED_JOURNEY, EncodedJourney);
		TryPut(result, Fields.PAX, PAX);
		TryPut(result, Fields.BAX, BAX);
		TryPut(result, Fields.CAR_TYPE, CarType);
		TryPut(result, Fields.WHEELCHAIR, WheelChair);
		TryPut(result, Fields.INFANT_SEAT, InfantSeat);
		TryPut(result, Fields.CHILD_SEAT, ChildSeat);
		TryPut(result, Fields.PETS, Pets);
		TryPut(result, Fields.NOTES, Notes);
		TryPut(result, Fields.DRIVER_DISPATCH_NOTES, DriverDispatchNotes);
		TryPut(result, Fields.DRIVER_ID, DriverID);
		TryPut(result, Fields.VEHICLE_ID, VehicleID);
		TryPut(result, Fields.PAYMENT_METHOD, PaymentMethod);
		TryPut(result, Fields.PAYMENT_REF, PaymentRef);
		TryPut(result, Fields.ACTUAL_FARE, ActualFare);
		TryPut(result, Fields.EDIT_STAMP, Editstamp);
		TryPut(result, Fields.LEAD_TIME, LeadTime);
		TryPut(result, Fields.AUTO_DISPATCH, AutoDispatch);
		TryPut(result, Fields.POB_TIME, POBTime);
		TryPut(result, Fields.CLEAR_TIME, ClearTime);
		TryPut(result, Fields.ESTIMATED_TIME, EstimatedTime);
		TryPut(result, Fields.ORIGINATOR, Originator);
		return result;
	}

	public static class Fields {
		public final static String ID = "ID";
		public final static String COMPANY_ID = "CompanyID";
		public final static String LOCAL_ID = "LocalID";
		public final static String PRIORITY = "Priority";
		public final static String STATUS = "Status";
		public final static String SHIFT_ID = "ShiftID";
		public final static String BOOKED_DATE_TIME = "BookedDateTime";
		public final static String CLIENT_ID = "ClientID";
		public final static String PASSENGER_NAME = "PassengerName";
		public final static String CONTACT_NUMBER = "ContactNumber";
		public final static String FROM = "From";
		public final static String TO = "To";
		public final static String VIA = "Via";
		public final static String ENCODED_JOURNEY = "EncodedJourney";
		public final static String PAX = "PAX";
		public final static String BAX = "BAX";
		public final static String CAR_TYPE = "CarType";
		public final static String WHEELCHAIR = "Wheelchair";
		public final static String INFANT_SEAT = "InfantSeat";
		public final static String CHILD_SEAT = "ChildSeat";
		public final static String PETS = "Pets";
		public final static String NOTES = "Notes";
		public final static String DRIVER_DISPATCH_NOTES = "DriverDispatchNotes";
		public final static String DRIVER_ID = "DriverID";
		public final static String VEHICLE_ID = "VehicleID";
		public final static String PAYMENT_METHOD = "PaymentMethod";
		public final static String PAYMENT_REF = "PaymentRef";
		public final static String ACTUAL_FARE = "ActualFare";
		public final static String EDIT_STAMP = "Editstamp";
		public final static String LEAD_TIME = "LeadTime";
		public final static String AUTO_DISPATCH = "AutoDispatch";
		public final static String POB_TIME = "POBTime";
		public final static String CLEAR_TIME = "ClearTime";
		public final static String ESTIMATED_TIME = "EstimatedTime";
		public final static String ORIGINATOR = "Originator";
		public final static String DISTANCE = "Distance";
	}

	public static class Api {
		public static final String MODEL_PATH = API_PATH + "/Booking";
		public static final String GET_BY_ID_PATH = MODEL_PATH + "/GetByID";
		public static final String UPDATE_PATH = MODEL_PATH + "";
		public static final String UPDATE_STATUS_PATH = MODEL_PATH + "/UpdateStatus";
		public static final String GET_DRIVER_UPCOMING = MODEL_PATH + "/DriversUpcoming";

		public abstract static class GetBookingByIdTask extends AsyncGetSingle<Booking> {
			@Override
			protected String getApiPath() {
				return GET_BY_ID_PATH;
			}

			@Override
			protected Booking fromJSON(JSONObject json) {
				return new Booking(json);
			}
		}

		public abstract static class GetBookingsTask extends AsyncGet<Booking> {
			@Override
			protected String getApiPath() {
				return MODEL_PATH;
			}

			@Override
			protected Booking fromJSON(JSONObject json) {
				return new Booking(json);
			}
		}

		public abstract static class UpcomingBookingsTask extends AsyncGet<Booking> {
			@Override
			protected String getApiPath() {
				return GET_DRIVER_UPCOMING;
			}

			@Override
			protected Booking fromJSON(JSONObject json) {
				return new Booking(json);
			}
		}

		public abstract static class UpdateStatusTask extends  AsyncGetSingle<Booking> {
			@Override
			protected String getApiPath() { return UPDATE_STATUS_PATH; }

			@Override
			protected Booking fromJSON(JSONObject json) { return new Booking(json); }
		}

		public abstract static class UpdateBookingTask extends AsyncPut<Booking> {
			@Override
			protected String getApiPath() {
				return UPDATE_PATH;
			}

			@Override
			protected Booking fromJSON(JSONObject json) {
				return new Booking(json);
			}
		}
	}

	public static class LocalDB extends DatabaseHandler {
		public static final String TABLE_NAME = "BOOKINGS";
		public static final String CREATE_TABLE_SCRIPT =
				"CREATE TABLE " + TABLE_NAME + "("
					+ Fields.ID + " INTEGER PRIMARY KEY,"
					+ Fields.COMPANY_ID + " INTEGER,"
					+ Fields.LOCAL_ID + " INTEGER,"
					+ Fields.PRIORITY + " INTEGER,"
					+ Fields.STATUS + " INTEGER,"
					+ Fields.SHIFT_ID + " INTEGER,"
					+ Fields.BOOKED_DATE_TIME + " INTEGER,"
					+ Fields.CLIENT_ID + " INTEGER,"
					+ Fields.PASSENGER_NAME + " TEXT,"
					+ Fields.CONTACT_NUMBER + " TEXT,"
					+ Fields.FROM + "Field TEXT,"
					+ Fields.TO + "Field TEXT,"
					+ Fields.VIA + " TEXT,"
					+ Fields.ENCODED_JOURNEY + " TEXT,"
					+ Fields.PAX + " INTEGER,"
					+ Fields.BAX + " INTEGER,"
					+ Fields.CAR_TYPE + " INTEGER,"
					+ Fields.WHEELCHAIR + " INTEGER,"
					+ Fields.INFANT_SEAT + " INTEGER,"
					+ Fields.CHILD_SEAT + " INTEGER,"
					+ Fields.PETS + " INTEGER,"
					+ Fields.NOTES + " TEXT,"
					+ Fields.DRIVER_DISPATCH_NOTES + " TEXT,"
					+ Fields.DRIVER_ID + " INTEGER,"
					+ Fields.VEHICLE_ID + " INTEGER,"
					+ Fields.PAYMENT_METHOD + " INTEGER,"
					+ Fields.PAYMENT_REF + " TEXT,"
					+ Fields.ACTUAL_FARE + " REAL,"
					+ Fields.EDIT_STAMP + " INTEGER,"
					+ Fields.LEAD_TIME + " INTEGER,"
					+ Fields.AUTO_DISPATCH + " INTEGER,"
					+ Fields.POB_TIME + " INTEGER,"
					+ Fields.CLEAR_TIME + " INTEGER,"
					+ Fields.ESTIMATED_TIME + " TEXT,"
					+ Fields.ORIGINATOR + " TEXT,"
					+ Fields.DISTANCE + " REAL" + ")";
		public static final String SELECT_BY_SHIFTID_SCRIPT = "SELECT * FROM " + TABLE_NAME + " WHERE " + Fields.SHIFT_ID + " = ?";
		public static final String SELECT_BY_UPCOMING_SCRIPT = "SELECT * FROM " + TABLE_NAME + " WHERE " + Fields.BOOKED_DATE_TIME + " >= ?";
		public static final String SELECT_BY_ID_SCRIPT = "SELECT * FROM " + TABLE_NAME + " WHERE " + Fields.ID + " = ?";

		public LocalDB(Context context) {
			super(context);
		}

		public ContentValues convertToContentValues(Booking booking) {
			ContentValues values = new ContentValues();
				TryValuesPut(values, Fields.ID, booking.ID);
				TryValuesPut(values, Fields.COMPANY_ID, booking.CompanyID);
				TryValuesPut(values, Fields.LOCAL_ID, booking.LocalID);
				TryValuesPut(values, Fields.PRIORITY, booking.Priority);
				TryValuesPut(values, Fields.STATUS, booking.Status);
				TryValuesPut(values, Fields.SHIFT_ID, booking.ShiftID);
				TryValuesPut(values, Fields.BOOKED_DATE_TIME, booking.BookedDateTime);
				TryValuesPut(values, Fields.CLIENT_ID, booking.ClientID);
				TryValuesPut(values, Fields.PASSENGER_NAME, booking.PassengerName);
				TryValuesPut(values, Fields.CONTACT_NUMBER, booking.ContactNumber);
				TryValuesPut(values, Fields.FROM + "Field", booking.From);
				TryValuesPut(values, Fields.TO + "Field", booking.To);
				TryValuesPut(values, Fields.VIA, booking.Via);
				TryValuesPut(values, Fields.ENCODED_JOURNEY, booking.EncodedJourney);
				TryValuesPut(values, Fields.PAX, booking.PAX);
				TryValuesPut(values, Fields.BAX, booking.BAX);
				TryValuesPut(values, Fields.CAR_TYPE, booking.CarType);
				TryValuesPut(values, Fields.WHEELCHAIR, booking.WheelChair);
				TryValuesPut(values, Fields.INFANT_SEAT, booking.InfantSeat);
				TryValuesPut(values, Fields.CHILD_SEAT, booking.ChildSeat);
				TryValuesPut(values, Fields.PETS, booking.Pets);
				TryValuesPut(values, Fields.NOTES, booking.Notes);
				TryValuesPut(values, Fields.DRIVER_DISPATCH_NOTES, booking.DriverDispatchNotes);
				TryValuesPut(values, Fields.DRIVER_ID, booking.DriverID);
				TryValuesPut(values, Fields.VEHICLE_ID, booking.VehicleID);
				TryValuesPut(values, Fields.PAYMENT_METHOD, booking.PaymentMethod);
				TryValuesPut(values, Fields.PAYMENT_REF, booking.PaymentRef);
				TryValuesPut(values, Fields.ACTUAL_FARE, booking.ActualFare);
				TryValuesPut(values, Fields.EDIT_STAMP, booking.Editstamp);
				TryValuesPut(values, Fields.LEAD_TIME, booking.LeadTime);
				TryValuesPut(values, Fields.AUTO_DISPATCH, booking.AutoDispatch);
				TryValuesPut(values, Fields.POB_TIME, booking.POBTime);
				TryValuesPut(values, Fields.CLEAR_TIME, booking.ClearTime);
				TryValuesPut(values, Fields.ESTIMATED_TIME, booking.EstimatedTime);
				TryValuesPut(values, Fields.ORIGINATOR, booking.Originator);
				TryValuesPut(values, Fields.DISTANCE, booking.Distance);
			return values;
		}

		public boolean addBooking(Booking booking) {
			SQLiteDatabase db = this.getWritableDatabase();
			if (db != null && db.isOpen()) {
				try {
					ContentValues values = convertToContentValues(booking);
					db.insert(TABLE_NAME, null, values);
					return true;
				}
				catch (Exception ignored) {
				}
				finally {
					db.close();
				}
			}
			return false;
		}

		public ArrayList<Booking> getBookingsForShift(long shiftId) {
			SQLiteDatabase db = this.getReadableDatabase();
			ArrayList<Booking> result = new ArrayList<Booking>();
			if (db != null && db.isOpen()) {
				try {
					Cursor cursor = db.rawQuery(SELECT_BY_SHIFTID_SCRIPT, new String[]{ String.valueOf(shiftId)});
					if (cursor.moveToFirst()) {
						do {
							Booking booking = new Booking(cursor);
							// Adding contact to list
							result.add(booking);
						} while (cursor.moveToNext());
					}
					cursor.close();
				} catch (Exception ignored) {}
				finally {
					db.close();
				}
			}
			return result;
		}

		public ArrayList<Booking> getUpcomingBookings() {
			SQLiteDatabase db = this.getReadableDatabase();
			ArrayList<Booking> result = new ArrayList<Booking>();
			if (db != null && db.isOpen()) {
				try {
					Cursor cursor = db.rawQuery(SELECT_BY_UPCOMING_SCRIPT, new String[]{ String.valueOf(new Date().getTime())});
					if (cursor.moveToFirst()) {
						do {
							Booking booking = new Booking(cursor);
							// Adding contact to list
							result.add(booking);
						} while (cursor.moveToNext());
					}
					cursor.close();
				} catch (Exception ignored) {}
				finally {
					db.close();
				}
			}
			return result;
		}

		public int updateOrInsertBooking(Booking booking) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = convertToContentValues(booking);
			if (db != null && db.isOpen()) {
				try {
					int rowsAffected = db.update(TABLE_NAME, values, Fields.ID + " = ?", new String[]{String.valueOf(booking.ID)});
					if (rowsAffected == 0) {
						db.insert(TABLE_NAME, null, values);
					}
					return rowsAffected;
				} catch (Exception ignored) {}
				finally {
					db.close();
				}
			}
			return -1;
		}

		public void updateOrInsertManyBookings(ArrayList<Booking> bookings) {
			SQLiteDatabase db = this.getWritableDatabase();
			if (db != null && db.isOpen()) {
				try {
					for(Booking b : bookings){
						ContentValues values = convertToContentValues(b);
						int rowsAffected = db.update(TABLE_NAME, values, Fields.ID + " = ?", new String[]{String.valueOf(b.ID)});
						if (rowsAffected == 0) {
							db.insert(TABLE_NAME, null, values);
						}
					}
				} catch (Exception ignored) {}
				finally {
					db.close();
				}
			}
		}

		public Booking getBookingByID(long id) {
			SQLiteDatabase db = this.getReadableDatabase();
			Booking result = null;
			if (db != null && db.isOpen()) {
				try {
					Cursor cursor = db.rawQuery(SELECT_BY_ID_SCRIPT, new String[] {String.valueOf(id)});
					if (cursor != null && cursor.moveToFirst()) {
						result = new Booking(cursor);
					}
					cursor.close();
				} catch (Exception ignored) {}
				finally {
					db.close();
				}
			}
			return result;
		}
	}
}
