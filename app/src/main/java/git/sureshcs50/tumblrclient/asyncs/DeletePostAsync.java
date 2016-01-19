package git.sureshcs50.tumblrclient.asyncs;

import git.sureshcs50.tumblrclient.utils.Constants;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class DeletePostAsync extends BaseAsyncTask<String> {
    private String mBlogName;
    private long mPostId;

    public DeletePostAsync(String blogName, long postId) {
        this.mBlogName = blogName;
        this.mPostId = postId;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            getClient().postDelete(mBlogName, mPostId);
            return Constants.RESULT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.RESULT_FAIL;
        }
    }
}
