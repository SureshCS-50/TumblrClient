package git.sureshcs50.tumblrclient.asyncs;

import android.app.Activity;

import com.tumblr.jumblr.types.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import git.sureshcs50.tumblrclient.R;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class GetDashboardFeedAsync extends BaseAsyncTask<List<Post>> {

    private Activity mActivity;
    private String mBlogName = "";
    private int mLimit;

    public GetDashboardFeedAsync(Activity activity, String blogName, int limit) {
        this.mActivity = activity;
        this.mBlogName = blogName;
        this.mLimit = limit;
    }

    @Override
    protected List<Post> doInBackground(Object... objects) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("limit", mLimit);
        if(mBlogName.trim().isEmpty() || mBlogName.equalsIgnoreCase(mActivity.getResources().getString(R.string.tag_dashboard))) {
            return getClient().userDashboard(params);
        } else{
            return getClient().blogPosts(mBlogName, params);
        }
    }
}

