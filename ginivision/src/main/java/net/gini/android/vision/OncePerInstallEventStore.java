package net.gini.android.vision;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.VisibleForTesting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal use only.
 *
 * @suppress
 */
public class OncePerInstallEventStore {

    private static final Logger LOG = LoggerFactory.getLogger(OncePerInstallEventStore.class);

    private static final String ONCE_PER_INSTALL_EVENTS = "GV_ONCE_PER_INSTALL_EVENTS";

    private final SharedPreferences mSharedPreferences;

    public OncePerInstallEventStore(final Context context) {
        mSharedPreferences = context.getSharedPreferences(ONCE_PER_INSTALL_EVENTS,
                Context.MODE_PRIVATE);
    }

    public boolean containsEvent(final OncePerInstallEvent event) {
        return mSharedPreferences.contains(event.name());
    }

    public void saveEvent(final OncePerInstallEvent event) {
        mSharedPreferences.edit()
                .putBoolean(event.name(), true)
                .apply();
        LOG.debug("Saved event {}", event.name());
    }

    @VisibleForTesting
    void clearEvent(final OncePerInstallEvent event) {
        mSharedPreferences.edit()
                .remove(event.name())
                .apply();
        LOG.debug("Cleared event {}", event.name());
    }
}
