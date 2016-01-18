package git.sureshcs50.tumblrclient.ui.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import git.sureshcs50.tumblrclient.R;
import git.sureshcs50.tumblrclient.ui.fragments.OAuthDialogFragment;
import git.sureshcs50.tumblrclient.utils.Utils;

public class SplashActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private final int SPLASH_TIMEOUT = 1000;

    private Button mBtnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mBtnLogin = (Button) findViewById(R.id.btnLogin);

        if (getClient() == null) {
            mBtnLogin.setVisibility(View.VISIBLE);
        } else {
            mBtnLogin.setVisibility(View.GONE);
        }

        //thread for splash screen running
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_TIMEOUT);
                } catch (InterruptedException e) {
                    Log.e("Exception", e.getMessage());
                } finally {
                    // for logged in user.. getClient() will return JumblrClient..
                    if (getClient() != null) {
                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

        if (Utils.hasConnection(this))
            logoTimer.start();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                if (Utils.hasConnection(this)) {
                    tumblrLogin();
                } else {
                    Toast.makeText(this, getString(R.string.msg_connection_failed), Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void tumblrLogin() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        // Create and show the dialog.
        OAuthDialogFragment newFragment = new OAuthDialogFragment();
        newFragment.show(ft, "loginDialog");
    }
}