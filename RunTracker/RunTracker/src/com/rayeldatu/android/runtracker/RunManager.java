package com.rayeldatu.android.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.rayeldatu.android.runtracker.RunDatabaseHelper.LocationCursor;
import com.rayeldatu.android.runtracker.RunDatabaseHelper.RunCursor;

public class RunManager {
	private static final String TAG = "RunManager";
	public static final String ACTION_LOCATION = "com.rayeldatu.android.runtracker.ACTION_LOCATION";
	private static RunManager sRunManager;
	private Context mAppContext;
	private LocationManager mLocationManager;
	private static final String TEST_PROVIDER = "TEST_PROVIDER";
	private static final String PREFS_FILE = "runs";
	private static final String PREF_CURRENT_RUN_ID = "RunManager.currentRunId";

	private RunDatabaseHelper mHelper;
	private SharedPreferences mPrefs;
	private long mCurrentRunId;

	private RunManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager) mAppContext
				.getSystemService(Context.LOCATION_SERVICE);
		mHelper = new RunDatabaseHelper(mAppContext);
		mPrefs = mAppContext.getSharedPreferences(PREFS_FILE,
				Context.MODE_PRIVATE);
		mCurrentRunId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
	}

	public static RunManager get(Context c) {
		if (sRunManager == null) {
			sRunManager = new RunManager(c.getApplicationContext());
		}
		return sRunManager;
	}

	private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
	}

	public void startLocationUpdates() {
		String provider = LocationManager.GPS_PROVIDER;

		if (mLocationManager.getProvider(TEST_PROVIDER) != null
				&& mLocationManager.isProviderEnabled(TEST_PROVIDER)) {
			provider = TEST_PROVIDER;
		}
		Log.d(TAG, "Using provider " + provider);
		Location lastKnown = mLocationManager.getLastKnownLocation(provider);
		if (lastKnown != null) {
			lastKnown.setTime(System.currentTimeMillis());
			broadcastLocation(lastKnown);
		}
		PendingIntent pi = getLocationPendingIntent(true);
		mLocationManager.requestLocationUpdates(provider, 0, 0, pi);
	}

	public void stopLocationUpdates() {
		PendingIntent pi = getLocationPendingIntent(false);
		if (pi != null) {
			mLocationManager.removeUpdates(pi);
			pi.cancel();
		}
	}

	private void broadcastLocation(Location location) {
		Intent broadcast = new Intent(ACTION_LOCATION);
		broadcast.putExtra(LocationManager.KEY_LOCATION_CHANGED, location);
		mAppContext.sendBroadcast(broadcast);
	}

	public Run startNewRun() {
		Run run = insertRun();
		startTrackingRun(run);
		return run;
	}

	public void startTrackingRun(Run run) {
		mCurrentRunId = run.getId();
		mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentRunId).commit();
		startLocationUpdates();
	}

	public void stopRun() {
		stopLocationUpdates();
		mCurrentRunId = -1;
		mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
	}

	private Run insertRun() {
		Run run = new Run();
		run.setId(mHelper.insertRun(run));
		return run;
	}

	public Run getRun(long id) {

		Run run = null;
		RunCursor cursor = mHelper.queryRun(id);
		cursor.moveToFirst();
		if (!cursor.isAfterLast())
			run = cursor.getRun();
		cursor.close();
		return run;
	}

	public RunCursor queryRuns() {
		return mHelper.queryRuns();
	}

	public void insertLocation(Location loc) {
		if (mCurrentRunId != -1) {
			mHelper.insertLocation(mCurrentRunId, loc);
		} else
			Log.e(TAG, "Location Received with no tracking run; ignoring...");
	}

	public Location getLastLocationForRun(long runId) {
		Location location = null;
		Log.d(TAG, "runId is " + runId);
		LocationCursor cursor = mHelper.queryLastLocationForRun(runId);
		cursor.moveToFirst();
		Log.d(TAG, "query has no contents " + (cursor.isAfterLast()));
		if (!cursor.isAfterLast())
			location = cursor.getLocation();
		cursor.close();
		Log.d(TAG, "location is null : " + (location == null));
		return location;
	}

	public boolean isTrackingRun() {
		return getLocationPendingIntent(false) != null;
	}

	public boolean isTrackingRun(Run run) {
		return run != null && run.getId() == mCurrentRunId;
	}

}
