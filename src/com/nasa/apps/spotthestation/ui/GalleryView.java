package com.nasa.apps.spotthestation.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.Scroller;

/**
 * Custom View that allows multiple images to be loaded horizontally. 
 * @author Daniel
 *
 */
public class GalleryView extends AbsListView {
	
	private Scroller mScroller;

	public GalleryView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	public GalleryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	 
	public GalleryView(Context context) {
		super(context);	
	}

	@Override
	public ListAdapter getAdapter() {
		
		return null;
	}

	@Override
	public void setSelection(int arg0) {
		
	}	
	
	
		
}
