package net.gini.android.vision.onboarding;

import android.os.Bundle;

class OnboardingPageFragmentHelper {

    private static final String ARGS_PAGE = "GV_PAGE";

    static Bundle createArguments(OnboardingPage page) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_PAGE, page);
        return arguments;
    }

    static OnboardingPageFragmentImpl createFragmentImpl(Bundle arguments) {
        if (arguments == null) {
            throw new RuntimeException("Missing OnboardingPage.");
        }

        OnboardingPage page = arguments.getParcelable(ARGS_PAGE);
        if (page == null) {
            throw new RuntimeException("Missing OnboardingPage.");
        }

        return new OnboardingPageFragmentImpl(page);
    }
}
