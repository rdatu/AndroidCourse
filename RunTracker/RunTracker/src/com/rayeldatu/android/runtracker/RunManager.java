package com.rayeldatu.android.runtracker;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

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
	private long mCurrentId;

	private RunManager(Context appContext) {
		mAppContext = appContext;
		mLocationManager = (LocationManager) mAppContext
				.getSystemService(Context.LOCATION_SERVICE);
		mHelper = new RunDatabaseHelper(mAppContext);
		mPrefs = mAppContext.getSharedPreferences(PREFS_FILE,
				Context.MODE_PRIVATE);
		mCurrentId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
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
		return PendingIntent.getBroadcast(mAppContext, 0, broadcast, 0);
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
		mCurrentId = run.getId();
		mPrefs.edit().putLong(PREF_CURRENT_RUN_ID, mCurrentId).commit();
		startLocationUpdates();
	}

	public void stopRun() {
		stopLocationUpdates();
		mCurrentId = -1;
		mPrefs.edit().remove(PREF_CURRENT_RUN_ID).commit();
	}

	private Run insertRun() {
		Run run = new Run();
		run.setId(mHelper.insertRun(run));
		return run;
	}

	public boolean isTrackingRun() {
		return getLocationPendingIntent(false) != null;
	}

}
