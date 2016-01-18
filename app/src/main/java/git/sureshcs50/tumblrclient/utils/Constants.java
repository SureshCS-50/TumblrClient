package git.sureshcs50.tumblrclient.utils;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class Constants {
    // name of shared preferences..
    public static final String TUMBLR_CLIENT_PREFERENCES = "tumblr_client_preferences";
    public static final String PREFS_ACCESS_TOKEN = "prefs_access_token";
    public static final String PREFS_TOKEN_SECRET = "prefs_token_secret";

    // urls..
    public static final String BASE_URL = "";

    // authentication
    public static final String OAUTH_BASE_URL = "http://www.tumblr.com/oauth/";
    public static final String REQUEST_URL = OAUTH_BASE_URL+"request_token";
    public static final String ACCESS_URL = OAUTH_BASE_URL+"access_token";
    public static final String AUTHORIZE_URL = OAUTH_BASE_URL+"authorize";

    // taken from tumblr app registration
    public static final String CONSUMER_KEY = "LM893Nk8Z4GhZtpPHUeALqW5YEX2rXb1rewdAXuduIBmra0Q8B";
    public static final String CONSUMER_SECRET = "yd7IRwLT7Y55V2DSUrM2mtiiw0QtOq6qABdY4QmB8zP9xo5Gss";

    // callback url..
    public static final String	OAUTH_CALLBACK_SCHEME	= "oauthflow-tumblr";
    public static final String	OAUTH_CALLBACK_HOST		= "callback";
    public static final String	OAUTH_CALLBACK_URL		= OAUTH_CALLBACK_SCHEME + "://" + OAUTH_CALLBACK_HOST;

    // post pagination limit..
    public static final int POST_PAGINATION_LIMIT = 2;

    // blog base host name..
    public static final String BASE_HOST_NAME = ".tumblr.com";
}