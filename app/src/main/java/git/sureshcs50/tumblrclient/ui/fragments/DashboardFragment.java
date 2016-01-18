package git.sureshcs50.tumblrclient.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import git.sureshcs50.tumblrclient.asyncs.GetDashboardFeedAsync;
import git.sureshcs50.tumblrclient.utils.BundleKeys;
import git.sureshcs50.tumblrclient.utils.Constants;
import git.sureshcs50.tumblrclient.utils.InfiniteScrollListener;
import git.sureshcs50.tumblrclient.utils.Utils;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class DashboardFragment extends BaseFragment {

    private GetDashboardFeedAsync mGetDashboardFeedAsync;
    private ListView mListViewFeeds;
    private RelativeLayout mFooterLayout;
    private FeedsAdapter mAdapter;
    private List<Post> mPosts;
    private String mBlogName = "";
    private int mOffset = 0;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String blogName) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BundleKeys.TAG_BLOG_NAME, blogName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle extras = getArguments();
            mBlogName = extras.getString(BundleKeys.TAG_BLOG_NAME, "");
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
        mListViewFeeds = (ListView) view.findViewById(R.id.listViewFeeds);
        mAdapter = new FeedsAdapter(getActivity(), mPosts);
        mListViewFeeds.setAdapter(mAdapter);

        // loads feeds for dashboard..
        loadFeeds(mOffset, false);

        //setting up footer layout for ListView..
        LayoutInflater footerInflater = (LayoutInflater) getActivity().getLayoutInflater();
        mFooterLayout = (RelativeLayout) footerInflater.inflate(R.layout.footer_progress_list_item, null);
        mFooterLayout.setVisibility(View.GONE);
        mListViewFeeds.addFooterView(mFooterLayout);

        //listens to ListView scroll and loads upcoming records..
        mListViewFeeds.setOnScrollListener(new InfiniteScrollListener(Constants.POST_PAGINATION_LIMIT) { //3 -> load next 3 data..
            @Override
            public void loadMore(int page, int totalItemsCount) {
                try {
                    if (Utils.hasConnection(getActivity())) {
                        loadFeeds(mOffset, false);
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

        return view;
    }

    private void loadFeeds(int offset, final boolean isNewlyAddedPost) {
        if (Utils.hasConnection(getActivity())) {
            mGetDashboardFeedAsync = new GetDashboardFeedAsync(getActivity(), mBlogName, Constants.POST_PAGINATION_LIMIT, offset) {
                @Override
                protected void onPostExecute(List<Post> posts) {
                    super.onPostExecute(posts);

                    // inserts newly added post to top of the list.
                    if (posts != null) {
                        if(!isNewlyAddedPost)
                            mAdapter.addItems(posts);
                        else
                            mAdapter.insertItem(posts.get(0));
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_failed_to_get_feeds), Toast.LENGTH_SHORT).show();
                    }
                }
            };
            mGetDashboardFeedAsync.execute();
        }
        mOffset += Constants.POST_PAGINATION_LIMIT;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // loads new post..
        loadFeeds(0, true);
    }
}