package com.gmail.nlopatka.ipaddressfinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	private static final String SAVED_GEO_LOCATION = "geo-location";
	private static final String TAG = "MainActivity";
	private ListView infoList;
	private EditText editIP;
	private IpLocationFinderAsync finderTask;
	private GeoLocation geoLocation;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		infoList = (ListView)findViewById(R.id.info);
		editIP = (EditText)findViewById(R.id.edit_ip);
		
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		if (savedInstanceState == null) {
			finderTask = new IpLocationFinderAsync();
			finderTask.execute("");
		} else {
			geoLocation = (GeoLocation) savedInstanceState.getSerializable(SAVED_GEO_LOCATION);
			if (geoLocation != null) {
				infoList.setAdapter(createListAdapter(geoLocation));
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
		if(geoLocation != null) {
			outState.putSerializable(SAVED_GEO_LOCATION, geoLocation);
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void displaySearchError()
	{
		ListAdapter adapter;
		String items[] = new String[]{getString(R.string.find_error)};
		adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
				items);
		infoList.setAdapter(adapter);
	}
	
	private ListAdapter createListAdapter(GeoLocation location) {
		List<String> items;
		items = geoLocation.toStringList();
		
		return new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,items);
	}
	
	private class IpLocationFinderAsync extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {
			String ip = params[0];
			if (!Util.isIPAddress(ip) && !ip.equals("")){
				ip = Util.resolveURL(ip);
				if(ip == null)
					return null;
			}
			return IPAddressFinder.findIPLocation(ip);
		}
		
		@Override
		protected void onPostExecute(String result) {
			finderTask = null;
			if(result == null) {
				displaySearchError();
				geoLocation = null;
			} else {				
				geoLocation = GeoLocation.createFromJSON(result);
				if (geoLocation == null) {
					Log.e(TAG, "Could not parse json: " + result);
					displaySearchError();
					return;
				}
				infoList.setAdapter(createListAdapter(geoLocation));					
			}
			super.onPostExecute(result);			
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
		default:
			Log.e(TAG, "OnClick: Could not find id " + v.getId());
			break;
		}
	}
	
	private void onBMapsPressed() {		
		if (geoLocation != null && geoLocation.latitude != null
				&& geoLocation.longitude != null) {
			String geo;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			geo = String.format("geo:0,0?q=%f,%f(%s)", (double)geoLocation.latitude,
					(double)geoLocation.longitude, geoLocation.ip);
			intent.setData(Uri.parse(geo));
			if (intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			}
		}
	}

	private void hideKeyBoard()
	{
		editIP.clearFocus();
		infoList.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromInputMethod(editIP.getWindowToken(), 0);
	}

	private void onBSearchPressed() {
		String address = editIP.getText().toString().trim();		
		hideKeyBoard();	
		Log.d(TAG, "Start searching location of " + address);
		if (finderTask != null) {
			finderTask.cancel(true);
		}
		finderTask = new IpLocationFinderAsync();
		finderTask.execute(address);
	}
}
