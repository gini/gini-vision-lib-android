package net.gini.android.vision.internal.camera.view;

/**
 * Created by Alpar Szotyori on 18.02.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * @exclude
 */
public final class FlashButtonHelper {

    /**
     * @exclude
     */
    public enum FLASH_BUTTON_POSITION {
        LEFT_OF_CAMERA_TRIGGER, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static FLASH_BUTTON_POSITION getFlashButtonPosition(final boolean documentImportEnabled,
            final boolean multiPageEnabled) {
        if (documentImportEnabled && !multiPageEnabled) {
            return FLASH_BUTTON_POSITION.BOTTOM_RIGHT;
        } else if (!documentImportEnabled) {
            return FLASH_BUTTON_POSITION.BOTTOM_LEFT;
        }
        return FLASH_BUTTON_POSITION.LEFT_OF_CAMERA_TRIGGER;
    }


    private FlashButtonHelper() {
    }
}
