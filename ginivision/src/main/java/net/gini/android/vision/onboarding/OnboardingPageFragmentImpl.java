package net.gini.android.vision.onboarding;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.ContextHelper;

class OnboardingPageFragmentImpl {

    private final FragmentImplCallback mFragment;
    private final OnboardingPage mPage;

    private View mBackground;
    private ImageView mImageOnboarding;
    private TextView mTextMessage;

    public OnboardingPageFragmentImpl(@NonNull FragmentImplCallback fragment, @NonNull OnboardingPage page) {
        mFragment = fragment;
        mPage = page;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_onboarding_page, container, false);
        bindViews(view);
        setUpViews();
        return view;
    }

    private void bindViews(@NonNull View view) {
        mImageOnboarding = (ImageView) view.findViewById(R.id.gv_image_onboarding);
        mTextMessage = (TextView) view.findViewById(R.id.gv_text_message);
        mBackground = view.findViewById(R.id.gv_background);
    }

    private void setUpViews() {
        mImageOnboarding.setImageDrawable(getImageDrawable());
        mTextMessage.setText(getText());
        setUpBackground();
    }

    private void setUpBackground() {
        if (mPage.isTransparent()) {
            mBackground.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Nullable
    private Drawable getImageDrawable() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return null;
        }
        if (mPage.getImageResId() == 0) {
            return null;
        }
        final Drawable drawable = ContextCompat.getDrawable(activity, mPage.getImageResId());
        if (!ContextHelper.isPortraitOrientation(activity)
                && mPage.shouldRotateImageForLandscape()) {
            return createRotatedDrawableForLandscape(activity, drawable);
        } else {
            return drawable;
        }
    }

    private Drawable createRotatedDrawableForLandscape(final Activity activity,
            final Drawable drawable) {
        final Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), mPage.getImageResId());
        if (bitmap == null) {
            return drawable;
        }
        final Matrix matrix = new Matrix();
        matrix.postRotate(270f);
        final Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return new BitmapDrawable(activity.getResources(), rotatedBitmap);
    }

    @Nullable
    private CharSequence getText() {
        if (mFragment.getActivity() == null) {
            return null;
        }
        if (mPage.getTextResId() == 0) {
            return null;
        }
        return mFragment.getActivity().getText(mPage.getTextResId());
    }

}
