package git.sureshcs50.tumblrclient;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.tumblr.jumblr.JumblrClient;

import git.sureshcs50.tumblrclient.utils.Constants;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class TumblrClientApplication extends Application {

    public static final String TAG = TumblrClientApplication.class.getSimpleName();
    public static SharedPreferences mPrefs;
    public static JumblrClient mJumblrClient = null;

    // a singleton to get jumblr client.
    public static JumblrClient getClient() {
        if (mJumblrClient == null) {
            String access_token = mPrefs.getString(Constants.PREFS_ACCESS_TOKEN, "");
            String token_secret = mPrefs.getString(Constants.PREFS_TOKEN_SECRET, "");
            if (!access_token.isEmpty() && !token_secret.isEmpty()) {
                mJumblrClient = new JumblrClient(
                        Constants.CONSUMER_KEY,
                        Constants.CONSUMER_SECRET
                );
                mJumblrClient.setToken(
                        access_token,
                        token_secret
                );
            }
        }
        return mJumblrClient;
    }

    public static void setClientAsNull(){
        mJumblrClient = null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPrefs = getSharedPreferences(Constants.TUMBLR_CLIENT_PREFERENCES, MODE_PRIVATE);
        getClient();
        initUniversalImageLoader(getApplicationContext());
    }

    private void initUniversalImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .extraForDownloader(null)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();

        ImageLoaderConfiguration config
                = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .defaultDisplayImageOptions(defaultOptions)
                .build();

        ImageLoader.getInstance().init(config);
    }

}
