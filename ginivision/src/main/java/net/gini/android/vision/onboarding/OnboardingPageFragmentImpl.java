package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.R;

class OnboardingPageFragmentImpl {

    private final OnboardingPage mAssets;

    public OnboardingPageFragmentImpl(@NonNull OnboardingPage assets) {
        mAssets = assets;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gv_fragment_onboarding_page, container, false);
    }

}
