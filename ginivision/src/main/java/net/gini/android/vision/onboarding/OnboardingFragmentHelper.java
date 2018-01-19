package net.gini.android.vision.onboarding;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

final class OnboardingFragmentHelper {

    private static final String ARGS_PAGES = "GV_PAGES";
    private static final String ARGS_SHOW_EMPTY_LAST_PAGE = "GV_SHOW_EMPTY_LAST_PAGE";

    static Bundle createArguments(@NonNull final ArrayList<OnboardingPage> pages, // NOPMD - Bundle
            final boolean showEmptyLastPage) {
        final Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(ARGS_PAGES, pages);
        arguments.putBoolean(ARGS_SHOW_EMPTY_LAST_PAGE, showEmptyLastPage);
        return arguments;
    }

    static Bundle createArguments(final boolean showEmptyLastPage) {
        final Bundle arguments = new Bundle();
        arguments.putBoolean(ARGS_SHOW_EMPTY_LAST_PAGE, showEmptyLastPage);
        return arguments;
    }

    static OnboardingFragmentImpl createFragmentImpl(
            @NonNull final OnboardingFragmentImplCallback fragment,
            @Nullable final Bundle arguments) {
        if (arguments != null) {
            final ArrayList<OnboardingPage> pages = arguments.getParcelableArrayList(ARGS_PAGES);
            final boolean showEmptyLastPage = arguments.getBoolean(ARGS_SHOW_EMPTY_LAST_PAGE, true);
            if (pages != null) {
                return new OnboardingFragmentImpl(fragment, showEmptyLastPage, pages);
            } else {
                return new OnboardingFragmentImpl(fragment, showEmptyLastPage);
            }
        }
        return new OnboardingFragmentImpl(fragment, true);
    }

    public static void setListener(@NonNull final OnboardingFragmentImpl fragmentImpl,
            @NonNull final Context context) {
        if (context instanceof OnboardingFragmentListener) {
            fragmentImpl.setListener((OnboardingFragmentListener) context);
        } else {
            throw new IllegalStateException(
                    "Hosting activity must implement OnboardingFragmentListener.");
        }
    }

    private OnboardingFragmentHelper() {
    }
}
