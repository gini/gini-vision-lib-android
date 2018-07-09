package net.gini.android.vision;

import android.app.Activity;
import android.content.Context;

import net.gini.android.vision.camera.CameraFragmentCompat;
import net.gini.android.vision.camera.CameraFragmentStandard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *     The {@code GiniVisionCoordinator} facilitates the default behavior for the Gini Vision Library.
 * </p>
 * <p>
 *     You can ignore this class when using the Screen API.
 * </p>
 * <p>
 *     If you use the Component API we recommend relying on this class to provide the default behavior of the Gini Vision Library. This can be achieved by calling the required methods at pre-defined points in your code and by implementing the {@link GiniVisionCoordinator.Listener}.
 * </p>
 */
public class GiniVisionCoordinator {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionCoordinator.class);

    public static boolean shouldShowGiniVisionNoResultsScreen(final Document document) {
        return document.getType() == Document.Type.IMAGE
                || document.getType() == Document.Type.IMAGE_MULTI_PAGE;
    }

    /**
     * <p>
     *     Interface for the {@link GiniVisionCoordinator} to dispatch events.
     * </p>
     * <p>
     *     If you use the {@link GiniVisionCoordinator} you should implement this interface in your Activity to facilitate the default behavior of the Gini Vision Library.
     * </p>
     */
    public interface Listener {
        /**
         * <p>
         *     Called when onboarding should be shown.
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
     * @param context a {@link Context} used by the new instance to provide the default behavior
     * @return a new instance of {@link GiniVisionCoordinator}
     */
    public static GiniVisionCoordinator createInstance(final Context context) {
        return new GiniVisionCoordinator(new OncePerInstallEventStore(context));
    }

    GiniVisionCoordinator(final OncePerInstallEventStore oncePerInstallEventStore) {
        mOncePerInstallEventStore = oncePerInstallEventStore;
    }

    /**
     * <p>
     *     Listener for handling events from the {@link GiniVisionCoordinator} to provide the default behavior.
     * </p>
     * @param listener your implementation of the {@link GiniVisionCoordinator.Listener}
     * @return the {@link GiniVisionCoordinator} instance for a fluid api
     */
    public GiniVisionCoordinator setListener(final Listener listener) {
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
    public GiniVisionCoordinator setShowOnboardingAtFirstRun(
            final boolean showOnboardingAtFirstRun) {
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
        if (mShowOnboardingAtFirstRun && !mOncePerInstallEventStore.containsEvent(
                OncePerInstallEvent.SHOW_ONBOARDING)) {
            LOG.debug("Show onboarding at first run");
            mListener.onShowOnboarding();
            mOncePerInstallEventStore.saveEvent(OncePerInstallEvent.SHOW_ONBOARDING);
        } else {
            logNotShowingOnboarding();
        }
    }

    private void logNotShowingOnboarding() {
        if (!mShowOnboardingAtFirstRun) {
            LOG.debug("Show onboarding at first run was disabled");
        } else if (mOncePerInstallEventStore.containsEvent(OncePerInstallEvent.SHOW_ONBOARDING)) {
            LOG.debug("Already shown onboarding at first run");
        }
    }
}
