package net.gini.android.vision;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @exclude
 */
public class OncePerInstallEventStore {

    private static final String ONCE_PER_INSTALL_EVENTS = "GV_ONCE_PER_INSTALL_EVENTS";

    private final SharedPreferences mSharedPreferences;

    public OncePerInstallEventStore(Context context) {
        mSharedPreferences = context.getSharedPreferences(ONCE_PER_INSTALL_EVENTS, Context.MODE_PRIVATE);
    }

    public boolean containsEvent(OncePerInstallEvent event) {
        return mSharedPreferences.contains(event.name());
    }

    public void saveEvent(OncePerInstallEvent event) {
        mSharedPreferences.edit()
                .putBoolean(event.name(), true)
                .apply();
    }
}
