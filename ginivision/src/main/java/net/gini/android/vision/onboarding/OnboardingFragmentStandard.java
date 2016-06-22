package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.ui.FragmentImplCallback;

import java.util.ArrayList;

public class OnboardingFragmentStandard extends Fragment implements FragmentImplCallback {

    private OnboardingFragmentImpl mFragmentImpl;

    public static OnboardingFragmentStandard createInstance() {
        return new OnboardingFragmentStandard();
    }

    public static OnboardingFragmentStandard createInstance(ArrayList<OnboardingPage> pages) {
        OnboardingFragmentStandard fragment = new OnboardingFragmentStandard();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(pages));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = OnboardingFragmentHelper.createFragmentImpl(this, getArguments());
        OnboardingFragmentHelper.setListener(mFragmentImpl, getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }
}
