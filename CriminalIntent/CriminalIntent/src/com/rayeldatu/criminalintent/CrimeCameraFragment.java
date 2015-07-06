package com.rayeldatu.criminalintent;

import java.io.IOException;
import java.util.List;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

@SuppressWarnings("deprecation")
public class CrimeCameraFragment extends Fragment {
	private static final String TAG = "CrimeCameraFragment";
	private Camera mCamera;
	private SurfaceView mSurfaceView;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_crime_camera, parent, false);

		Button takePictureButton = (Button) v
				.findViewById(R.id.crime_camera_takePictureButton);
		takePictureButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getActivity().finish();
			}
		});

		mSurfaceView = (SurfaceView) v
				.findViewById(R.id.crime_camera_surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		holder.addCallback(new SurfaceHolder.Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				if (mCamera != null)
					mCamera.stopPreview();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				// TODO Auto-generated method stub
				try {
					if (mCamera != null)
						mCamera.setPreviewDisplay(holder);
				} catch (IOException exception) {
					Log.e(TAG, "Error Setting up preview display", exception);
				}
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int w,
					int h) {
				// TODO Auto-generated method stub
				Camera.Parameters parameters = mCamera.getParameters();
				Size s = getBestSupportedSize(
						parameters.getSupportedPreviewSizes(), w, h);
				parameters.setPreviewSize(s.width, s.height);
				mCamera.setParameters(parameters);
				try {
					mCamera.startPreview();
				} catch (Exception e) {
					Log.e(TAG, "Could not start Preview", e);
					mCamera.release();
					mCamera = null;
				}

			}
		});

		return v;
	}

	@Override
	@TargetApi(9)
	public void onResume() {
		super.onResume();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
			mCamera = Camera.open(0);
		else
			mCamera = Camera.open();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	private Size getBestSupportedSize(List<Size> sizes, int width, int height) {
		Size bestSize = sizes.get(0);
		int largestArea = bestSize.width * bestSize.height;
		for (Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				bestSize = s;
				largestArea = area;
			}
		}
		return bestSize;
	}
}
