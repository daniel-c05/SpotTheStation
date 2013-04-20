package com.nasa.apps.spotthestation.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nasa.apps.spotthestation.R;

/**
 * Adapter used to populate the List on the Sliding Drawer. 
 */
public class SlidingMenuAdapter extends BaseAdapter {
	
	/**
	 * Order is open to changes.
	 */
	private static final String [] ENTRIES = {
		"Sightings",
		"Alerts",
		"About ISS",
		"Go Social"
	};
	
	private static final int [] ENTRY_ICONS = {
		R.drawable.ic_action_map_blue,
		R.drawable.ic_action_warning,
		R.drawable.ic_action_info,
		R.drawable.ic_action_dialog
	};
	
	LayoutInflater mInflater;
	
	public SlidingMenuAdapter (Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return ENTRIES.length;
	}

	@Override
	public Object getItem(int pos) {
		return pos;
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder;
		if (convertView == null) {
			//If the view is new, inflate the layout
			convertView = mInflater.inflate(R.layout.sliding_menu_item, null);		
			
			mHolder = new ViewHolder();
			mHolder.image = (ImageView) convertView.findViewById(R.id.sliding_image);
			mHolder.title = (TextView) convertView.findViewById(R.id.sliding_text);
			
			convertView.setTag(mHolder);
		}
		
		else {			
			mHolder = (ViewHolder) convertView.getTag();
		}
				
		mHolder.image.setImageResource(ENTRY_ICONS[position]);
		mHolder.title.setText(ENTRIES[position]);
		
		return convertView;
	}
	
	private class ViewHolder {
		ImageView image;
		TextView title;
	}	
	
}
