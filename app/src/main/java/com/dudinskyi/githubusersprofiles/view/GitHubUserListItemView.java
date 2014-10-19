package com.dudinskyi.githubusersprofiles.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dudinskyi.githubuserprofiles.R;
import com.dudinskyi.githubusersprofiles.adapter.GitHubUserListAdapter;
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
        mUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mExpandedImageView != null && mExpandedImageView.getVisibility() == View.GONE) {
                    zoomImageFromThumb(mUserAvatar);
                }
            }
        });
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
    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(getContext().getCacheDir() + "/" +
                GitHubUserListAdapter.USER_LOGO_FILE_PREFIX + getData().getLogin(), options);
        try {
            mExpandedImageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        thumbView.getGlobalVisibleRect(startBounds);
        mContainerLayout.getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final bounds using the
        // "center crop" technique. This prevents undesirable stretching during the animation.
        // Also calculate the start scaling factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        thumbView.setAlpha(0f);
        mExpandedImageView.setVisibility(View.VISIBLE);

        mExpandedImageView.setPivotX(0f);
        mExpandedImageView.setPivotY(0f);

        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(mExpandedImageView, View.X, startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.Y, startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        final float startScaleFinal = startScale;
        mExpandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator.ofFloat(mExpandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator.ofFloat(mExpandedImageView, View.Y, startBounds.top))
                        .with(ObjectAnimator.ofFloat(mExpandedImageView, View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator.ofFloat(mExpandedImageView, View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        mExpandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        mExpandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
