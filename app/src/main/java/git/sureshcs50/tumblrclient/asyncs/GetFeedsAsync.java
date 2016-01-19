package git.sureshcs50.tumblrclient.asyncs;

import android.app.Activity;

import com.tumblr.jumblr.types.Post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import git.sureshcs50.tumblrclient.utils.Constants;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class GetFeedsAsync extends BaseAsyncTask<List<Post>> {

    private Activity mActivity;
    private String mBlogName = "";
    private int mLimit;
    private int mOffset = 0;
    private int mFeedStatus = 0;

    public GetFeedsAsync(Activity activity, String blogName, int limit, int offset, int feedStatus) {
        this.mActivity = activity;
        this.mBlogName = blogName;
        this.mLimit = limit;
        this.mOffset = offset;
        this.mFeedStatus = feedStatus;
    }

    @Override
    protected List<Post> doInBackground(Object... objects) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("limit", mLimit);
            params.put("offset", mOffset);

            // based on the feed status code it will get List<Post>..
            switch (mFeedStatus) {
                case Constants.FEED_STATUS_DASHBOARD:
                    return getClient().userDashboard(params);
                case Constants.FEED_STATUS_BLOG:
                    return getClient().blogPosts(mBlogName, params);
                case Constants.FEED_STATUS_BLOG_DRAFT:
                    return getClient().blogDraftPosts(mBlogName);
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
