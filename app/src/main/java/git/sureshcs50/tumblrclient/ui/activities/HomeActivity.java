package git.sureshcs50.tumblrclient.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tumblr.jumblr.types.Blog;
import com.tumblr.jumblr.types.User;

import java.util.ArrayList;

import git.sureshcs50.tumblrclient.R;
import git.sureshcs50.tumblrclient.asyncs.GetUserInfoAsync;
import git.sureshcs50.tumblrclient.ui.fragments.DashboardFragment;
import git.sureshcs50.tumblrclient.utils.Utils;

public class HomeActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int NAV_BLOG_SUB_MENU_GROUP_ID = 1;
    public static final int NAV_SETTINGS_SUB_MENU_GROUP_ID = 2;

    private Toolbar mToolbar;
    private FloatingActionButton mFabAddPost;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavView;
    private Menu mNavigationMenu;

    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransition;
    private String mCurrentFragmentTag = "";

    private ArrayList<Blog> mBlogs;
    private TextView mTxtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mFragmentManager = getSupportFragmentManager();
        mFragmentTransition = mFragmentManager.beginTransaction();
        mBlogs = new ArrayList<Blog>();

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mFabAddPost = (FloatingActionButton) findViewById(R.id.fab);
        mFabAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mTxtTitle = (TextView) mNavView.findViewById(R.id.txtTitle);
        mNavView.setNavigationItemSelectedListener(this);
        mNavigationMenu = mNavView.getMenu();


        if (Utils.hasConnection(this)) {
            // get user info from jumblr client..
            new GetUserInfoAsync() {
                @Override
                protected void onPostExecute(User user) {
                    super.onPostExecute(user);
                    mTxtTitle.setText(user.getName());
                    mBlogs.addAll(user.getBlogs());

                    // adding a section and items into it..
                    SubMenu subMenu = mNavigationMenu.addSubMenu(NAV_BLOG_SUB_MENU_GROUP_ID, 0,
                            Menu.NONE, getString(R.string.tag_blogs));
                    for (int i = 0; i < mBlogs.size(); i++) {
                        subMenu.add(0, i, Menu.NONE, mBlogs.get(i).getName());
                    }

                    updateNavigationMenus();
                }
            }.execute();
        }

        // adding a section and items into it..
        SubMenu subMenu = mNavigationMenu.addSubMenu(NAV_SETTINGS_SUB_MENU_GROUP_ID, 0,
                Menu.NONE, getString(R.string.tag_settings));
        subMenu.add(0, 1, Menu.NONE, getString(R.string.tag_log_out)).setIcon(getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));

        // loads first fragment..
        mCurrentFragmentTag = DashboardFragment.class.getSimpleName();
        loadFragment(DashboardFragment.newInstance(getString(R.string.tag_dashboard)));
    }

    private void updateNavigationMenus() {
        for (int i = 0, count = mNavView.getChildCount(); i < count; i++) {
            final View child = mNavView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        String title = item.getTitle().toString();
        if (title.equalsIgnoreCase(getString(R.string.tag_log_out))) {
            Utils.clearAuthTokeCache(HomeActivity.this);
            Intent iSplash = new Intent(HomeActivity.this, SplashActivity.class);
            startActivity(iSplash);
            finish();
        } else if (title.equalsIgnoreCase(getString(R.string.tag_dashboard))) {
            // if current fragment is same as clicked item's fragment it won't load..
            if(!mCurrentFragmentTag.equalsIgnoreCase(DashboardFragment.class.getSimpleName())) {
                mCurrentFragmentTag = DashboardFragment.class.getSimpleName();
                loadFragment(DashboardFragment.newInstance(getString(R.string.tag_dashboard)));
            }
        } else {
            boolean isBlogClicked = false;
            for(int i = 0; i < mBlogs.size(); i++){
                if(mBlogs.get(i).getName().equalsIgnoreCase(title)){
                    isBlogClicked = true;
                    break;
                }
            }
            if((!mCurrentFragmentTag.equalsIgnoreCase(title)) && isBlogClicked){
                mCurrentFragmentTag = title;
                loadFragment(DashboardFragment.newInstance(mCurrentFragmentTag));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFragment(Fragment fragment){
        mFragmentTransition = mFragmentManager.beginTransaction();
        mFragmentTransition.replace(R.id.container, fragment, mCurrentFragmentTag)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}