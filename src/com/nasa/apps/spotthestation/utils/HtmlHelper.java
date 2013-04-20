package com.nasa.apps.spotthestation.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.PrettyXmlSerializer;
import org.htmlcleaner.TagNode;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.TextView;

import com.nasa.apps.spotthestation.Constants;
import com.nasa.apps.spotthestation.adapters.NewsAdapter;

/**
 * Helper class to read data from an HTML Document
 */
public class HtmlHelper {

	public static String NASA_PREFIX = "http://www.nasa.gov";
	public static String FULL_HTML_PREFIX = "http://";

	private class News {
		static final String XPATH_ARTICLE_LINKS = 
				"//div[@class='landing-slide']/div[@class='landing-slide-inner']/div[@class='fpss-img_holder_div_landing']/div[@id='fpss-img-div_466']/a/@href";
		static final String XPATH_ARTICLE_IMAGES = 
				"//div[@class='landing-slide']/div[@class='landing-slide-inner']/div[@class='fpss-img_holder_div_landing']/div[@id='fpss-img-div_466']/a/img/@src";
		static final String XPATH_ARTICLE_HEADERS = 
				"//div[@class='landing-slide']/div[@class='landing-slide-inner']/div[@class='landing-fpss-introtext']/div[@class='landing-slidetext']/h1/a";
		static final String XPATH_ARTICLE_DESCRIPTIONS = 
				"//div[@class='landing-slide']/div[@class='landing-slide-inner']/div[@class='landing-fpss-introtext']/div[@class='landing-slidetext']/p";						
	}

	public class Articles {
		public static final String XPATH_SUBTITLE = 
				"//div[@class='name_address']/div[@class='address']/span";
		public static final String XPATH_TITLE = 
				"//div[@class='box_710 box_white box_710_white']/h2";
		public static final String XPATH_CONTENT = 
				"//div[@class='default_style_wrap prejs_body_adjust_detail']";
		public static final String XPATH_CONTENT_TEST = 
				"//span[@class='img_comments_right']/following-sibling::text()";
	}

	public class Expeditions {
		public static final String XPATH_EXPED_DETAILS_CUR = 
				"//div[@id='tab1']/div[@id='stationCurrentLabel0']/ul[@class='content_outline_black']";
		public static final String XPATH_EXPED_DETAILS_FUT = 
				"//div[@id='tab2']/div[@id='stationFutureLabel0']/ul[@class='content_outline_black']";
		public static final String XPATH_EXPED_DETAILS_PAST = 
				"//div[@id='tab3']/div[@id='stationPastLabel0']/ul[@class='content_outline_black']";

		public static final String XPATH_EXPED_MEMBERS_CUR = 
				"//div[@id='tab1']/div[@id='stationCurrentLabel0']/ul[@class='content_outline_black']/a";
		public static final String XPATH_EXPED_MEMBERS_FUT = 
				"//div[@id='tab2']/div[@id='stationFutureLabel0']/ul[@class='content_outline_black']/a";
		public static final String XPATH_EXPED_MEMBERS_PAST = 
				"//div[@id='tab3']/div[@id='stationPastLabel0']/ul[@class='content_outline_black']/a";
	}

	private static final String NEWS_LIST_FILE_NAME = "news_list.xml";
	private static final String XML_FILE_EXTENSION = ".xml";
	private static final String PDF_FILE_EXTENSION = ".pdf";
	private static final String TAG_CONTENT_BODY = "content_body";
	private static final String PREF_NASA_DATA = "nasadata";

	private SharedPreferences mPreferences;
	private SharedPreferences.Editor mEditor;


	HtmlCleaner mCleaner;
	CleanerProperties mProps;
	String filesDir;
	TagNode mNode;
	URL mUrl;

	public HtmlHelper (String filesDir, String url) {
		this.mCleaner = new HtmlCleaner();
		this.filesDir = filesDir;
		mProps = mCleaner.getProperties();
		mProps.setAllowMultiWordAttributes(true);
		try {
			mUrl = new URL(url);
		} catch (Exception e) {
			Constants.logMessage(e.toString());
		}
	}

