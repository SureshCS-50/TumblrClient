package git.sureshcs50.tumblrclient.asyncs;

import com.tumblr.jumblr.types.Post;

import java.util.HashMap;
import java.util.Map;

import git.sureshcs50.tumblrclient.utils.Constants;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class EditPostAsync extends BaseAsyncTask<String> {
    private String mBlogName;
    private long mPostId;
    private Post mPost;
    private String mState;

    public EditPostAsync(Post post, String state) {
        this.mPost = post;
        this.mBlogName = mPost.getBlogName();
        this.mPostId = mPost.getId();
        this.mState = state;
    }

    @Override
    protected String doInBackground(Object... objects) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("state", mState);
            getClient().postEdit(mBlogName, mPostId, params);
            return Constants.RESULT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.RESULT_FAIL;
        }
    }
}
