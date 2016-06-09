package net.gini.android.vision.onboarding;

import android.os.Bundle;

import java.util.ArrayList;

class OnboardingFragmentHelper {

    private static final String ARGS_PAGES = "GV_PAGES";

    static Bundle createArguments(ArrayList<OnboardingPage> pages) {
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGS_PAGES, pages);
        return arguments;
    }

    static OnboardingFragmentImpl createFragmentImpl(Bundle arguments) {
        if (arguments != null) {
            ArrayList<OnboardingPage> pages = arguments.getParcelable(ARGS_PAGES);
            if (pages != null) {
                return new OnboardingFragmentImpl(pages);
            }
        }
        return new OnboardingFragmentImpl();
    }
}
