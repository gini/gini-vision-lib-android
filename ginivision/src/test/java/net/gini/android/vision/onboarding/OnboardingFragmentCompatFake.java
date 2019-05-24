package net.gini.android.vision.onboarding;

import net.gini.android.vision.test.FragmentImplFactory;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class OnboardingFragmentCompatFake extends OnboardingFragmentCompat {

    public static FragmentImplFactory<OnboardingFragmentImpl, OnboardingFragmentCompat>
            sFragmentImplFactory;

    public OnboardingFragmentCompatFake() {
    }

    @Override
    OnboardingFragmentImpl createFragmentImpl() {
        return sFragmentImplFactory.createFragmentImpl(this);
    }
}
