package com.rayeldatu.android.photogallery;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class PhotoGalleryFragment extends Fragment {
	GridView mGridView;
	ArrayList<GalleryItem> mItems;

	private static final String TAG = "PhotoGalleryFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		new FetchItemsTask().execute();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_photo_gallery, parent,
				false);
		mGridView = (GridView) v.findViewById(R.id.gridView);

		setupAdapter();
		return v;
	}

	void setupAdapter() {
		if (getActivity() == null || mGridView == null)
			return;
		if (mItems != null) {
			mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
					android.R.layout.simple_gallery_item, mItems));

		} else {
			mGridView.setAdapter(null);
		}
	}

	private class FetchItemsTask extends
			AsyncTask<Void, Void, ArrayList<GalleryItem>> {

		@Override
		protected ArrayList<GalleryItem> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return new FlickrFetchr().fetchItems();

		}

		@Override
		protected void onPostExecute(ArrayList<GalleryItem> items) {
			mItems = items;
			setupAdapter();
		}

	}
}
