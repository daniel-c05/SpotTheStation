package com.nasa.apps.spotthestation.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nasa.apps.spotthestation.R;
import com.nasa.apps.spotthestation.utils.HtmlHelper;

public class NewsArticleFragment extends Fragment {

	private TextView mTitle, mSubtitle, mContent;
	private String mArticleUrl = "http://www.nasa.gov/mission_pages/station/news/earthkam_news.html";
	private String filesDir;

	@Override
	public void onAttach(Activity activity) {
		filesDir = activity.getFilesDir().getAbsolutePath() + "/";
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.news_article, null);		

		mTitle = (TextView) view.findViewById(R.id.article_title);
		mSubtitle = (TextView) view.findViewById(R.id.article_subtitle);
		mContent = (TextView) view.findViewById(R.id.article_content);
		mContent.setText(Html.fromHtml(getString(R.string.dummy_news_content)));

		new HtmlHelper(filesDir, mArticleUrl).getArticleDetails(mTitle, mSubtitle, mContent, mArticleUrl);

		return view;
	}
}
