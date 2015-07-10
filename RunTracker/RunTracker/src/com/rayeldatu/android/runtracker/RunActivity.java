package com.rayeldatu.android.runtracker;

import android.support.v4.app.Fragment;

public class RunActivity extends SingleFragmentActivity {
	public static final String EXTRA_RUN_ID = "com.rayeldatu.android.runtracker.run_id";

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		long runId = getIntent().getLongExtra(EXTRA_RUN_ID, -1);
		if (runId != -1) {
			return RunFragment.newInstance(runId);
		} else {
			return new RunFragment();
		}

	}
}
