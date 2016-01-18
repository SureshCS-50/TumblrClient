package git.sureshcs50.tumblrclient.asyncs;

import android.widget.Toast;

import com.tumblr.jumblr.types.TextPost;

import git.sureshcs50.tumblrclient.utils.Constants;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class TextPostAsync extends BaseAsyncTask<String> {

    private String mBlogName = "", mTitle = "", mPostBody = "";
    private boolean isPublish = true;

    public TextPostAsync(String blogName, String title, String postBody, boolean isPublish) {
        this.mBlogName = blogName;
        this.mTitle = title;
        this.mPostBody = postBody;
        this.isPublish = isPublish;
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            TextPost textPost = getClient().newPost(mBlogName, TextPost.class);
            textPost.setBlogName(mBlogName);
            textPost.setTitle(mTitle);
            textPost.setBody(mPostBody);
            // if it is not publish.. then it will be save as draft..
            if(!isPublish){
                textPost.setState(Constants.POST_STATE_DRAFT);
            }
            textPost.save();
            return Constants.RESULT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return Constants.RESULT_FAIL;
        }
    }
}
