package com.rayeldatu.android.runtracker;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.rayeldatu.android.runtracker.RunDatabaseHelper.RunCursor;

public class RunListFragment extends ListFragment {
	private RunCursor mCursor;
	private static final int REQUEST_NEW_RUN = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		mCursor = RunManager.get(getActivity()).queryRuns();
		RunCursorAdapter adapter = new RunCursorAdapter(getActivity(), mCursor);
		setListAdapter(adapter);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.run_list_options, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_new_run:
			Intent i = new Intent(getActivity(), RunActivity.class);
			startActivityForResult(i, REQUEST_NEW_RUN);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (REQUEST_NEW_RUN == requestCode) {
			mCursor.requery();
			((RunCursorAdapter) getListAdapter()).notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		mCursor.close();
		super.onDestroy();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent i = new Intent(getActivity(), RunActivity.class);
		i.putExtra(RunActivity.EXTRA_RUN_ID, id);
		startActivity(i);
	}

	private static class RunCursorAdapter extends CursorAdapter {
		private RunCursor mRunCursor;

		public RunCursorAdapter(Context context, RunCursor cursor) {

			super(context, cursor, 0);
			mRunCursor = cursor;

		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// TODO Auto-generated method stub
			Run run = mRunCursor.getRun();

			TextView startDateTextView = (TextView) view;
			startDateTextView.setText("Run at " + run.getStartDate());
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			return inflater.inflate(android.R.layout.simple_list_item_1,
					parent, false);
		}
	}
}
