package com.rayeldatu.android.draganddraw;

import android.graphics.PointF;

public class Box {
	private PointF mOrigin;
	private PointF mCurrent;

	public Box(PointF origin) {
		mOrigin = mCurrent = origin;
	}

	public PointF getOrigin() {
		return mOrigin;
	}

	public void setCurrent(PointF current) {
		mCurrent = current;
	}

	public PointF getCurrent() {
		return mCurrent;
	}

}
