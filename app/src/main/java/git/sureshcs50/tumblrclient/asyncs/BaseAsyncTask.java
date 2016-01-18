package git.sureshcs50.tumblrclient.asyncs;

import android.os.AsyncTask;

import com.tumblr.jumblr.JumblrClient;

import git.sureshcs50.tumblrclient.TumblrClientApplication;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public abstract class BaseAsyncTask<T> extends AsyncTask<Object, Void, T> {
    private JumblrClient mJumblrClient;

    protected BaseAsyncTask(){
        mJumblrClient = TumblrClientApplication.getClient();
    }

    public JumblrClient getClient() {
        return mJumblrClient;
    }
}
