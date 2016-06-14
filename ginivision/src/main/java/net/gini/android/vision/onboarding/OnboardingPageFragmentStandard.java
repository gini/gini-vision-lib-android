package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class OnboardingPageFragmentStandard extends Fragment {

    private OnboardingPageFragmentImpl mFragmentImpl;

    public static OnboardingPageFragmentStandard createInstance(OnboardingPage page) {
        OnboardingPageFragmentStandard fragment = new OnboardingPageFragmentStandard();
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
