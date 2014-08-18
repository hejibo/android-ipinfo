package com.gmail.nlopatka.ipaddressfinder;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class IPAddressFinder {
	private static final String TAG = "IPAddressFinder";
	static public String findIPLocation (String ip) {
		
		if (ip == null) {
			return null;
		} else {
			HttpClient client = new DefaultHttpClient();
			String url = String.format("http://freegeoip.net/json/%s", ip);
			HttpGet request = new HttpGet(url);			
			try {
				HttpResponse response = client.execute(request);
				return EntityUtils.toString(response.getEntity());
			} catch (IOException e) {
				Log.e(TAG, "Could not execute HTTP GET: " + e.getMessage());
				return null;
			}
		}		
	}
}
