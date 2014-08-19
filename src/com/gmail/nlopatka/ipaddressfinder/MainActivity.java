package com.gmail.nlopatka.ipaddressfinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
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
	private static final String TAG = "MainActivity";
	private ListView infoList;
	private EditText editIP;
	private IpLocationFinderAsync finderTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		infoList = (ListView)findViewById(R.id.info);
		editIP = (EditText)findViewById(R.id.edit_ip);
		
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	protected void onResume() {				
		super.onResume();
		finderTask = new IpLocationFinderAsync();
		finderTask.execute("");
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
	
	private class IpLocationFinderAsync extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return IPAddressFinder.findIPLocation(params[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			finderTask = null;
			if(result == null) {
				displaySearchError();
			} else {
				JSONObject json;
				ListAdapter adapter;
				try {
					List<String> items = new ArrayList<String>();
					json = new JSONObject(result);
					Iterator<String> keysIterator = json.keys();
					while(keysIterator.hasNext()) {
						String key = keysIterator.next();
						String val = json.optString(key);
						if(!val.equals("")) {
							items.add(String.format("%s: %s", key, val));
						}
					}					
					adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,items);
					infoList.setAdapter(adapter);
				} catch (JSONException e) {
					Log.e(TAG, "Could not parse JSON:" + e.getMessage());
					displaySearchError();
					return;
				}				
			}
			super.onPostExecute(result);			
		}		
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bsearch:
			onBSearchPressed();
			break;
		default:
			Log.e(TAG, "OnClick: Could not find id " + v.getId());
			break;
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
