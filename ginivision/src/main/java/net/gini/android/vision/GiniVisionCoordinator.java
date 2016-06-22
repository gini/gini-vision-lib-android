package net.gini.android.vision;

import android.content.Context;

public class GiniVisionCoordinator {

    public interface Listener {
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

    public static GiniVisionCoordinator createInstance(Context context) {
        return new GiniVisionCoordinator(new OncePerInstallEventStore(context));
    }

    GiniVisionCoordinator(OncePerInstallEventStore oncePerInstallEventStore) {
        mOncePerInstallEventStore = oncePerInstallEventStore;
    }

    public GiniVisionCoordinator setListener(Listener listener) {
        mListener = listener;
        return this;
    }

    public GiniVisionCoordinator setShowOnboardingAtFirstRun(boolean showOnboardingAtFirstRun) {
        mShowOnboardingAtFirstRun = showOnboardingAtFirstRun;
        return this;
    }

    public void onScannerStarted() {
        if (mShowOnboardingAtFirstRun && !mOncePerInstallEventStore.containsEvent(OncePerInstallEvent.SHOW_ONBOARDING)) {
            mListener.onShowOnboarding();
            mOncePerInstallEventStore.saveEvent(OncePerInstallEvent.SHOW_ONBOARDING);
        }
    }
}
