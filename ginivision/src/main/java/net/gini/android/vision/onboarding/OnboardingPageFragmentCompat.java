package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OnboardingPageFragmentCompat extends Fragment {

    private OnboardingPageFragmentImpl mFragmentImpl;

    public static OnboardingPageFragmentCompat createInstance(OnboardingPage page) {
        OnboardingPageFragmentCompat fragment = new OnboardingPageFragmentCompat();
        fragment.setArguments(OnboardingPageFragmentHelper.createArguments(page));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = OnboardingPageFragmentHelper.createFragmentImpl(getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }
}
