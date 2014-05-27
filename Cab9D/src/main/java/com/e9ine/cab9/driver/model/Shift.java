package com.e9ine.cab9.driver.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.e9ine.cab9.driver.api.AsyncGet;
import com.e9ine.cab9.driver.api.AsyncGetById;
import com.e9ine.cab9.driver.api.AsyncPost;
import com.e9ine.cab9.driver.api.AsyncPut;
import com.e9ine.cab9.driver.api.WrappedApiResult;
import com.e9ine.cab9.driver.database.DatabaseHandler;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by David on 03/01/14 for com.e9ine.cab9.drivers.model.
 */
public class Shift extends ServerModel {
	public Long ID;
	public Integer CompanyID;
	public Integer DriverID;
	public Integer VehicleID;
	public Date ShiftStart;
	public Date ShiftEnd;
	public Integer MileageStart;
	public Integer MileageEnd;
	public Boolean WheelChair;
	public Boolean InfantSeat;
	public Boolean ChildSeat;
	public Boolean Pets;
	public Boolean Active;
	public String EncodedRoute;
	public String ShiftNotes;
	public Integer TotalAccepted;
	public Integer TotalRejected;
	public Integer TotalMissed;
	public Integer TotalBookings;

	public Shift(JSONObject json) {
		ID = json.optLong(Fields.ID, -1);
		CompanyID = json.optInt(Fields.COMPANY_ID, -1);
		DriverID = json.optInt(Fields.DRIVER_ID, -1);
		VehicleID = json.optInt(Fields.VEHICLE_ID, -1);
		MileageStart = json.optInt(Fields.MILEAGE_START, -1);
		MileageEnd = json.optInt(Fields.MILEAGE_END, -1);
		WheelChair = json.optBoolean(Fields.WHEELCHAIR, false);
		InfantSeat = json.optBoolean(Fields.INFANT_SEAT, false);
		ChildSeat = json.optBoolean(Fields.CHILD_SEAT, false);
		Pets = json.optBoolean(Fields.PETS, false);
		Active = json.optBoolean(Fields.ACTIVE, false);
		EncodedRoute = json.optString(Fields.ENCODED_ROUTE, "");
		ShiftNotes = json.optString(Fields.SHIFT_NOTES, "");
		ShiftStart = optDate(json, Fields.SHIFT_START, null);
		ShiftEnd = optDate(json, Fields.SHIFT_END, null);
		TotalBookings = json.optInt(Fields.TOTAL_BOOKINGS, 0);
		TotalAccepted = json.optInt(Fields.TOTAL_ACCEPTED, 0);
		TotalRejected = json.optInt(Fields.TOTAL_REJECTED, 0);
		TotalMissed = json.optInt(Fields.TOTAL_MISSED, 0);
	}

	public Shift(Cursor cursor) {
		ID = GetLongFromCursor(cursor, Fields.ID, -1);
		CompanyID = GetIntFromCursor(cursor, Fields.COMPANY_ID, -1);
		DriverID = GetIntFromCursor(cursor, Fields.DRIVER_ID, -1);
		VehicleID = GetIntFromCursor(cursor, Fields.VEHICLE_ID, -1);
		MileageStart = GetIntFromCursor(cursor, Fields.MILEAGE_START, -1);
		MileageEnd = GetIntFromCursor(cursor, Fields.MILEAGE_END, -1);
		WheelChair = GetBooleanFromCursor(cursor, Fields.WHEELCHAIR, false);
		InfantSeat = GetBooleanFromCursor(cursor, Fields.INFANT_SEAT, false);
		ChildSeat = GetBooleanFromCursor(cursor, Fields.CHILD_SEAT, false);
		Pets = GetBooleanFromCursor(cursor, Fields.PETS, false);
		Active = GetBooleanFromCursor(cursor, Fields.ACTIVE, false);
		EncodedRoute = GetStringFromCursor(cursor, Fields.ENCODED_ROUTE, "");
		ShiftNotes = GetStringFromCursor(cursor, Fields.SHIFT_NOTES, "");
		ShiftStart = GetDateFromCursor(cursor, Fields.SHIFT_START, null);
		ShiftEnd = GetDateFromCursor(cursor, Fields.SHIFT_END, null);
		TotalAccepted = GetIntFromCursor(cursor, Fields.TOTAL_ACCEPTED, 0);
		TotalRejected = GetIntFromCursor(cursor, Fields.TOTAL_REJECTED, 0);
		TotalMissed = GetIntFromCursor(cursor, Fields.TOTAL_MISSED, 0);
		TotalBookings = GetIntFromCursor(cursor, Fields.TOTAL_BOOKINGS, 0);
	}

