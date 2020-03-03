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
    NEXT
}