	public void getNewsArticles () {
		new GetArticleListTask().execute();
	}

	public void getArticleDetails (TextView title, TextView subtitle, TextView content, String url) {
		new GetArticleDetailsTask(title, subtitle, content).execute(url);
	}

	public static boolean isHtmlAvailable (Context context, String url) {
		SharedPreferences mPreferences = context.getSharedPreferences(PREF_NASA_DATA, Context.MODE_PRIVATE);
		String html = mPreferences.getString(url, "");
		if (html.equals("")) {
			return false;	
		}
		return true;
	}

	public void getHtmlFromUrl (Context context, String url) {
		DownloadHtmlTask mTask = new DownloadHtmlTask();
		mTask.execute(new String[] {url});
	}
	
	public static NewsAdapter getNewsArticleAdapter (final Context context, final String html) {
		
		if (isHtmlAvailable(context, html)) {
			return createNewsArticleAdapter(context, html);
		}
		
		return null;		
		
	}

	public static NewsAdapter createNewsArticleAdapter (final Context context, final String html) {

		HtmlCleaner mCleaner = new HtmlCleaner();
		TagNode mNode = mCleaner.clean(html);		
		CleanerProperties mProps = mCleaner.getProperties();
		mProps.setAllowMultiWordAttributes(true);
		try {

			//Get all of the article links
			Object[] mArticles = mNode.evaluateXPath(News.XPATH_ARTICLE_LINKS);
			//Get all of the image links
			Object[] mImages = mNode.evaluateXPath(News.XPATH_ARTICLE_IMAGES);
			//Get all of the Article Titles
			Object[] mTitles = mNode.evaluateXPath(News.XPATH_ARTICLE_HEADERS);
			//Get all of the Article Descriptions
			Object[] mDescriptions = mNode.evaluateXPath(News.XPATH_ARTICLE_DESCRIPTIONS);

			Constants.logMessage("Found : " + mArticles.length + " articles");
			//Value containers
			String [] links = new String [mArticles.length];
			String [] images = new String [mArticles.length];
			String [] titles = new String [mArticles.length];
			String [] descriptions = new String [mArticles.length];

			for (int i = 0; i < mArticles.length; i++) {
				//The Nasa Page returns link that are often not fully qualified URL, so I need to append the prefix if needed. 
				links[i] = mArticles[i].toString().startsWith(FULL_HTML_PREFIX)? mArticles[i].toString() : NASA_PREFIX + mArticles[i].toString();
				images[i] = mImages[i].toString().startsWith(FULL_HTML_PREFIX)? mImages[i].toString() : NASA_PREFIX + mImages[i].toString();
				//On the previous two items we were getting the attribute value
				//Here, we actually need the text inside the actual element, and so we want to cast the object to a TagNode
				//The TagNode allows to extract the Text for the supplied element. 
				titles[i] = ((TagNode)mTitles[i]).getText().toString();
				descriptions[i] = ((TagNode)mDescriptions[i]).getText().toString();
			}
			
			return new NewsAdapter(context, titles, descriptions, images, links);
			
		}
		catch (Exception e) {
			Constants.logMessage(e.toString());
			return null;
		}		
	}

	private String getHtmlFromUrl(String url) {

		Constants.logMessage("AsyncTask calling to donwnload: " + url);

		DefaultHttpClient client = new DefaultHttpClient();		

		HttpGet httpGet = new HttpGet(url);
		String response = "";

		try {
			HttpResponse execute = client.execute(httpGet);
			InputStream content = execute.getEntity().getContent();

			BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
			String s = "";

			while ((s = buffer.readLine()) != null) {
				response += s;
			}

		}		
		catch (Exception e) {
			Constants.logMessage(e.toString());
		}

		return response;
	}

	private class DownloadHtmlTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			return getHtmlFromUrl(params[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			
		}
	}

	private class GetArticleDetailsTask extends AsyncTask<String, Void, Void> {

