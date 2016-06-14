package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.ui.FragmentImplCallback;

import java.util.ArrayList;

public class OnboardingFragmentCompat extends Fragment implements FragmentImplCallback {

    private OnboardingFragmentImpl mFragmentImpl;

    public static OnboardingFragmentCompat createInstance() {
        return new OnboardingFragmentCompat();
    }

    public static OnboardingFragmentCompat createInstance(ArrayList<OnboardingPage> pages) {
        OnboardingFragmentCompat fragment = new OnboardingFragmentCompat();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(pages));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = OnboardingFragmentHelper.createFragmentImpl(this, getArguments());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }
}
