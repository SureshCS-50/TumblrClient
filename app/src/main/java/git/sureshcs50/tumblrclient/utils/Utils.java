package git.sureshcs50.tumblrclient.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.regex.Pattern;

import git.sureshcs50.tumblrclient.TumblrClientApplication;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class Utils {
    public static boolean validateEmailAddress(String email) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        if (!emailPattern.matcher(email).matches()) {
            return false;
        }
        return true;
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnectedOrConnecting()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnectedOrConnecting()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            return true;
        }

        return false;
    }

    public static DisplayImageOptions getDisplayImageOptionsBuilder(int default_pic) {
        return new DisplayImageOptions.Builder()
                .showImageForEmptyUri(default_pic)
                .showImageOnLoading(default_pic)
                .showImageOnFail(default_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public static void clearAuthTokeCache(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.TUMBLR_CLIENT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        TumblrClientApplication.setClientAsNull();
    }
}
