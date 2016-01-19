package git.sureshcs50.tumblrclient.ui.activities;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.TextPost;
import com.tumblr.jumblr.types.User;

import java.util.ArrayList;
import java.util.Arrays;

import git.sureshcs50.tumblrclient.R;
import git.sureshcs50.tumblrclient.asyncs.TextPostAsync;
import git.sureshcs50.tumblrclient.utils.Constants;
import git.sureshcs50.tumblrclient.utils.Utils;

public class PostActivity extends BaseActivity {

    private EditText mEtTitle, mEtBody;
    private Spinner mSpnrBlogs, mSpnrAction;
    private TextPost mTxtPost;
    private ArrayAdapter<String> mSpnrBlogsAdapter;
    private ArrayList<Blog> mBlogs;
    private ArrayList<String> mBlogNames;
    private ArrayList<String> mActions = new ArrayList<String>(Arrays.asList(Constants.POST_ACTION_PUBLISH, Constants.POST_ACTION_DRAFT));
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text_post);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Post");
        }

        mEtTitle = (EditText) findViewById(R.id.etTitle);
        mEtBody = (EditText) findViewById(R.id.etBody);
        mSpnrBlogs = (Spinner) findViewById(R.id.spnrBlogs);
        mSpnrAction = (Spinner) findViewById(R.id.spnrAction);

        mBlogs = new ArrayList<Blog>();
        mBlogNames = new ArrayList<String>();

        mUser = getUser();
        mBlogs.addAll(mUser.getBlogs());
        for (Blog blog : mBlogs)
            mBlogNames.add(blog.getName() + Constants.BASE_HOST_NAME);

        // spinner adapter for blogs..
        mSpnrBlogsAdapter = new ArrayAdapter<String>(PostActivity.this, android.R.layout.simple_spinner_item, mBlogNames);
        mSpnrBlogsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrBlogs.setAdapter(mSpnrBlogsAdapter);

        // spinner adapter for actions..
        ArrayAdapter<String> spnrActionsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mActions);
        spnrActionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnrAction.setAdapter(spnrActionsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post, menu);
        MenuItem actionDone = menu.findItem(R.id.action_done);
        actionDone.setVisible(true);
        actionDone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (validateFields() && Utils.hasConnection(PostActivity.this)) {
                    if (mSpnrAction.getSelectedItem().toString().equalsIgnoreCase(Constants.POST_ACTION_PUBLISH)) {
                        postText(mSpnrBlogs.getSelectedItem().toString(),
                                mEtTitle.getText().toString().trim(),
                                mEtBody.getText().toString().trim(), true);
                        return true;

                    } else if (mSpnrAction.getSelectedItem().toString().equalsIgnoreCase(Constants.POST_ACTION_DRAFT)) {
                        postText(mSpnrBlogs.getSelectedItem().toString(),
                                mEtTitle.getText().toString().trim(),
                                mEtBody.getText().toString().trim(), false);
                        return true;
                    }
                }
                return false;
            }
        });
        return true;
    }

    private boolean validateFields() {
        if (mEtTitle.getText().toString().trim().isEmpty()) {
            mEtTitle.setError("required");
            return false;
        }
        if (mEtBody.getText().toString().trim().isEmpty()) {
            mEtTitle.setError("required");
        }
        return true;
    }

    private void postText(String blogName, String title, String text, final boolean isPublish) {
        new TextPostAsync(blogName, title, text, isPublish) {
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                if (result.equalsIgnoreCase(Constants.RESULT_SUCCESS)) {
                    if (isPublish) {
                        setResult(Constants.RESULT_CODE_POST);
                    } else {
                        setResult(Constants.RESULT_CODE_DRAFT);
                    }
                    finish();
                } else {
                    Toast.makeText(PostActivity.this, getString(R.string.msg_post_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }
}
