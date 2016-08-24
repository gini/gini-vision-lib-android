package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

class OnboardingPageFragmentHelper {

    private static final String ARGS_PAGE = "GV_PAGE";

    static Bundle createArguments(@NonNull OnboardingPage page) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_PAGE, page);
        return arguments;
    }

    static OnboardingPageFragmentImpl createFragmentImpl(@NonNull FragmentImplCallback fragment, @NonNull Bundle arguments) {
        OnboardingPage page = arguments.getParcelable(ARGS_PAGE);
        if (page == null) {
            throw new IllegalStateException("Missing OnboardingPage.");
        }
        return new OnboardingPageFragmentImpl(fragment, page);
    }
}
