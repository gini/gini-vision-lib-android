package net.gini.android.vision.tracking;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

import android.app.Activity;

/**
 * Events triggered on the analysis screen.
 *
 * <p> If you use the Screen API all events will be triggered automatically.
 *
 * <p> If you use the Component API some events will not be triggered (for ex. events which rely on {@link Activity#onBackPressed()}). You
 *  need to check whether all the events you are interested in are triggered.
 */
public enum AnalysisScreenEvent {
    /**
     * Triggers when the user presses back.(<b>Screen API only</b>)
     */
    CANCEL,
    /**
     * Triggers when analysis failed.(<b>Screen API + Component API</b>)
     *
     * <p> Use the keys in {@link ERROR_DETAILS_MAP_KEY} to get details about the event from the details map.
     */
    ERROR,
    /**
     * Triggers when the user retries analysis after it failed.(<b>Screen API + Component API</b>)
     */
    RETRY;

    /**
     * Keys to retrieve details about the {@link AnalysisScreenEvent#ERROR} event.
     */
    public static final class ERROR_DETAILS_MAP_KEY {

        /**
         * Error message key in the details map. Value type is {@link String}.
         */
        public static String MESSAGE = "MESSAGE";

        /**
         * Error object key in the details map. Value type is {@link Throwable}.
         */
        public static String ERROR_OBJECT = "ERROR_OBJECT";
    }
}
