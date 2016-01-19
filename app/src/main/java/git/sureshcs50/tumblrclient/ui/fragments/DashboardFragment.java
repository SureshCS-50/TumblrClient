package git.sureshcs50.tumblrclient.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tumblr.jumblr.types.Post;

import java.util.ArrayList;
import java.util.List;

import git.sureshcs50.tumblrclient.R;
import git.sureshcs50.tumblrclient.adapters.FeedsAdapter;
import git.sureshcs50.tumblrclient.asyncs.GetFeedsAsync;
import git.sureshcs50.tumblrclient.ui.activities.DraftActivity;
import git.sureshcs50.tumblrclient.ui.activities.HomeActivity;
import git.sureshcs50.tumblrclient.utils.BundleKeys;
import git.sureshcs50.tumblrclient.utils.Constants;
import git.sureshcs50.tumblrclient.utils.InfiniteScrollListener;
import git.sureshcs50.tumblrclient.utils.Utils;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class DashboardFragment extends BaseFragment {

    private GetFeedsAsync mGetDashboardFeedAsync;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView mListViewFeeds;
    private RelativeLayout mFooterLayout;
    private FeedsAdapter mAdapter;
    private List<Post> mPosts;
    private String mBlogName = "";
    private int mOffset = 0;
    private int mFeedStatus = 0;
    private boolean isActionBarMenuNeeded = false;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String blogName, boolean isActionBarMenuNeeded, int feedStatus) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.TAG_BLOG_NAME, blogName);
        bundle.putBoolean(BundleKeys.TAG_IS_ACTIONBAR_MENU_NEEDED, isActionBarMenuNeeded);
        bundle.putInt(BundleKeys.TAG_FEED_STATUS, feedStatus);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle extras = getArguments();
            mBlogName = extras.getString(BundleKeys.TAG_BLOG_NAME, "");
            isActionBarMenuNeeded = extras.getBoolean(BundleKeys.TAG_IS_ACTIONBAR_MENU_NEEDED, false);
            mFeedStatus = extras.getInt(BundleKeys.TAG_FEED_STATUS, 0);
        }

        if (!mBlogName.trim().isEmpty() && !mBlogName.equalsIgnoreCase(getResources().getString(R.string.tag_dashboard))) {
            mBlogName = mBlogName + Constants.BASE_HOST_NAME;
        }

        mPosts = new ArrayList<Post>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.colorPrimaryDark);

        mListViewFeeds = (ListView) view.findViewById(R.id.listViewFeeds);
        mAdapter = new FeedsAdapter(getActivity(), mPosts);
        mListViewFeeds.setAdapter(mAdapter);

        //setting up footer layout for ListView..
        LayoutInflater footerInflater = (LayoutInflater) getActivity().getLayoutInflater();
        mFooterLayout = (RelativeLayout) footerInflater.inflate(R.layout.footer_progress_list_item, null);
        mFooterLayout.setVisibility(View.GONE);
        mListViewFeeds.addFooterView(mFooterLayout);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mOffset = 0;
                loadFeeds(mOffset, true, mFeedStatus);
            }
        });

        // loads feeds for dashboard..
        loadFeeds(mOffset, false, mFeedStatus);

        //listens to ListView scroll and loads upcoming records..
        mListViewFeeds.setOnScrollListener(new InfiniteScrollListener(Constants.POST_PAGINATION_LIMIT) { //2 -> load next 2 data..
            @Override
            public void loadMore(int page, int totalItemsCount) {
                try {
                    if (Utils.hasConnection(getActivity())) {
                        loadFeeds(mOffset, false, mFeedStatus);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        mFooterLayout.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), getString(R.string.msg_load_feeds), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // will be displayed only in blogs feed screen..
        setHasOptionsMenu(isActionBarMenuNeeded);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        MenuItem draftItem = menu.findItem(R.id.action_draft);
        draftItem.setVisible(true);
        draftItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent iDrafts = new Intent(getActivity(), DraftActivity.class);
                iDrafts.putExtra(BundleKeys.TAG_BLOG_NAME, mBlogName);
                startActivity(iDrafts);
                return true;
            }
        });
    }

    private void loadFeeds(int offset, final boolean isNewlyAddedPost, int feedStatus) {
        if (Utils.hasConnection(getActivity())) {
            mGetDashboardFeedAsync = new GetFeedsAsync(getActivity(), mBlogName, Constants.POST_PAGINATION_LIMIT, offset, feedStatus) {
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
                        if (!isNewlyAddedPost)
                            mAdapter.addItems(posts);
                        else
                            mAdapter.insertItem(posts);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_failed_to_get_feeds), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            mGetDashboardFeedAsync.execute();
            mOffset += Constants.POST_PAGINATION_LIMIT;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity())
                .setActionBarTitle(mBlogName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // loads new post..
        loadFeeds(0, true, mFeedStatus);
    }
}