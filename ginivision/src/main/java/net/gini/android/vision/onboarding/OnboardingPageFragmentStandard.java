package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.ui.FragmentImplCallback;

/**
 * TODO: candidate for removal
 *
 * @exclude
 */
public class OnboardingPageFragmentStandard extends Fragment implements FragmentImplCallback {

    private OnboardingPageFragmentImpl mFragmentImpl;

    public static OnboardingPageFragmentStandard createInstance(OnboardingPage page, boolean noBackground) {
        OnboardingPageFragmentStandard fragment = new OnboardingPageFragmentStandard();
        fragment.setArguments(OnboardingPageFragmentHelper.createArguments(page, noBackground));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = OnboardingPageFragmentHelper.createFragmentImpl(this, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }
}
