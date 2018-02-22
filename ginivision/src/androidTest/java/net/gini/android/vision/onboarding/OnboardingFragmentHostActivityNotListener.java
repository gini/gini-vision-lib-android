package net.gini.android.vision.onboarding;

import net.gini.android.vision.test.FragmentHostActivity;
import net.gini.android.vision.test.R;

import java.util.ArrayList;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class OnboardingFragmentHostActivityNotListener extends
        FragmentHostActivity<OnboardingFragmentCompat> {

    static OnboardingFragmentListener sListener;

    @Override
    protected void setListener() {
        if (sListener != null) {
            getFragment().setListener(sListener);
        }
    }

    @Override
    protected OnboardingFragmentCompat createFragment() {
        final ArrayList<OnboardingPage> pages = new ArrayList<>();
        pages.add(new OnboardingPage(R.string.gv_onboarding_flat,
                R.drawable.gv_onboarding_flat));
        return OnboardingFragmentCompat.createInstanceWithoutEmptyLastPage(pages);
    }

}
