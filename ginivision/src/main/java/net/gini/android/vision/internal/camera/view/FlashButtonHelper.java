package net.gini.android.vision.internal.camera.view;

/**
 * Created by Alpar Szotyori on 18.02.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public final class FlashButtonHelper {

    /**
     * Internal use only.
     *
     * @suppress
     */
    public enum FlashButtonPosition {
        LEFT_OF_CAMERA_TRIGGER, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static FlashButtonPosition getFlashButtonPosition(final boolean documentImportEnabled,
            final boolean multiPageEnabled) {
        if (documentImportEnabled && !multiPageEnabled) {
            return FlashButtonPosition.BOTTOM_RIGHT;
        } else if (!documentImportEnabled) {
            return FlashButtonPosition.BOTTOM_LEFT;
        }
        return FlashButtonPosition.LEFT_OF_CAMERA_TRIGGER;
    }


    private FlashButtonHelper() {
    }
}
