package git.sureshcs50.tumblrclient.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import git.sureshcs50.tumblrclient.R;
import git.sureshcs50.tumblrclient.ui.activities.HomeActivity;
import git.sureshcs50.tumblrclient.utils.Constants;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * Created by sureshkumar-pc on 18/01/2016.
 */
public class OAuthDialogFragment extends DialogFragment {

    // Preferences..
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    // OAuth Provider, Consumer..
    private CommonsHttpOAuthConsumer mConsumer;
    private CommonsHttpOAuthProvider mProvider;

    private WebView mWebViewOauth;
    private String mAuthURL = "";

    public OAuthDialogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);

        mSharedPreferences = getActivity().getSharedPreferences(Constants.TUMBLR_CLIENT_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();

        // to avoid OAuthCommunicationException..
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_oauth_dialog, container, false);

        mWebViewOauth = (WebView) view.findViewById(R.id.web_oauth);

        mConsumer = new CommonsHttpOAuthConsumer(Constants.CONSUMER_KEY, Constants.CONSUMER_SECRET);
        mProvider = new CommonsHttpOAuthProvider(Constants.REQUEST_URL, Constants.ACCESS_URL, Constants.AUTHORIZE_URL);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... strings) {
                return setAuthURL();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                //load the url of the oAuth login page
                mWebViewOauth.loadUrl(result);
                //set the web client
                mWebViewOauth.setWebViewClient(new MyWebViewClient());
                //activates JavaScript (just in case)
                WebSettings webSettings = mWebViewOauth.getSettings();
                webSettings.setJavaScriptEnabled(true);
            }
        }.execute();

        return view;
    }

    private String setAuthURL() {
        try {
            mAuthURL = mProvider.retrieveRequestToken(mConsumer, Constants.OAUTH_CALLBACK_URL);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
        Log.d("Auth Url ", mAuthURL);
        return mAuthURL;
    }

    // show oAuth login in WebView instead of opening a browser for better user experience.
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //check if the login was successful and the access token returned
            if (url.startsWith(Constants.OAUTH_CALLBACK_URL)) {
                Uri uri = Uri.parse(url);
                String token = uri.getQueryParameter("oauth_token");
                String verifier = uri.getQueryParameter("oauth_verifier");
                try {
                    mProvider.retrieveAccessToken(mConsumer, verifier);
                    mEditor.putString(Constants.PREFS_ACCESS_TOKEN, mConsumer.getToken());
                    mEditor.putString(Constants.PREFS_TOKEN_SECRET, mConsumer.getTokenSecret());
                    mEditor.commit();
                    OAuthDialogFragment.this.dismiss();

                    // login successful and start home activity.
                    Intent iHome = new Intent(getActivity(), HomeActivity.class);
                    startActivity(iHome);
                    getActivity().finish();
                } catch (Exception e) {
                    Log.e("Error ", "Error occurred while retrieving access token");
                    e.printStackTrace();
                }
                return true;
            }
            return false;
        }
    }
}
