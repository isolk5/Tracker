package com.isol.app.tracker;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.isol.app.tracker.PortaliFragment.InventarioItemModel;
import com.isol.app.tracker.PortaliFragment.MySimpleArrayAdapter;
import com.isol.app.tracker.ZoneFragment.ZoneListener;

public class Principale extends FragmentActivity implements ZoneListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} tht will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	boolean flagUpdatePortalData = false;
	
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_principale);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		// Recupero i dati dell'utente

		MyApplication myapp = MyApplication.getInstance();

		JSONObject obj = Utilita
				.getJSONObjectFromArray("JSONInterface.aspx?function=GetPersonByAccount&account="
						+ myapp.getAccountName());

		String nickname;
		try {
			nickname = obj.getString("Person_Nickname");
			int GroupID = obj.getInt("Group_ID");
			int PersonID = obj.getInt("Person_ID");
//			int PersonZone = obj.getInt("Person_Zone");
			
			myapp.setGroupID(GroupID);
			myapp.setPersonID(PersonID);
			myapp.setNickName(nickname);
//			myapp.setPersonZone(PersonZone);
			
			getActionBar().setTitle("Tracker di " + myapp.getNickName());

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.principale, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	        	
	    		//chiamo l'activity user_item
	    		Intent intentSettings = new Intent(this, AppSettingsActivity.class);
	    		startActivity(intentSettings);
	    		return false;
	    		
	        case R.id.action_logout:
	            
	    		//chiamo l'activity di Sign In richiedendo il SignOut
	    		Intent intentSignOut = new Intent(this, SignInActivity.class);
	    		intentSignOut.putExtra(Constants.PAR_FUNCTION, "signout");
	    		startActivity(intentSignOut);
	    		finish();
	            return false;
	            
	        case R.id.action_new_portal:
	        	
	    		//chiamo l'activity di Sign In richiedendo il SignOut
	    		Intent intentNewPortal = new Intent(this, NewPortalActivity.class);
	    		startActivityForResult(intentNewPortal,0);
	            return false;

	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Make sure the request was successful
        if (resultCode == RESULT_OK) {  
        	String azione = data.getStringExtra(Constants.PAR_PORTAL_ADDED);
        	if (azione.equals("true")) {
        		flagUpdatePortalData = true;
        	}
        }
	}
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			// A seconda dell'indice ritorno il fragment appropriato
			switch (position) {
			case 0:
				return new PortaliFragment();
			case 1:
				return new OggettiFragment();
			case 2:
				return new AgentiFragment();
			case 3:
				return new ZoneFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

	@Override
	public void onZoneSelectedListener(int position) {
		// Devo refreshare la lista dei portali associati
		flagUpdatePortalData=true;
	}
}
