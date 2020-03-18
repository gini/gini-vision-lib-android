package net.gini.android.vision.onboarding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.ContextHelper;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

class OnboardingPageFragmentImpl extends OnboardingPageContract.View {

    private final FragmentImplCallback mFragment;

    private View mBackground;
    private ImageView mImageOnboarding;
    private TextView mTextMessage;

    public OnboardingPageFragmentImpl(@NonNull final FragmentImplCallback fragment,
            @NonNull final OnboardingPage page) {
        mFragment = fragment;
        if (mFragment.getActivity() == null) {
            throw new IllegalStateException("Missing activity for fragment.");
        }
        initPresenter(mFragment.getActivity(), page);
    }

    private void initPresenter(@NonNull final Activity activity,
            @NonNull final OnboardingPage page) {
        createPresenter(activity);
        getPresenter().setPage(page);
    }

    private void createPresenter(@NonNull final Activity activity) {
        new OnboardingPagePresenter(activity, this);
    }

    @Override
    void showImage(final int imageResId, final boolean rotated) {
        mImageOnboarding.setImageDrawable(getImageDrawable(imageResId, rotated));
    }

    @Nullable
    private Drawable getImageDrawable(@DrawableRes final int imageResId, final boolean rotated) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return null;
        }
        if (imageResId == 0) {
            return null;
        }

        final Drawable drawable = ContextCompat.getDrawable(activity, imageResId);
        if (!ContextHelper.isPortraitOrientation(activity)
                && rotated) {
            final Drawable rotatedDrawable = createRotatedDrawableForLandscape(
                    imageResId);
            return rotatedDrawable != null ? rotatedDrawable : drawable;
        } else {
            return drawable;
        }
    }

    @Nullable
    private Drawable createRotatedDrawableForLandscape(@DrawableRes final int imageResId) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return null;
        }

        final Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(),
                imageResId);
        if (bitmap == null) {
            return null;
        }
        final Matrix matrix = new Matrix();
        matrix.postRotate(270f);
        final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(activity.getResources(), rotatedBitmap);
    }

    @Override
    void showText(final int textResId) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }

        mTextMessage.setText(activity.getText(textResId).toString());
    }

    @Override
    void showTransparentBackground() {
        mBackground.setBackgroundColor(Color.TRANSPARENT);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_onboarding_page, container, false);
        bindViews(view);
        getPresenter().start();
        return view;
    }

    private void bindViews(@NonNull final View view) {
        mImageOnboarding = (ImageView) view.findViewById(R.id.gv_image_onboarding);
        mTextMessage = (TextView) view.findViewById(R.id.gv_text_message);
        mBackground = view.findViewById(R.id.gv_background);
    }


}
