package com.rayeldatu.android.runtracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RunFragment extends Fragment {
	private static final String TAG = "RunFragment";
	private Button mStartButton, mStopButton;
	private TextView mStartedTextView, mLatitudeTextView, mLongitudeTextView,
			mAltitudeTextView, mDurationTextView;
	private RunManager mRunManager;
	private Run mRun;
	private Location mLastLocation;
	private BroadcastReceiver mLocationReceiver = new LocationReceiver() {
		@Override
		protected void onLocationReceived(Context context, Location loc) {
			mLastLocation = loc;
			if (isVisible())
				updateUI();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mRunManager = RunManager.get(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_run, parent, false);
		mStartedTextView = (TextView) view
				.findViewById(R.id.run_startedTextView);
		mLatitudeTextView = (TextView) view
				.findViewById(R.id.run_latitudeTextView);
		mLongitudeTextView = (TextView) view
				.findViewById(R.id.run_longitudeTextView);
		mAltitudeTextView = (TextView) view
				.findViewById(R.id.run_altitudeTextView);
		mDurationTextView = (TextView) view
				.findViewById(R.id.run_durationTextView);

		mStartButton = (Button) view.findViewById(R.id.run_startButton);
		mStartButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRunManager.startLocationUpdates();
				mRun = new Run();
				updateUI();
			}
		});
		mStopButton = (Button) view.findViewById(R.id.run_stopButton);
		mStopButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mRunManager.stopLocationUpdates();
				updateUI();
			}
		});

		updateUI();
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		getActivity().registerReceiver(mLocationReceiver,
				new IntentFilter(RunManager.ACTION_LOCATION));
	}

	@Override
	public void onStop() {
		getActivity().unregisterReceiver(mLocationReceiver);
		super.onStop();
	}

	private void updateUI() {
		boolean started = mRunManager.isTrackingRun();
		Log.d(TAG, "STARTED: " + started);
		
		if (mRun != null)
			mStartedTextView.setText(mRun.getStartDate().toString());

		int durationSeconds = 0;

		if (mRun != null && mLastLocation != null) {
			durationSeconds = mRun.getDurationSeconds(mLastLocation.getTime());
			mLatitudeTextView.setText(Double.toString(mLastLocation
					.getLatitude()));
			mLongitudeTextView.setText(Double.toString(mLastLocation
					.getLongitude()));
			mAltitudeTextView.setText(Double.toString(mLastLocation
					.getAltitude()));
		}
		mDurationTextView.setText(Run.formatDuration(durationSeconds));

		mStartButton.setEnabled(!started);
		mStopButton.setEnabled(started);
	}
}
