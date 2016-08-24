package net.gini.android.vision.onboarding;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

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
        if (mFragment.getActivity() == null) {
            return null;
        }
        if (mPage.getImageResId() == 0) {
            return null;
        }
        return mFragment.getActivity().getResources().getDrawable(mPage.getImageResId());
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
