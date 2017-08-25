package net.gini.android.vision.screen.screenshots.screens.screenapi;

import static net.gini.android.vision.screen.screenshots.Helper.isObjectAvailable;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import net.gini.android.vision.screen.screenshots.screens.Screen;

public class ReviewScreen implements Screen {

    private static final String ROTATE_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_rotate";
    private static final String NEXT_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_next";

    private final UiDevice mUiDevice;

    public ReviewScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() {
        return isObjectAvailable(new UiSelector().resourceId(ROTATE_BUTTON_RES_ID), mUiDevice);
    }

    public void proceedToAnalysis() throws UiObjectNotFoundException {
        UiObject button = mUiDevice.findObject(new UiSelector().resourceId(NEXT_BUTTON_RES_ID));
        button.clickAndWaitForNewWindow();
    }
}
