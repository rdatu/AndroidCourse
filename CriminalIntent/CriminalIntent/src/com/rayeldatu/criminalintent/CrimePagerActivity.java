package com.rayeldatu.criminalintent;

import java.util.ArrayList;
import java.util.UUID;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;

public class CrimePagerActivity extends FragmentActivity {
	private ViewPager mViewPager;
	private ArrayList<Crime> mCrimes;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		mViewPager = new ViewPager(this);
		mViewPager.setId(R.id.ViewPager);
		setContentView(mViewPager);
		
		mCrimes = CrimeLab.get(this).getCrimes();
		
		FragmentManager fm = getSupportFragmentManager();
		mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return mCrimes.size();
			}
			
			@Override
			public Fragment getItem(int pos) {
				// TODO Auto-generated method stub
				Crime crime = mCrimes.get(pos);
				return CrimeFragment.newInstance(crime.getId());
			}
		});
		
		UUID crimeId = (UUID) getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);
		for(int i = 0;i<mCrimes.size();i++){
			if(mCrimes.get(i).getId().equals(crimeId)){
				mViewPager.setCurrentItem(i);
				break;
			}
		}
		
		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()  {
			
			@Override
			public void onPageSelected(int pos) {
				// TODO Auto-generated method stub
				Crime crime = mCrimes.get(pos);
				if(crime.getTitle() != null){
					setTitle(crime.getTitle());
				}
				Log.d("TITLE",crime.getTitle());
			}
			
			@Override
			public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
