package com.dudinskyi.githubusersprofiles.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.dudinskyi.githubusersprofiles.model.GitHubUserProfile;
import com.dudinskyi.githubusersprofiles.view.GitHubUserListItemView;
import com.octo.android.robospice.request.simple.BitmapRequest;
import com.octo.android.robospice.spicelist.SpiceListItemView;
import com.octo.android.robospice.spicelist.simple.BitmapSpiceManager;
import com.octo.android.robospice.spicelist.simple.SpiceArrayAdapter;

import java.io.File;

/**
 * Adapter for git hub user
 *
 * @author Oleksandr Dudinskyi (dudinskyj@gmail.com)
 */
public class GitHubUserListAdapter extends SpiceArrayAdapter<GitHubUserProfile> {
    public static final String USER_LOGO_FILE_PREFIX = "GITHUB_USER_LOGO";
    private ImageView mExpandedImageView;
    private FrameLayout mContainerLayout;

    public GitHubUserListAdapter(Context context, BitmapSpiceManager spiceManagerBitmap,
                                 GitHubUserProfile[] users, ImageView expandedImageView,
                                 FrameLayout containerLayout) {
        super(context, spiceManagerBitmap, users);
        mExpandedImageView = expandedImageView;
        mContainerLayout = containerLayout;
    }

    @Override
    public BitmapRequest createRequest(GitHubUserProfile gitHubUserProfile, int imageIndex,
                                       int requestImageWidth, int requestImageHeight) {
        File file = new File(getContext().getCacheDir(), USER_LOGO_FILE_PREFIX + gitHubUserProfile.getLogin());
        return new BitmapRequest(gitHubUserProfile.getAvatar_url(), requestImageWidth,
                requestImageHeight, file);
    }

    @Override
    public SpiceListItemView<GitHubUserProfile> createView(Context context, ViewGroup parent) {
        return new GitHubUserListItemView(getContext(), mExpandedImageView, mContainerLayout);
    }
}
