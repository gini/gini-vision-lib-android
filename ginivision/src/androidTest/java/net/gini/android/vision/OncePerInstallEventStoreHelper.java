package net.gini.android.vision;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.content.Context;

public final class OncePerInstallEventStoreHelper {

    public static void clearOnboardingWasShownPreference() {
        final Context applicationContext = getApplicationContext();
        final OncePerInstallEventStore store = new OncePerInstallEventStore(applicationContext);
        store.clearEvent(OncePerInstallEvent.SHOW_ONBOARDING);
    }

    public static void setOnboardingWasShownPreference() {
        final Context applicationContext = getApplicationContext();
        final OncePerInstallEventStore store = new OncePerInstallEventStore(applicationContext);
        store.saveEvent(OncePerInstallEvent.SHOW_ONBOARDING);
    }

    private OncePerInstallEventStoreHelper() {
    }
}
