package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.ui.FragmentImplCallback;

class OnboardingPageFragmentHelper {

    private static final String ARGS_PAGE = "GV_PAGE";
    private static final String ARGS_NO_BACKGROUND = "GV_NO_BACKGROUND";

    static Bundle createArguments(@NonNull OnboardingPage page, boolean noBackground) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_PAGE, page);
        arguments.putBoolean(ARGS_NO_BACKGROUND, noBackground);
        return arguments;
    }

    static OnboardingPageFragmentImpl createFragmentImpl(@NonNull FragmentImplCallback fragment, @NonNull Bundle arguments) {
        OnboardingPage page = arguments.getParcelable(ARGS_PAGE);
        if (page == null) {
            throw new IllegalStateException("Missing OnboardingPage.");
        }

        boolean noBackground = arguments.getBoolean(ARGS_NO_BACKGROUND, false);

        return new OnboardingPageFragmentImpl(fragment, page, noBackground);
    }
}
