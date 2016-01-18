package git.sureshcs50.tumblrclient.ui.fragments;

import android.support.v4.app.Fragment;

import com.tumblr.jumblr.JumblrClient;

import git.sureshcs50.tumblrclient.TumblrClientApplication;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class BaseFragment extends Fragment {
    public JumblrClient getClient() {
        return TumblrClientApplication.getClient();
    }
}
