package net.gini.android.vision.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

class OnboardingFragmentHelper {

    private static final String ARGS_PAGES = "GV_PAGES";

    static Bundle createArguments(@NonNull ArrayList<OnboardingPage> pages) {
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGS_PAGES, pages);
        return arguments;
    }

    static OnboardingFragmentImpl createFragmentImpl(@NonNull OnboardingFragmentImplCallback fragment, @Nullable Bundle arguments) {
        if (arguments != null) {
            ArrayList<OnboardingPage> pages = arguments.getParcelableArrayList(ARGS_PAGES);
            if (pages != null) {
                return new OnboardingFragmentImpl(fragment, pages);
            }
        }
        return new OnboardingFragmentImpl(fragment);
    }

    public static void setListener(@NonNull OnboardingFragmentImpl fragmentImpl, @NonNull Context context) {
        if (context instanceof OnboardingFragmentListener) {
            fragmentImpl.setListener((OnboardingFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement OnboardingFragmentListener.");
        }
    }
}
