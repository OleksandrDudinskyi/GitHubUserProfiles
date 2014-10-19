package com.dudinskyi.githubusersprofiles.view;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudinskyi.githubuserprofiles.R;
import com.dudinskyi.githubusersprofiles.model.GitHubUserProfile;
import com.octo.android.robospice.spicelist.SpiceListItemView;

/**
 * Github User List item view
 *
 * @author Oleksandr Dudinskyi (dudinskyj@gmail.com)
 */
public class GitHubUserListItemView extends RelativeLayout implements SpiceListItemView<GitHubUserProfile> {
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private TextView mUserLogin;
    private TextView mProfileUrl;
    private ImageView mUserAvatar;
    private GitHubUserProfile mGitHubUserProfile;
    private ImageView mExpandedImageView;
    private FrameLayout mContainerLayout;


    public GitHubUserListItemView(Context context, ImageView expandedImageView,
                                  FrameLayout containerLayout) {
        super(context);
        inflateView(context);
        mExpandedImageView = expandedImageView;
        mContainerLayout = containerLayout;
    }


    private void inflateView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.list_view_item, this);
        mUserLogin = (TextView) this.findViewById(R.id.user_login);
        mProfileUrl = (TextView) this.findViewById(R.id.profile_url);
        mProfileUrl.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(((TextView) v).getText().toString()));
                getContext().startActivity(i);
            }
        });
        mUserAvatar = (ImageView) this.findViewById(R.id.user_avatar);
        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);
    }


    @Override
    public void update(GitHubUserProfile gitHubUserProfile) {
        mGitHubUserProfile = gitHubUserProfile;
        mUserLogin.setText(gitHubUserProfile.getLogin());
        SpannableString content = new SpannableString(
                String.valueOf(gitHubUserProfile.getHtml_url()));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mProfileUrl.setText(content);
    }


    @Override
    public GitHubUserProfile getData() {
        return mGitHubUserProfile;
    }


    @Override
    public ImageView getImageView(int imageIndex) {
        return mUserAvatar;
    }


    @Override
    public int getImageViewCount() {
        return 100;
    }

}
