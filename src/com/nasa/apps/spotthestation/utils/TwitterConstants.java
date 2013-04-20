package com.nasa.apps.spotthestation.utils;


public class TwitterConstants {

	    // public static final SocialNetConstant.Type SOCIAL_NET_TYPE =
	    // SocialNetConstant.Type.Twitter;

	    /*
		 * 
		 */
	    // Create a new key/secret here: https://dev.twitter.com/apps
	    public static final String TWITTER_CONSUMER_KEY = "CDBBk2XBPFpClxoiY9LQ";
	    public static final String TWITTER_CONSUMER_SECRET = "Xq5BsPFfPkKWWpCGMVVWZeY63H9K45lcaP7wkmP7k";

	    public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	    public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	    public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

	    public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	    public static final String OAUTH_CALLBACK_HOST = "callback";
	    public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
	            + "://" + OAUTH_CALLBACK_HOST;
	   
	    /*
		 * 
		 */
	    public static final int REQUEST_CODE_IMAGE_PICKER = 12345;
	    public static final int REQUEST_CODE_CAMERA = 12346;

	    public static final String JPEG_FILE_PREFIX = "IMG_";
	    public static final String JPEG_FILE_SUFFIX = ".jpg";
}
