package com.rayeldatu.android.runtracker;

import android.support.v4.app.Fragment;

public class RunActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		// TODO Auto-generated method stub
		return new RunFragment();
	}
}
