package com.rayeldatu.android.runtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RunDatabaseHelper extends SQLiteOpenHelper {
	private static final String DB_Name = "runs.sqlite";
	private static final int VERSION = 1;

	private static final String TABLE_RUN = "run";
	private static final String COLUMN_RUN_START_DATE = "start_date";

	public RunDatabaseHelper(Context context) {
		super(context, DB_Name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table run ("
				+ "_id integer primary key autoincrement, start_date integer)");
		db.execSQL("create table location("
				+ "timestamp integer, latitude real, longitude real, altitude real,"
				+ " provider varchar(100), run_id integer references run(_id)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public long insertRun(Run run) {
		ContentValues cv = new ContentValues();
		cv.put(COLUMN_RUN_START_DATE, run.getStartDate().getTime());
		return getWritableDatabase().insert(TABLE_RUN, null, cv);
	}
}
