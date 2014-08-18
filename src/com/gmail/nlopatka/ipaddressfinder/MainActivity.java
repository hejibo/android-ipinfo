package com.gmail.nlopatka.ipaddressfinder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity {
	private static final String TAG = "MainActivity";
	private ListView infoList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_main);
		infoList = (ListView)findViewById(R.id.info);
	}
	
	@Override
	protected void onResume() {				
		super.onResume();
		new IpLocationFinderAsync().execute();
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
	private class IpLocationFinderAsync extends AsyncTask<String, String, String>
	{

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return IPAddressFinder.findIPLocation("8.8.8.8");
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			ListAdapter adapter;
			if(result == null) {
				String items[] = new String[]{getString(R.string.find_error)};
				adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,
						items);
				infoList.setAdapter(adapter);
			} else {
				JSONObject json;
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
					return;
				}				
			}
			super.onPostExecute(result);
		}
		
	}
}