		URL mArticleUrl;
		WeakReference<TextView> mTitleRef, mSubtitleRef, mContentRef;
		String titleStr, subtitleStr, contentStr;

		public GetArticleDetailsTask (TextView title, TextView subtitle, TextView content) {
			this.mTitleRef = new WeakReference<TextView>(title);
			this.mSubtitleRef = new WeakReference<TextView>(subtitle);
			this.mContentRef = new WeakReference<TextView>(content);			
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				mArticleUrl = new URL(params[0]);
				//try cleaning the supplied url. 
				mNode = mCleaner.clean(mArticleUrl);		

				Object[] objectArray = mNode.evaluateXPath(Articles.XPATH_TITLE);

				titleStr = ((TagNode) objectArray[0]).getText().toString();

				objectArray = mNode.evaluateXPath(Articles.XPATH_SUBTITLE);

				subtitleStr = ((TagNode) objectArray[0]).getText().toString();

				objectArray = mNode.evaluateXPath(Articles.XPATH_CONTENT);

				contentStr = ((TagNode) objectArray[0]).getText().toString();

			} catch (Exception e) {
				Constants.logMessage("Error cleaning file" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {			
			try {
				//For now I am just writing to an xml file to sort of read through
				//God is HTML code ugly. 
				new PrettyXmlSerializer(mProps).writeToFile(
						mNode, filesDir + "blah" + XML_FILE_EXTENSION, "utf-8"
						);

				if (mTitleRef != null && mContentRef != null & mSubtitleRef != null) {
					mTitleRef.get().setText(titleStr);
					mSubtitleRef.get().setText(subtitleStr);
					mContentRef.get().setText(contentStr);
				}

			} catch (Exception e) {
				Constants.logMessage("Error writing to file: " + e.toString());
			}
		}		
	}

	private class GetArticleListTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				//try cleaning the nasa page. 
				mNode = mCleaner.clean(mUrl);

				//Get all of the article links
				Object[] mArticles = mNode.evaluateXPath(News.XPATH_ARTICLE_LINKS);
				//Get all of the image links
				Object[] mImages = mNode.evaluateXPath(News.XPATH_ARTICLE_IMAGES);
				//Get all of the Article Titles
				Object[] mTitles = mNode.evaluateXPath(News.XPATH_ARTICLE_HEADERS);
				//Get all of the Article Descriptions
				Object[] mDescriptions = mNode.evaluateXPath(News.XPATH_ARTICLE_DESCRIPTIONS);

				Constants.logMessage("Found : " + mArticles.length + " articles");
				//Value containers
				String link, image, title, description;

				for (int i = 0; i < mArticles.length; i++) {
					//The Nasa Page returns link that are often not fully qualified URL, so I need to append the prefix if needed. 
					link = mArticles[i].toString().startsWith(FULL_HTML_PREFIX)? mArticles[i].toString() : NASA_PREFIX + mArticles[i].toString();
					image = mImages[i].toString().startsWith(FULL_HTML_PREFIX)? mImages[i].toString() : NASA_PREFIX + mImages[i].toString();
					//On the previous two items we were getting the attribute value
					//Here, we actually need the text inside the actual element, and so we want to cast the object to a TagNode
					//The TagNode allows to extract the Text for the supplied element. 
					title = ((TagNode)mTitles[i]).getText().toString();
					description = ((TagNode)mDescriptions[i]).getText().toString();
					//Only log the values for now. 
					Constants.logMessage("Link to article is " + link);
					Constants.logMessage("Image from article is " + image);
					Constants.logMessage("Title of article is " + title);
					Constants.logMessage("Description of article is " + description);

				}
			} catch (Exception e) { 
				Constants.logMessage("Error cleaning file" + e.toString());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {			
			try {
				//For now I am just writing to an xml file to sort of read through
				//God is HTML code ugly. 
				new PrettyXmlSerializer(mProps).writeToFile(
						mNode, filesDir + NEWS_LIST_FILE_NAME, "utf-8"
						);
			} catch (Exception e) {
				Constants.logMessage("Error writing to file: " + e.toString());
			}
		}		
	}

}
