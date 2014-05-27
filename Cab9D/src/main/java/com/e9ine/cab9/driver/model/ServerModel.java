package com.e9ine.cab9.driver.model;

import android.content.ContentValues;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by David on 03/01/14 for com.e9ine.cab9.drivers.model.
 */
public abstract class ServerModel {
	public static final String API_PATH = "http://test.cab9ine.com/api";
	public static final SimpleDateFormat ISO_8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public static final SimpleDateFormat PRETTY_DATE = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	public static void TryPut(JSONObject json, String name, Object value) {
		try {
			if (value != null)
				json.put(name, value);
		} catch (JSONException ignored) {
		}
	}

	public static void TryValuesPut(ContentValues values, String name, String value) {
		try {
			if (value != null)
				values.put(name, value);
		} catch (Exception ignored) {
		}
	}

	public static void TryValuesPut(ContentValues values, String name, Integer value) {
		try {
			if (value != null)
				values.put(name, value);
		} catch (Exception ignored) {
		}
	}

	public static void TryValuesPut(ContentValues values, String name, Long value) {
		try {
			if (value != null)
				values.put(name, value);
		} catch (Exception ignored) {
		}
	}

	public static void TryValuesPut(ContentValues values, String name, Boolean value) {
		try {
			if (value != null)
				values.put(name, (value) ? 1 : 0);
		} catch (Exception ignored) {
		}
	}

	public static void TryValuesPut(ContentValues values, String name, Date value) {
		try {
			if (value != null)
				values.put(name, value.getTime());
		} catch (Exception ignored) {
		}
	}

	public static void TryValuesPut(ContentValues values, String name, Double value) {
		try {
			if (value != null)
				values.put(name, value);
		} catch (Exception ignored) {
		}
	}

	public static String GetStringFromCursor(Cursor cursor, String column, String defaultValue) {
		int index = cursor.getColumnIndex(column);
		if (index == -1) return defaultValue;
		return cursor.getString(index);
	}

	public static int GetIntFromCursor(Cursor cursor, String column, int defaultValue) {
		int index = cursor.getColumnIndex(column);
		if (index == -1) return defaultValue;
		return cursor.getInt(index);
	}

	public static long GetLongFromCursor(Cursor cursor, String column, long defaultValue) {
		int index = cursor.getColumnIndex(column);
		if (index == -1) return defaultValue;
		return cursor.getLong(index);
	}

	public static double GetDoubleFromCursor(Cursor cursor, String column, double defaultValue) {
		int index = cursor.getColumnIndex(column);
		if (index == -1) return defaultValue;
		return cursor.getDouble(index);
	}

	public static Date GetDateFromCursor(Cursor cursor, String column, Date defaultValue) {
		int index = cursor.getColumnIndex(column);
		if (index == -1) return defaultValue;
		try {
			return new Date(cursor.getLong(index));
		} catch (Exception ignored) {
			return defaultValue;
		}
	}

	public static Boolean GetBooleanFromCursor(Cursor cursor, String column, Boolean defaultValue) {
		int index = cursor.getColumnIndex(column);
		if (index == -1) return defaultValue;
		return (cursor.getInt(index) == 1);
	}

	public static Date optDate(JSONObject json, String key, Date defaultValue) {
		String dateString = json.optString(key, "");
		if (!dateString.equals("")){
			try {
				return ISO_8601.parse(dateString);
			} catch (ParseException ignored) {
			}
		}
		return defaultValue;
	}

	public String toJSONString() {
		return toJSON().toString();
	}

	public abstract JSONObject toJSON();
}
