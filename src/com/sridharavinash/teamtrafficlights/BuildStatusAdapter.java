package com.sridharavinash.teamtrafficlights;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

class BuildStatusAdapter extends ArrayAdapter<TeamCityBuilds>{
	private final Activity context;
	private final ArrayList<TeamCityBuilds> statusList;
	
	static class ViewHolder{
		public TextView statusText;
		public TextView dateText;
		public ImageView image;
	}
	
	public BuildStatusAdapter(Activity context, ArrayList<TeamCityBuilds> projlist){
		super(context,R.layout.buildstatuslist);
		this.statusList = projlist;
		this.context = context;
	}
	
	@Override
	public int getCount(){
		return statusList.size();
	}
	@Override
	public View getView(int pos,View convertView, ViewGroup parent){
		View rowView = convertView;
		if(rowView == null){
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.listitem, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.image = (ImageView)rowView.findViewById(R.id.imageView1);
			viewHolder.dateText = (TextView)rowView.findViewById(R.id.datetext);
			viewHolder.statusText = (TextView)rowView.findViewById(R.id.statustext);
			rowView.setTag(viewHolder);
			
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		TeamCityBuilds builds = statusList.get(pos);
		holder.statusText.setText(builds.status);
		holder.dateText.setText(builds.startDate);
		
		if (builds.status.startsWith("SUCCESS")) {
			holder.image.setImageResource(R.drawable.green);
		} else {
			holder.image.setImageResource(R.drawable.red);
		}
		return rowView;
	}
}