	public JSONObject toJSON() {
		JSONObject result = new JSONObject();
		TryPut(result, Fields.ID, ID);
		TryPut(result, Fields.COMPANY_ID, CompanyID);
		TryPut(result, Fields.DRIVER_ID, DriverID);
		TryPut(result, Fields.VEHICLE_ID, VehicleID);
		TryPut(result, Fields.SHIFT_START, ShiftStart);
		TryPut(result, Fields.SHIFT_END, ShiftEnd);
		TryPut(result, Fields.MILEAGE_START, MileageStart);
		TryPut(result, Fields.MILEAGE_END, MileageEnd);
		TryPut(result, Fields.WHEELCHAIR, WheelChair);
		TryPut(result, Fields.INFANT_SEAT, InfantSeat);
		TryPut(result, Fields.CHILD_SEAT, ChildSeat);
		TryPut(result, Fields.PETS, Pets);
		TryPut(result, Fields.ACTIVE, Active);
		TryPut(result, Fields.ENCODED_ROUTE, EncodedRoute);
		TryPut(result, Fields.SHIFT_NOTES, ShiftNotes);
		return result;
	}

	public static class Fields {
		public final static String ID = "ID";
		public final static String COMPANY_ID = "CompanyID";
		public final static String DRIVER_ID = "DriverID";
		public final static String VEHICLE_ID = "VehicleID";
		public final static String SHIFT_START = "ShiftStart";
		public final static String SHIFT_END = "ShiftEnd";
		public final static String MILEAGE_START = "MileageStart";
		public final static String MILEAGE_END = "MileageEnd";
		public final static String WHEELCHAIR = "WheelChair";
		public final static String INFANT_SEAT = "InfantSeat";
		public final static String CHILD_SEAT = "ChildSeat";
		public final static String PETS = "Pets";
		public final static String ACTIVE = "Active";
		public final static String ENCODED_ROUTE = "EncodedRoute";
		public final static String SHIFT_NOTES = "ShiftNotes";
		public final static String TOTAL_BOOKINGS = "TotalBookings";
		public final static String TOTAL_ACCEPTED = "TotalAccepted";
		public final static String TOTAL_REJECTED = "TotalRejected";
		public final static String TOTAL_MISSED = "TotalMissed";
	}

	public static class Api {
		public static final String MODEL_PATH = API_PATH + "/DriverShift";

		public static final String GET_BY_ID_PATH = MODEL_PATH + "/GetByID";
		public static final String UPDATE_PATH = MODEL_PATH + "";
		public static final String POST_POINT_PATH = MODEL_PATH + "";
		public static final String START_SHIFT_PATH = MODEL_PATH + "/StartShift";
		public static final String END_SHIFT_PATH = MODEL_PATH + "/EndShift";

		public static abstract class GetShiftByIdTask extends AsyncGetById<Shift> {
			@Override
			protected String getApiPath() {
				return GET_BY_ID_PATH;
			}

			@Override
			protected Shift fromJSON(JSONObject json) {
				return new Shift(json);
			}
		}

		public static abstract class GetShiftsTask extends AsyncGet<Shift> {
			@Override
			protected String getApiPath() {
				return MODEL_PATH;
			}

			@Override
			protected Shift fromJSON(JSONObject json) {
				return new Shift(json);
			}
		}

		public static abstract class UpdateShiftTask extends AsyncPut<Shift> {
			@Override
			protected String getApiPath() {
				return UPDATE_PATH;
			}

			@Override
			protected Shift fromJSON(JSONObject json) {
				return new Shift(json);
			}
		}

