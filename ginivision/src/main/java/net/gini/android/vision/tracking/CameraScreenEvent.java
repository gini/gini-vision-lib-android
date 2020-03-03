package net.gini.android.vision.tracking;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

import android.app.Activity;

/**
 * Events triggered on the camera screen.
 *
 * <p> If you use the Screen API all events will be triggered automatically.
 *
 * <p> If you use the Component API some events will not be triggered (for ex. events which rely on {@link Activity#onBackPressed()}). You
 * need to check whether all the events you are interested in are triggered.
 */
public enum CameraScreenEvent {
    /**
     * Triggers when the user presses back.(<b>Screen API only</b>)
     */
    EXIT,
    /**
     * Triggers when the user opens the help screen.(<b>Screen API only</b>)
     */
    HELP,
    /**
     * Triggers when the user takes a picture.(<b>Screen API + Component API</b>)
     */
    TAKE_PICTURE
}
