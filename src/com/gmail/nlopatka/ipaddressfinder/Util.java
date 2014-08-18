package com.gmail.nlopatka.ipaddressfinder;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public final class Util {
	private static String TAG = "Util";
	private Util() {}
	public static String getWifiIPAddress()
	{
		try {
			List<NetworkInterface> interfaces = Collections.list( NetworkInterface.getNetworkInterfaces() );
			for(NetworkInterface i:interfaces) {
				List<InetAddress> addrs = Collections.list(i.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {                     
						if (InetAddressUtils.isIPv4Address(addr.getHostAddress())) 
							return addr.getHostAddress();
					}
				}
			}
		} catch (SocketException e) {
			Log.e(TAG, "Could not obtain network interfaces: " + e.getMessage());
			return null;
		}
		return null;
	}
}