		public static abstract class UpdatePositionTask extends AsyncTask<PositionUpdate, Void, WrappedApiResult<PositionUpdate>> {
			@SafeVarargs
			@Override
			protected final WrappedApiResult<PositionUpdate> doInBackground(PositionUpdate... params) {
				if (params.length != 1) {
					return new WrappedApiResult<PositionUpdate>(WrappedApiResult.PARAMETER_ERROR_CODE);
				}
				PositionUpdate input = params[0];
				HttpClient client = new DefaultHttpClient();
				String path = String.format("%s?shiftId=%d&latitude=%s&longitude=%s", POST_POINT_PATH, input.shiftId, input.latitude, input.longitude);
				HttpPost request = new HttpPost(path);
				request.setHeader("content-type", "application/json");
				request.setHeader("authorization", Credentials.getAuthenticatedHeader("POST", path));
				HttpResponse response;
				int HttpStatusCode;

				try {
					StringEntity entity = new StringEntity("");
					entity.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
					request.setEntity(entity);
					response = client.execute(request);
					HttpStatusCode = response.getStatusLine().getStatusCode();
					switch (HttpStatusCode) {
						case HttpStatus.SC_UNAUTHORIZED:
							return new WrappedApiResult<PositionUpdate>(WrappedApiResult.UNAUTHORIZED_CODE);
						case HttpStatus.SC_INTERNAL_SERVER_ERROR:
							return new WrappedApiResult<PositionUpdate>(WrappedApiResult.SERVER_ERROR_CODE);
						case HttpStatus.SC_NOT_FOUND:
							return new WrappedApiResult<PositionUpdate>(WrappedApiResult.NOT_FOUND_CODE);
						case HttpStatus.SC_BAD_REQUEST:
							return new WrappedApiResult<PositionUpdate>(WrappedApiResult.BAD_REQUEST_CODE);
						case HttpStatus.SC_OK:
							return new WrappedApiResult<PositionUpdate>(input);
						case HttpStatus.SC_CREATED:
							return new WrappedApiResult<PositionUpdate>(input);
						default:
							return new WrappedApiResult<PositionUpdate>(WrappedApiResult.OTHER_ERROR_CODE);
					}
				} catch (Exception ignored) {
					return new WrappedApiResult<PositionUpdate>(WrappedApiResult.OTHER_ERROR_CODE);
				}
			}

			@Override
			protected void onPostExecute(WrappedApiResult<PositionUpdate> response) {
				if (response.Success) {
					onResultReturned(response.Result);
				} else {
					onErrorResponse(response.FailureCode);
				}
			}

			protected abstract void onResultReturned(PositionUpdate result);

			protected abstract void onErrorResponse(int failureCode);
		}
	}

	public static class PositionUpdate {
		public long shiftId;
		public Double latitude;
		public Double longitude;

		public PositionUpdate(long s, Double lat, Double lon){
			shiftId = s;
			latitude = lat;
			longitude = lon;
		}
	}

	public static class LocalDB extends DatabaseHandler {
		public static final String TABLE_NAME = "SHIFTS";
		public static final String CREATE_TABLE_SCRIPT =
				"CREATE TABLE " + TABLE_NAME + "("
						+ Fields.ID + " INTEGER PRIMARY KEY,"
						+ Fields.COMPANY_ID + " INTEGER,"
						+ Fields.DRIVER_ID + " INTEGER,"
						+ Fields.VEHICLE_ID + " INTEGER,"
						+ Fields.SHIFT_START + " INTEGER,"
						+ Fields.SHIFT_END + " INTEGER,"
						+ Fields.MILEAGE_START + " INTEGER,"
						+ Fields.MILEAGE_END + " INTEGER,"
						+ Fields.WHEELCHAIR + " INTEGER,"
						+ Fields.INFANT_SEAT + " INTEGER,"
						+ Fields.CHILD_SEAT + " INTEGER,"
						+ Fields.PETS + " INTEGER,"
						+ Fields.ACTIVE + " INTEGER,"
						+ Fields.ENCODED_ROUTE + " TEXT,"
						+ Fields.SHIFT_NOTES + " TEXT,"
						+ Fields.TOTAL_BOOKINGS + " INTEGER,"
						+ Fields.TOTAL_ACCEPTED + " INTEGER,"
						+ Fields.TOTAL_REJECTED + " INTEGER,"
						+ Fields.TOTAL_MISSED + " INTEGER" + ")";
		public static final String SELECT_ALL_SHIFTS = "SELECT * FROM " + TABLE_NAME;
		public static final String SELECT_BY_ID_SCRIPT = "SELECT * FROM " + TABLE_NAME + " WHERE " + Fields.ID + " = ?";

