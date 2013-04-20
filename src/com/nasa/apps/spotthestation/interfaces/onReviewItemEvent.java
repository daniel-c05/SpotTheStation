package com.nasa.apps.spotthestation.interfaces;

import android.os.Bundle;

public interface onReviewItemEvent {
	void onFlagChanged(int pageNum, boolean value);
	void onFormDataChanged(int which, Bundle info);
	void onReviewItem(int which);
} 
