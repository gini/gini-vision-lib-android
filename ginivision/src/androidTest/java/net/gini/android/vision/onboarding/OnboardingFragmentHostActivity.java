package net.gini.android.vision.onboarding;

import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionError;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class OnboardingFragmentHostActivity extends
        OnboardingFragmentHostActivityNotListener implements OnboardingFragmentListener {

    private boolean isClosed;

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void onCloseOnboarding() {
        isClosed = true;
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {

    }
}
