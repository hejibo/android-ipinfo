package com.gmail.nlopatka.ipaddressfinder;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class InfoListAdapter extends BaseExpandableListAdapter {
	private List<GeoLocation> data;
	private MainActivity activity;
	private LayoutInflater layoutInflater;
	
	public InfoListAdapter (List<GeoLocation> data, MainActivity activity) {
		this.data = data; 
		this.activity = activity;
		this.layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Object getChild(int groupPos, int childPos) {	
		return data.get(groupPos).getAsStringList(false).get(childPos);
	}

	@Override
	public long getChildId(int arg0, int arg1) {	
		return 0;
	}

	@Override
	public View getChildView(int groupPos, int childPos, boolean isLastChild, View convertView,
			ViewGroup parent) {
		String details;
		View view;
		TextView detailsView;
		details = data.get(groupPos).getAsStringList(false).get(childPos);
		if (convertView == null) {
			view = layoutInflater.inflate(R.layout.main_info_list_group_details, null);
		} else {
			view = convertView;
		}				
		detailsView = (TextView) view.findViewById(R.id.info_list_details);
		
		detailsView.setText(details);		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPos) {
		return data.get(groupPos).getAsStringList(false).size();
	}

	@Override
	public Object getGroup(int groupPos) {
		return data.get(groupPos);
	}

	@Override
	public int getGroupCount() {
		if (data == null)
			return 0;
		return data.size();
	}

	@Override
	public long getGroupId(int groupPosition) {	
		return 0;
	}
	
	@Override
	public View getGroupView(int groupPos, boolean isExpandable, View convertView, ViewGroup parent) {
		GeoLocation geo;
		View view;
		TextView groupName;
		ImageButton bViewOnMaps;
		geo = data.get(groupPos);
		if (convertView == null) {
			view = layoutInflater.inflate(R.layout.main_info_list_group, null);
		} else {
			view = convertView;
		}				
		groupName = (TextView) view.findViewById(R.id.info_list_group_name);
		bViewOnMaps = ((ImageButton)view.findViewById(R.id.info_list_group_bview_on_map));
		bViewOnMaps.setFocusable(false);
		bViewOnMaps.setTag(geo);
		bViewOnMaps.setOnClickListener(activity);
		
		groupName.setText(geo.ip);
		
		return view;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return false;
	}

}
