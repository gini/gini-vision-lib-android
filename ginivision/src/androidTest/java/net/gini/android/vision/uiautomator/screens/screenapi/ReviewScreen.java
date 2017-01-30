package net.gini.android.vision.uiautomator.screens.screenapi;

import static net.gini.android.vision.uiautomator.Helper.isObjectAvailable;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;

import net.gini.android.vision.uiautomator.screens.Screen;

public class ReviewScreen implements Screen {

    private static final String ROTATE_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_rotate";
    private final UiDevice mUiDevice;

    public ReviewScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() {
        return isObjectAvailable(new UiSelector().resourceId(ROTATE_BUTTON_RES_ID), mUiDevice);
    }
}
