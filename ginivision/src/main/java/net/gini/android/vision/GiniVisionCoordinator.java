package net.gini.android.vision;

import android.app.Activity;
import android.content.Context;

import net.gini.android.vision.camera.CameraFragmentCompat;
import net.gini.android.vision.camera.CameraFragmentStandard;

/**
 * <p>
 *     The {@code GiniVisionCoordinator} facilitates the default behaviour for the Gini Vision Library.
 * </p>
 * <p>
 *     You can ignore this class when using the Screen API.
 * </p>
 * <p>
 *     If you use the Component API we recommend relying on this class to provide the default behaviour of the Gini Vision Library by calling the required methods at pre-defined points in your code and by implementing the {@link GiniVisionCoordinator.Listener}.
 * </p>
 */
public class GiniVisionCoordinator {

    /**
     * <p>
     *     Interface for the {@link GiniVisionCoordinator} to dispatch events.
     * </p>
     * <p>
     *     If you use the {@link GiniVisionCoordinator} you should implement this interface in your Activity to facilitate the default behaviour of the Gini Vision Library.
     * </p>
     */
    public interface Listener {
        /**
         * <p>
         *     Called when the onboarding should be shown.
         * </p>
         * <p>
         *     Is used to show the Onboarding Screen once per installation the first time the Camera Screen is started.
         * </p>
         */
        void onShowOnboarding();
    }

    private static final Listener NO_OP_LISTENER = new Listener() {
        @Override
        public void onShowOnboarding() {
        }
    };

    private Listener mListener = NO_OP_LISTENER;
    private final OncePerInstallEventStore mOncePerInstallEventStore;
    private boolean mShowOnboardingAtFirstRun = true;

    /**
     * <p>
     *     Factory method to create and configure a {@link GiniVisionCoordinator} instance.
     * </p>
     * @param context a {@link Context} used by the new instance to provide the default behaviour
     * @return a new instance of {@link GiniVisionCoordinator}
     */
    public static GiniVisionCoordinator createInstance(Context context) {
        return new GiniVisionCoordinator(new OncePerInstallEventStore(context));
    }

    GiniVisionCoordinator(OncePerInstallEventStore oncePerInstallEventStore) {
        mOncePerInstallEventStore = oncePerInstallEventStore;
    }

    /**
     * <p>
     *     Listener for handling events from the {@link GiniVisionCoordinator} to provide the default behaviour.
     * </p>
     * @param listener your implementation of the {@link GiniVisionCoordinator.Listener}
     * @return the {@link GiniVisionCoordinator} instance for a fluid api
     */
    public GiniVisionCoordinator setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    /**
     * <p>
     *     Enable or disable showing the Onboarding Screen once per installation the first time the Camera Screen is started.
     * </p>
     * <p>
     *     Default value is {@code true}.
     * </p>
     * @param showOnboardingAtFirstRun if {@code true} the Onboarding Screen is shown the first time the Camera Screen is started
     * @return the {@link GiniVisionCoordinator} instance for a fluid api
     */
    public GiniVisionCoordinator setShowOnboardingAtFirstRun(boolean showOnboardingAtFirstRun) {
        mShowOnboardingAtFirstRun = showOnboardingAtFirstRun;
        return this;
    }

    /**
     * <p>
     *     Call this method when the {@link CameraFragmentStandard} or {@link CameraFragmentCompat} has started.
     * </p>
     * <p>
     *     Can be called in your Acitivity's {@link Activity#onStart()} method, which hosts the Camera Fragment.
     * </p>
     */
    public void onCameraStarted() {
        if (mShowOnboardingAtFirstRun && !mOncePerInstallEventStore.containsEvent(OncePerInstallEvent.SHOW_ONBOARDING)) {
            mListener.onShowOnboarding();
            mOncePerInstallEventStore.saveEvent(OncePerInstallEvent.SHOW_ONBOARDING);
        }
    }
}
