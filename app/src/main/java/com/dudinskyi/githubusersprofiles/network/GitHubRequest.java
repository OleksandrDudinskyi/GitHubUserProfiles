package com.dudinskyi.githubusersprofiles.network;

import com.dudinskyi.githubusersprofiles.model.GitHubUserProfile;
import com.octo.android.robospice.request.springandroid.SpringAndroidSpiceRequest;

import java.net.URI;

/**
 * Request for github user data
 *
 * @author Oleksandr Dudinskyi (dudinskyj@gmail.com)
 */
public class GitHubRequest extends SpringAndroidSpiceRequest<GitHubUserProfile[]> {
    private static final String GIT_HUB_USER_URL = "https://api.github.com/users";

    public GitHubRequest() {
        super(GitHubUserProfile[].class);
    }

    @Override
    public GitHubUserProfile[] loadDataFromNetwork() throws Exception {
        return getRestTemplate().getForObject(new URI(GIT_HUB_USER_URL), GitHubUserProfile[].class);
    }
}
