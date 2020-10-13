package net.gini.android.vision.tracking;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

import android.app.Activity;

/**
 * Events triggered on the review screen.
 *
 * <p> If you use the Screen API all events will be triggered automatically.
 *
 * <p> If you use the Component API some events will not be triggered (for ex. events which rely on {@link Activity#onBackPressed()}). You
 * need to check whether all the events you are interested in are triggered.
 */
public enum ReviewScreenEvent {
    /**
     * Triggers when the user presses back.(<b>Screen API only</b>)
     */
    BACK,
    /**
     * Triggers when the user presses the next button.(<b>Screen API + Component API</b>)
     */
    NEXT,
    /**
     * Triggers when upload failed.(<b>Screen API + Component API</b>)
     *
     * <p> Use the keys in {@link ReviewScreenEvent.UPLOAD_ERROR_DETAILS_MAP_KEY} to get details about the event from the details map.
     */
    UPLOAD_ERROR;

    /**
     * Keys to retrieve details about the {@link ReviewScreenEvent#UPLOAD_ERROR} event.
     */
    public static class UPLOAD_ERROR_DETAILS_MAP_KEY {

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
