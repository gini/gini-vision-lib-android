package net.gini.android.vision.onboarding;

import net.gini.android.vision.GiniVisionError;

import androidx.annotation.NonNull;

/**
 * <p>
 * Interface used by {@link OnboardingFragmentStandard} and {@link OnboardingFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface OnboardingFragmentListener {
    /**
     * <p>
     *     Called when the user has left the last page - either by swiping or tapping on the Next button - and you should
     *     close the Onboarding Fragment.
     * </p>
     */
    void onCloseOnboarding();

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(@NonNull GiniVisionError error);
}