		public LocalDB(Context context) {
			super(context);
		}

		public ContentValues convertToContentValues(Shift shift) {
			ContentValues values = new ContentValues();
			TryValuesPut(values, Fields.ID, shift.ID);
			TryValuesPut(values, Fields.COMPANY_ID, shift.CompanyID);
			TryValuesPut(values, Fields.DRIVER_ID, shift.DriverID);
			TryValuesPut(values, Fields.VEHICLE_ID, shift.VehicleID);
			TryValuesPut(values, Fields.SHIFT_START, shift.ShiftStart);
			TryValuesPut(values, Fields.SHIFT_END, shift.ShiftEnd);
			TryValuesPut(values, Fields.MILEAGE_START, shift.MileageStart);
			TryValuesPut(values, Fields.MILEAGE_END, shift.MileageEnd);
			TryValuesPut(values, Fields.WHEELCHAIR, shift.WheelChair);
			TryValuesPut(values, Fields.INFANT_SEAT, shift.InfantSeat);
			TryValuesPut(values, Fields.CHILD_SEAT, shift.ChildSeat);
			TryValuesPut(values, Fields.PETS, shift.Pets);
			TryValuesPut(values, Fields.ACTIVE, shift.Active);
			TryValuesPut(values, Fields.ENCODED_ROUTE, shift.EncodedRoute);
			TryValuesPut(values, Fields.SHIFT_NOTES, shift.DriverID);
			TryValuesPut(values, Fields.TOTAL_BOOKINGS, shift.TotalBookings);
			TryValuesPut(values, Fields.TOTAL_ACCEPTED, shift.TotalAccepted);
			TryValuesPut(values, Fields.TOTAL_REJECTED, shift.TotalRejected);
			TryValuesPut(values, Fields.TOTAL_MISSED, shift.TotalMissed);
			return values;
		}

		public boolean addShift(Shift shift) {
			SQLiteDatabase db = this.getWritableDatabase();
			if (db != null && db.isOpen()) {
				try {
					ContentValues values = convertToContentValues(shift);
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

		public ArrayList<Shift> getAllShifts() {
			SQLiteDatabase db = this.getReadableDatabase();
			ArrayList<Shift> result = new ArrayList<Shift>();
			if (db != null && db.isOpen()) {
				try {
					Cursor cursor = db.rawQuery(SELECT_ALL_SHIFTS, new String[]{ });
					if (cursor.moveToFirst()) {
						do {
							Shift shift = new Shift(cursor);
							// Adding contact to list
							result.add(shift);
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

		public int updateOrInsertShift(Shift shift) {
			SQLiteDatabase db = this.getWritableDatabase();
			ContentValues values = convertToContentValues(shift);
			if (db != null && db.isOpen()) {
				try {
					int rowsAffected = db.update(TABLE_NAME, values, Fields.ID + " = ?", new String[]{String.valueOf(shift.ID)});
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

		public void updateOrInsertManyShifts(ArrayList<Shift> shifts) {
			SQLiteDatabase db = this.getWritableDatabase();
			if (db != null && db.isOpen()) {
				try {
					for (Shift s : shifts){
						ContentValues values = convertToContentValues(s);
						int rowsAffected = db.update(TABLE_NAME, values, Fields.ID + " = ?", new String[]{String.valueOf(s.ID)});
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

		public Shift getShiftByID(long id) {
			SQLiteDatabase db = this.getReadableDatabase();
			Shift result = null;
			if (db != null && db.isOpen()) {
				try {
					Cursor cursor = db.rawQuery(SELECT_BY_ID_SCRIPT, new String[] {String.valueOf(id)});
					if (cursor != null && cursor.moveToFirst()) {
						result = new Shift(cursor);
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
