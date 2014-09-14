package com.gmail.nlopatka.ipaddressfinder;

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.util.Log;

public final class Util {
	private static String TAG = "Util";
	private Util() {}
	
	public static boolean isIPAddress(String str)
	{
		return str.matches("\\d+.\\d+.\\d+.\\d+");
	}
	
	public static String[] resolveURL(String url)
	{
		try {
			InetAddress[] addresses = InetAddress.getAllByName(url);
			String[] res = new String[addresses.length];
			int i = 0;
			for(InetAddress addr:addresses) {
				res[i++] = addr.getHostAddress();
			}
			return res;
		} catch (UnknownHostException e) {
			Log.e(TAG, "Could not resolve url " + url + " : " + e.getMessage());
			return null;			
		}		
	}
}
