package net.gini.android.vision;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

public final class OncePerInstallEventStoreHelper {

    public static void clearOnboardingWasShownPreference() {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        OncePerInstallEventStore store = new OncePerInstallEventStore(targetContext);
        store.clearEvent(OncePerInstallEvent.SHOW_ONBOARDING);
    }

    public static void setOnboardingWasShownPreference() {
        Context targetContext = InstrumentationRegistry.getTargetContext();
        OncePerInstallEventStore store = new OncePerInstallEventStore(targetContext);
        store.saveEvent(OncePerInstallEvent.SHOW_ONBOARDING);
    }

    private OncePerInstallEventStoreHelper() {
    }
}
