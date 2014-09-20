package com.gmail.nlopatka.ipaddressfinder;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements OnClickListener{
	private static final String SAVED_GEO_LOCATION = "geo-location";
	private static final String TAG = "MainActivity";
	private ExpandableListView infoList;
	private EditText editIP;
	private IpLocationFinderAsync finderTask;
	private ArrayList<GeoLocation> geoLocations = new ArrayList<GeoLocation>();
	private TextView errorField;
	private ProgressBar progressBar;
	private ImageButton searchButton;
	private Drawable searchDrawable;
	private Drawable cancelDrawable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		searchButton = (ImageButton)findViewById(R.id.bsearch);
		searchDrawable = getResources().getDrawable(android.R.drawable.ic_menu_search);
		cancelDrawable = getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel);
		infoList = (ExpandableListView)findViewById(R.id.info);
		editIP = (EditText)findViewById(R.id.edit_ip);
		errorField = (TextView) findViewById(R.id.main_error_field);
		progressBar = (ProgressBar) findViewById(R.id.main_progress_bar);
		
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		if (savedInstanceState == null) {
			finderTask = new IpLocationFinderAsync();
			finderTask.execute("");
		} else {
			ArrayList<GeoLocation> newGeoLocations = (ArrayList<GeoLocation>) savedInstanceState.getSerializable(SAVED_GEO_LOCATION);
			if (newGeoLocations != null && newGeoLocations.size() > 0) {
				geoLocations = newGeoLocations;
				infoList.setAdapter(new InfoListAdapter(geoLocations, this));
				errorField.setVisibility(View.INVISIBLE);
				infoList.setVisibility(View.VISIBLE);
			} else {
				displaySearchError();
			}
		}
		
		Log.d(TAG, "onCreate");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.d(TAG, "onRestoreInstanceState");
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onStart() {
		Log.d(TAG, "onStart");
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstanceState");
		if(geoLocations != null && geoLocations.size() > 0) {
			outState.putSerializable(SAVED_GEO_LOCATION, geoLocations);
		}
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		Log.d(TAG, "onStop");
		if (finderTask != null) {
			finderTask.cancel(true);
			finderTask = null;
		}
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void onBackPressed() {		
		hideKeyBoardIfVisible();
		super.onBackPressed();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displaySearchError()
	{
		errorField.setVisibility(View.VISIBLE);
		infoList.setVisibility(View.INVISIBLE);		
	}
	
	private void refreshScreen (boolean isLoading) {
		if (isLoading) {
			progressBar.setVisibility(View.VISIBLE);
			searchButton.setImageDrawable(cancelDrawable);
		} else {
			progressBar.setVisibility(View.INVISIBLE);
			searchButton.setImageDrawable(searchDrawable);
		}
	}
	
	private boolean isWorking ()
	{
		return progressBar.getVisibility() == View.VISIBLE;
	}
	
	private class IpLocationFinderAsync extends AsyncTask<String, String, String[]>
	{
		@Override
		protected void onPreExecute() {
			refreshScreen(true);
			super.onPreExecute();
		}
		@Override
		protected String[] doInBackground(String... params) {
			String ip = params[0];
			if (!Util.isIPAddress(ip) && !ip.equals("")){
				String [] addresses = Util.resolveURL(ip);
				String [] res;
				int i = 0;;
				if(addresses == null) {
					return null;
				}
				
				res = new String[addresses.length];				
				for(String addr:addresses) {
					if(isCancelled())
						break;
					res[i++] = IPAddressFinder.findIPLocation(addr);
				}
				return res;
			} else {
				return new String[]{IPAddressFinder.findIPLocation(ip)};
			}
		}
		
		@Override
		protected void onCancelled() {
			refreshScreen(false);
			super.onCancelled();
		}
		
		@Override
		protected void onPostExecute(String[] results) {
			refreshScreen(false);
			finderTask = null;
			geoLocations.clear();
			if(results == null) {
				displaySearchError();				
			} else {
				for(String jsonRes:results) {
					if (jsonRes == null)
						continue;
					GeoLocation cur = GeoLocation.createFromJSON(jsonRes);
					if(cur != null) {
						geoLocations.add( cur);
					} else {
						Log.e(TAG, "Could not parse json: " + jsonRes);
					}
				}
				if (geoLocations.size() == 0) {					
					displaySearchError();
					return;
				}
				infoList.setAdapter(new InfoListAdapter(geoLocations, MainActivity.this));
				if (results.length == 1) {
					infoList.expandGroup(0);
				}
				errorField.setVisibility(View.INVISIBLE);
				infoList.setVisibility(View.VISIBLE);
			}
			super.onPostExecute(results);			
		}		
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bsearch:
			onBSearchPressed();
			break;
		case R.id.bmaps:
			onBMapsPressed();
			break;
		case R.id.info_list_group_bview_on_map:
			onGroupViewOnMap(v);
			break;
		default:
			Log.e(TAG, "OnClick: Could not find id " + v.getId());
			break;
		}
	}
	
	private void onGroupViewOnMap(View v) {
		Object tag = v.getTag();
		GeoLocation geo;
		if (tag == null || !(tag instanceof GeoLocation)) {
			Log.e(TAG, "Could not display on maps: button has wrong tag");
			return;
		}
		geo = (GeoLocation) tag;
		
		runMapActivity(geo);
	}
	
	private void runMapActivity (GeoLocation geo)
	{
		if (geo!= null && geo.latitude != null && geo.longitude != null) {
			String geostr;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			geostr = String.format("geo:0,0?q=%f,%f(%s)", geo.latitude,
					(double)geo.longitude, geo.ip);
			intent.setData(Uri.parse(geostr));
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			}
		} else {
			Log.e(TAG, "Could not run map activity: wrong object GeoLocation");
		}
	}

	private void onBMapsPressed() {		
		if (geoLocations != null && geoLocations.size() > 0 ) {
			runMapActivity(geoLocations.get(0));
		}
	}

	private void hideKeyBoardIfVisible()
	{
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		//imm.toggleSoftInput(0, 0);
		//imm.hideSoftInputFromInputMethod(editIP.getWindowToken(), 0);
		
		imm.hideSoftInputFromWindow(editIP.getWindowToken(), 0);
		editIP.clearFocus();
		infoList.requestFocus();		
	}

	private void onBSearchPressed() {
		boolean wasWorking = isWorking(); 
		
		if (finderTask != null) {
			finderTask.cancel(true);
			finderTask = null;
		}
		
		if (!wasWorking) {
			String address = editIP.getText().toString().trim();		
			hideKeyBoardIfVisible();	
			Log.d(TAG, "Start searching location of " + address);			
			finderTask = new IpLocationFinderAsync();
			finderTask.execute(address);
		}
	}
}
