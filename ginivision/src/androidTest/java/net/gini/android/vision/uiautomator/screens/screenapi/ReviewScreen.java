package net.gini.android.vision.uiautomator.screens.screenapi;

import static net.gini.android.vision.uiautomator.Helper.waitForObject;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import net.gini.android.vision.uiautomator.screens.Screen;

public class ReviewScreen implements Screen {

    private static final String ROTATE_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_rotate";
    private final UiDevice mUiDevice;

    public ReviewScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() throws InterruptedException {
        UiObject2 rotateButton = waitForObject(By.res(ROTATE_BUTTON_RES_ID), mUiDevice);
        return rotateButton != null;
    }
}
