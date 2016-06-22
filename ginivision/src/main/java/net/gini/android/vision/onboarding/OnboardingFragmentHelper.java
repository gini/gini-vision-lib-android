package net.gini.android.vision.onboarding;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.ui.FragmentImplCallback;

import java.util.ArrayList;

class OnboardingFragmentHelper {

    private static final String ARGS_PAGES = "GV_PAGES";

    static Bundle createArguments(ArrayList<OnboardingPage> pages) {
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGS_PAGES, pages);
        return arguments;
    }

    static OnboardingFragmentImpl createFragmentImpl(FragmentImplCallback fragment, Bundle arguments) {
        if (arguments != null) {
            ArrayList<OnboardingPage> pages = arguments.getParcelableArrayList(ARGS_PAGES);
            if (pages != null) {
                return new OnboardingFragmentImpl(fragment, pages);
            }
        }
        return new OnboardingFragmentImpl(fragment);
    }

    public static void setListener(OnboardingFragmentImpl fragmentImpl, Context context) {
        if (context instanceof OnboardingFragmentListener) {
            fragmentImpl.setListener((OnboardingFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement OnboardingFragmentListener.");
        }
    }
}
