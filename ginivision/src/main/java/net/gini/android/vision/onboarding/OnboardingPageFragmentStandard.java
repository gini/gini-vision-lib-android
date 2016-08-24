package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

/**
 * @exclude
 */
public class OnboardingPageFragmentStandard extends Fragment implements FragmentImplCallback {

    private OnboardingPageFragmentImpl mFragmentImpl;

    public static OnboardingPageFragmentStandard createInstance(@NonNull OnboardingPage page) {
        OnboardingPageFragmentStandard fragment = new OnboardingPageFragmentStandard();
        fragment.setArguments(OnboardingPageFragmentHelper.createArguments(page));
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
