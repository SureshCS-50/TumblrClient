package git.sureshcs50.tumblrclient.ui.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.List;

import git.sureshcs50.tumblrclient.R;
import git.sureshcs50.tumblrclient.adapters.FeedsAdapter;
import git.sureshcs50.tumblrclient.asyncs.GetFeedsAsync;
import git.sureshcs50.tumblrclient.utils.BundleKeys;
import git.sureshcs50.tumblrclient.utils.Constants;
import git.sureshcs50.tumblrclient.utils.InfiniteScrollListener;
import git.sureshcs50.tumblrclient.utils.Utils;

public class DraftActivity extends BaseActivity {

    private GetFeedsAsync mGetDashboardFeedAsync;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mLvDrafts;
    private RelativeLayout mFooterLayout;
    private FeedsAdapter mAdapter;
    private List<Post> mPosts;
    private String mBlogName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draft);

        mPosts = new ArrayList<Post>();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mBlogName = extras.getString(BundleKeys.TAG_BLOG_NAME, "");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Drafts");
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.colorPrimaryDark);

        mLvDrafts = (ListView) findViewById(R.id.listDraft);
        mAdapter = new FeedsAdapter(this, mPosts);
        mLvDrafts.setAdapter(mAdapter);

        //setting up footer layout for ListView..
        LayoutInflater footerInflater = (LayoutInflater) getLayoutInflater();
        mFooterLayout = (RelativeLayout) footerInflater.inflate(R.layout.footer_progress_list_item, null);
        mFooterLayout.setVisibility(View.GONE);
        mLvDrafts.addFooterView(mFooterLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFeeds();
            }
        });

        // loads feeds for dashboard..
        loadFeeds();

        //listens to ListView scroll and loads upcoming records..
        mLvDrafts.setOnScrollListener(new InfiniteScrollListener(Constants.POST_PAGINATION_LIMIT) { //3 -> load next 3 data..
            @Override
            public void loadMore(int page, int totalItemsCount) {
                try {
                    if (Utils.hasConnection(DraftActivity.this)) {
                        loadFeeds();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mFooterLayout.setVisibility(View.GONE);
                        Toast.makeText(DraftActivity.this, getString(R.string.msg_load_feeds), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadFeeds() {
        if (Utils.hasConnection(this)) {
            mGetDashboardFeedAsync = new GetFeedsAsync(this, mBlogName, Constants.POST_PAGINATION_LIMIT, 0, Constants.FEED_STATUS_BLOG_DRAFT) {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    mFooterLayout.setVisibility(View.VISIBLE);
                }

                @Override
                protected void onPostExecute(List<Post> posts) {
                    super.onPostExecute(posts);
                    mFooterLayout.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    // inserts newly added post to top of the list.
                    if (posts != null) {
                        mAdapter.addItems(posts);
                    } else {
                        Toast.makeText(DraftActivity.this, getString(R.string.msg_failed_to_get_feeds), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            mGetDashboardFeedAsync.execute();
        }
    }
}
