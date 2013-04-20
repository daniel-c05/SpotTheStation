package com.nasa.apps.spotthestation.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.nasa.apps.spotthestation.R;

/**
 * Adapter used to populate the News Feed List. 
 *
 */
public class NewsAdapter extends BaseAdapter {

	/**
	 * The layout inflater used to inflate the resource file. 
	 */
	private LayoutInflater mInflater;
	/**
	 * Resources used to access the JPEGs stored on the RES folder. 
	 */
	private Resources mResources;
	
	private String [] mTitles, mDescriptions, mImages, mLinks;
	
	public NewsAdapter (Context context, String [] titles, String [] descriptions, String [] images, String [] links) {
		mResources = context.getResources();
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mTitles = titles;
		mDescriptions = descriptions;
		mImages = images;
		mLinks = links;
	}

	@Override
	public int getCount() {
		if (mLinks != null) {
			return mLinks.length;
		}
		else {
			return 0;
		}		
	}

	@Override
	public Object getItem(int arg0) {
		if (mLinks != null) {
			return mLinks[arg0];
		}
		else {
			return 0;
		}
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int pos, View view, ViewGroup arg2) {
		ViewHolder mHolder;
		if (view == null) {
			//If the view is new, inflate the layout
			view = mInflater.inflate(R.layout.news_list_item, null);		
			
			mHolder = new ViewHolder();
			mHolder.image = (ImageView) view.findViewById(R.id.news_image);
			mHolder.title = (TextView) view.findViewById(R.id.news_title);
			mHolder.content = (TextView) view.findViewById(R.id.news_content);
			
			view.setTag(mHolder);
		}
		
		else {			
			mHolder = (ViewHolder) view.getTag();
		}
		
		UrlImageViewHelper.setUrlDrawable(mHolder.image, mImages[pos]);
		mHolder.title.setText(mTitles[pos]);
		mHolder.content.setText(mDescriptions[pos]);
		
		return view;
	}
	
	private void setAsyncImageResource (final ImageView view, final int resource, final int position) {		
		
		GetAndSetScaledImage task = new GetAndSetScaledImage(view, resource, position);
		task.execute();
				
	}
	
	/**
	 * Simple Async implementation to load the bitmap images. 
	 * This needs some work as the images seem to load funny on the adapter as you scroll. 
	 * Also, once we get the data from the NASA site, this would require a re-do. 
	 */
	class GetAndSetScaledImage extends AsyncTask<Void, Void, Void> {

		Bitmap mBitmap;
		int resource;
		int position;
		//private final WeakReference<ImageView> imageViewReference;
		private ImageView mImageView;
		
		public GetAndSetScaledImage (final ImageView view, final int resource, final int position) {
			this.resource = resource;
			//this.imageViewReference = new WeakReference<ImageView>(view);
			this.mImageView = view;
			this.position = position;
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			//Now that the view is not null, set the content
			mBitmap = BitmapFactory.decodeResource(mResources, resource);
			//We want to pre-scale, as auto-scaling at runtime is expensive
			mBitmap = Bitmap.createScaledBitmap(mBitmap, 96, 96, true);
			return null;
		}			
		
		protected void onPostExecute(Void result) {
			if (!isEnabled(position)) {
				return;
			}
			if (isCancelled()) {
				mBitmap = null;
			}
			
			if (mImageView != null) {
				if (mImageView.getTag() == ((Object)position)) {
					mImageView.setImageBitmap(mBitmap);
				}	
			}
			
			/*
			if (imageViewReference != null) {	
				ImageView view = imageViewReference.get();
				if (view != null) {
					if (view.getTag() == ((Object)position)) {
						view.setImageBitmap(mBitmap);
					}				
				}
			}
			*/
		};
	}
	
	/**
	 * Class used to store the references to Views in the container. 
	 * The usage of this ViewHolder patterns helps reduce the time in which the list is drawn. 
	 */
	private class ViewHolder {
		ImageView image;
		TextView title, content;
	}
	
}
