package net.gini.android.vision.onboarding;

import android.os.Bundle;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

import androidx.annotation.NonNull;

final class OnboardingPageFragmentHelper {

    private static final String ARGS_PAGE = "GV_PAGE";

    static Bundle createArguments(@NonNull final OnboardingPage page) {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_PAGE, page);
        return arguments;
    }

    static OnboardingPageFragmentImpl createFragmentImpl(
            @NonNull final FragmentImplCallback fragment, @NonNull final Bundle arguments) {
        final OnboardingPage page = arguments.getParcelable(ARGS_PAGE);
        if (page == null) {
            throw new IllegalStateException("Missing OnboardingPage.");
        }
        return new OnboardingPageFragmentImpl(fragment, page);
    }

    private OnboardingPageFragmentHelper() {
    }
}
