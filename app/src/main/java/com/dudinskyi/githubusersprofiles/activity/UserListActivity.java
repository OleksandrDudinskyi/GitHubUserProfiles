package com.dudinskyi.githubusersprofiles.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dudinskyi.githubuserprofiles.R;
import com.dudinskyi.githubusersprofiles.adapter.GitHubUserListAdapter;
import com.dudinskyi.githubusersprofiles.model.GitHubUserProfile;
import com.dudinskyi.githubusersprofiles.network.GitHubRequest;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.spicelist.simple.BitmapSpiceManager;

/**
 * Main activity for showing github users information
 *
 * @author Oleksandr Dudinskyi (dudinskyj@gmail.com)
 */
public class UserListActivity extends Activity {
    private ListView mGithubUserListView;
    private ProgressBar mProgressBar;
    private TextView mLoadingText;
    private GitHubUserListAdapter mGitHubUserListAdapter;
    private ImageView mExpandedImageView;
    private FrameLayout mContainerLayout;
    private SpiceManager spiceManagerJson = new SpiceManager(JacksonSpringAndroidSpiceService.class);
    private BitmapSpiceManager spiceManagerBinary = new BitmapSpiceManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(false);
        setContentView(R.layout.github_user_list);
        mExpandedImageView = (ImageView) findViewById(R.id.expanded_image);
        mContainerLayout = (FrameLayout) findViewById(R.id.container);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mGithubUserListView = (ListView) findViewById(R.id.github_user_list_view);
        mLoadingText = (TextView) findViewById(R.id.loading_text);
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManagerJson.start(this);
        spiceManagerBinary.start(this);
        loadGithubUsersList();
    }

    @Override
    public void onStop() {
        spiceManagerJson.shouldStop();
        spiceManagerBinary.shouldStop();
        super.onStop();
    }

    private void updateListViewContent(GitHubUserProfile[] users) {
        mGitHubUserListAdapter = new GitHubUserListAdapter(this, spiceManagerBinary, users, mExpandedImageView, mContainerLayout);
        mGithubUserListView.setAdapter(mGitHubUserListAdapter);
        mGithubUserListView.setFriction(ViewConfiguration.getScrollFriction() * 10);
        mProgressBar.setVisibility(View.GONE);
        mLoadingText.setVisibility(View.GONE);
        mGithubUserListView.setVisibility(View.VISIBLE);
    }

    private void loadGithubUsersList() {
        setProgressBarIndeterminateVisibility(true);
        spiceManagerJson.execute(new GitHubRequest(), null, DurationInMillis.ONE_SECOND * 10,
                new GitHubUserListListener());
    }

    private class GitHubUserListListener implements RequestListener<GitHubUserProfile[]> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            setProgressBarIndeterminateVisibility(false);
            Toast.makeText(UserListActivity.this, "Impossible to get the list of users", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(GitHubUserProfile[] result) {
            setProgressBarIndeterminateVisibility(false);
            updateListViewContent(result);
        }
    }

    @Override
    public void onBackPressed() {
        if (mExpandedImageView != null && mExpandedImageView.getVisibility() == View.VISIBLE) {
            mExpandedImageView.performClick();
        } else {
            super.onBackPressed();
        }
    }
}
