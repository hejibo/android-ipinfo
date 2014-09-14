package com.gmail.nlopatka.ipaddressfinder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class GeoLocation implements Serializable{	
	private static final long serialVersionUID = 1L;
	private static final String TAG = "GeoLocation";
	
	private List<String> listRepresentation;
	
	public String ip;
	public Double latitude;
	public Double longitude;
	public String country_name;
	public String country_code;
	public String region_code;
	public String region_name;
	public String city;
	public String zipcode;
	public String metro_code;
	public String area_code;	
	
	public static GeoLocation createFromJSON (String json) {		
		try {
			GeoLocation geo = new GeoLocation();
			JSONObject jsonObject = new JSONObject(json);
			
			geo.ip = jsonObject.optString("ip");
			geo.country_name = jsonObject.optString("country_name");
			geo.country_code = jsonObject.optString("country_code");
			geo.region_code = jsonObject.optString("region_code");
			geo.region_name = jsonObject.optString("region_name");
			geo.city = jsonObject.optString("city");
			geo.zipcode = jsonObject.optString("zipcode");
			geo.metro_code = jsonObject.optString("metro_code");
			geo.area_code = jsonObject.optString("area_code");
			
			if (jsonObject.has("latitude")) {
				geo.latitude = jsonObject.optDouble("latitude");
			}			
			if (jsonObject.has("longitude")) {
				geo.longitude = jsonObject.optDouble("longitude");
			}
			
			return geo;
		} catch (JSONException e) {
			Log.e (TAG, "Could not parse json: " + e.getMessage());
			return null;			
		}		
	}
	
	public List<String> getAsStringList(boolean update)
	{
		if (!update && listRepresentation != null) {
			return listRepresentation;
		}
		
		listRepresentation = new ArrayList<String>();
		
		if (this.ip != null && !this.ip.equals("")) {
			listRepresentation.add(String.format("ip: %s",this.ip));
		}
		if (this.country_name != null && !this.country_name.equals("")) {
			listRepresentation.add(String.format("county name: %s",this.country_name));
		}
		if (this.region_name != null && !this.region_name.equals("")) {
			listRepresentation.add(String.format("region name: %s",this.region_name));
		}
		if (this.city != null && !this.city.equals("")) {
			listRepresentation.add(String.format("city: %s",this.city));
		}
		if (this.longitude != null) {
			listRepresentation.add(String.format("longitude: %f",this.longitude));
		}
		if (this.latitude != null) {
			listRepresentation.add(String.format("latitude: %f",this.latitude));
		}
		return listRepresentation;
	}
}
