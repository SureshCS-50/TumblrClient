package git.sureshcs50.tumblrclient.utils;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class Constants {
    // name of shared preferences..
    public static final String TUMBLR_CLIENT_PREFERENCES = "tumblr_client_preferences";
    public static final String PREFS_ACCESS_TOKEN = "prefs_access_token";
    public static final String PREFS_TOKEN_SECRET = "prefs_token_secret";
    public static final String PREFS_USER = "prefs_user";

    // authentication
    public static final String OAUTH_BASE_URL = "http://www.tumblr.com/oauth/";
    public static final String REQUEST_URL = OAUTH_BASE_URL+"request_token";
    public static final String ACCESS_URL = OAUTH_BASE_URL+"access_token";
    public static final String AUTHORIZE_URL = OAUTH_BASE_URL+"authorize";

    // taken from tumblr app registration
    public static final String CONSUMER_KEY = "Your customer key";
    public static final String CONSUMER_SECRET = "Your customer secret key";

    // callback url..
    public static final String	OAUTH_CALLBACK_SCHEME	= "oauthflow-tumblr";
    public static final String	OAUTH_CALLBACK_HOST		= "callback";
    public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

    // post pagination limit..
    public static final int POST_PAGINATION_LIMIT = 2;

    // blog base host name..
    public static final String BASE_HOST_NAME = ".tumblr.com";

    // post actions..
    public static final String POST_ACTION_PUBLISH = "Publish now";
    public static final String POST_ACTION_DRAFT = "Save as draft";

    // post status..
    public static final String POST_STATE_PUBLISH = "published";
    public static final String POST_STATE_DRAFT = "draft";

    // Text post result..
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_FAIL = "fail";

    // request and result code..
    public static final int REQUEST_CODE_POST = 001;
    public static final int RESULT_CODE_POST = 002;
    public static final int RESULT_CODE_DRAFT = 003;

    // get post feeds status code..
    public static final int FEED_STATUS_DASHBOARD = 0;
    public static final int FEED_STATUS_BLOG = 1;
    public static final int FEED_STATUS_BLOG_DRAFT = 2;
}