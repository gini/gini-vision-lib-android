package net.gini.android.vision.uiautomator.screens.screenapi;

import static net.gini.android.vision.uiautomator.Helper.isObjectAvailable;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import net.gini.android.vision.uiautomator.screens.Screen;

public class MainScreen implements Screen {

    private static final String START_SCANNER_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/button_start_scanner";

    private final UiDevice mUiDevice;

    public MainScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() {
        return isObjectAvailable(new UiSelector().resourceId(START_SCANNER_BUTTON_RES_ID),
                mUiDevice);
    }

    public void startGiniVisionLibrary() throws UiObjectNotFoundException {
        UiObject scannerButton = mUiDevice.findObject(
                new UiSelector().resourceId(START_SCANNER_BUTTON_RES_ID));
        scannerButton.clickAndWaitForNewWindow();
    }
}
