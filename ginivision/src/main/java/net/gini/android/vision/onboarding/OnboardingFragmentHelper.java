package net.gini.android.vision.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

class OnboardingFragmentHelper {

    private static final String ARGS_PAGES = "GV_PAGES";
    private static final String ARGS_SHOW_EMPTY_LAST_PAGE = "GV_SHOW_EMPTY_LAST_PAGE";

    static Bundle createArguments(@NonNull ArrayList<OnboardingPage> pages, boolean showEmptyLastPage) {
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGS_PAGES, pages);
        arguments.putBoolean(ARGS_SHOW_EMPTY_LAST_PAGE, showEmptyLastPage);
        return arguments;
    }

    static Bundle createArguments(boolean showEmptyLastPage) {
        Bundle arguments = new Bundle();
        arguments.putBoolean(ARGS_SHOW_EMPTY_LAST_PAGE, showEmptyLastPage);
        return arguments;
    }

    static OnboardingFragmentImpl createFragmentImpl(@NonNull OnboardingFragmentImplCallback fragment, @Nullable Bundle arguments) {
        if (arguments != null) {
            ArrayList<OnboardingPage> pages = arguments.getParcelableArrayList(ARGS_PAGES);
            boolean showEmptyLastPage = arguments.getBoolean(ARGS_SHOW_EMPTY_LAST_PAGE, true);
            if (pages != null) {
                return new OnboardingFragmentImpl(fragment, showEmptyLastPage, pages);
            } else {
                return new OnboardingFragmentImpl(fragment, showEmptyLastPage);
            }
        }
        return new OnboardingFragmentImpl(fragment, true);
    }

    public static void setListener(@NonNull OnboardingFragmentImpl fragmentImpl, @NonNull Context context) {
        if (context instanceof OnboardingFragmentListener) {
            fragmentImpl.setListener((OnboardingFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement OnboardingFragmentListener.");
        }
    }
}
