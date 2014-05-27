package com.e9ine.cab9.driver.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.e9ine.cab9.driver.model.Booking;
import com.e9ine.cab9.driver.model.Shift;

/**
 * Created by David on 05/02/14 for com.e9ine.cab9.driver.database.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "cab9Database";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(Booking.LocalDB.CREATE_TABLE_SCRIPT);
		db.execSQL(Shift.LocalDB.CREATE_TABLE_SCRIPT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + Booking.LocalDB.TABLE_NAME);
		db.execSQL(Booking.LocalDB.CREATE_TABLE_SCRIPT);

		db.execSQL("DROP TABLE IF EXISTS " + Shift.LocalDB.TABLE_NAME);
		db.execSQL(Shift.LocalDB.CREATE_TABLE_SCRIPT);
	}
}
