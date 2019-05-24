package net.gini.android.vision.onboarding;

import net.gini.android.vision.test.FragmentHostActivity;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class OnboardingFragmentHostActivity extends
        FragmentHostActivity<OnboardingFragmentCompatFake> {

    @Override
    protected void setListener() {

    }

    @Override
    protected OnboardingFragmentCompatFake createFragment() {
        return new OnboardingFragmentCompatFake();
    }
}
