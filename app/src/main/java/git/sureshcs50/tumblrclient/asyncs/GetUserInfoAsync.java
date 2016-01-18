package git.sureshcs50.tumblrclient.asyncs;

import com.tumblr.jumblr.types.User;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class GetUserInfoAsync extends BaseAsyncTask<User> {
    @Override
    protected User doInBackground(Object... params) {
        return getClient().user();
    }
}
