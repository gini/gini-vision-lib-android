package net.gini.android.vision.onboarding;

import android.content.Context;
import android.os.Bundle;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
            @NonNull final Context context, @Nullable final OnboardingFragmentListener listener) {
        if (context instanceof OnboardingFragmentListener) {
            fragmentImpl.setListener((OnboardingFragmentListener) context);
        } else if (listener != null) {
            fragmentImpl.setListener(listener);
        } else {
            throw new IllegalStateException(
                    "OnboardingFragmentListener not set. "
                            + "You can set it with OnboardingFragment[Compat,Standard]#setListener() or "
                            + "by making the host activity implement the OnboardingFragmentListener.");
        }
    }

    private OnboardingFragmentHelper() {
    }
}
