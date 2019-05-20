package net.gini.android.vision.onboarding;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

class OnboardingPageFragmentImpl extends OnboardingPageContract.View {

    private final FragmentImplCallback mFragment;

    private View mBackground;
    private ImageView mImageOnboarding;
    private TextView mTextMessage;

    public OnboardingPageFragmentImpl(@NonNull final FragmentImplCallback fragment,
            @NonNull final OnboardingPage page) {
        mFragment = fragment;
        createPresenter(page);
    }

    private void createPresenter(@NonNull final OnboardingPage page) {
        new OnboardingPagePresenter(mFragment.getActivity().getApplication(), this);
        getPresenter().setPage(page);
    }

    @Override
    void showImage(@NonNull final Drawable image) {
        mImageOnboarding.setImageDrawable(image);
    }

    @Override
    void showText(@NonNull final String text) {
        mTextMessage.setText(text);
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
