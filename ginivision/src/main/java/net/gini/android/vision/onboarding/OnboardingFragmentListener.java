package net.gini.android.vision.onboarding;

import net.gini.android.vision.GiniVisionError;

public interface OnboardingFragmentListener {
    void onCloseOnboarding();
    void onError(GiniVisionError giniVisionError);
}
