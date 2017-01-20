package net.gini.android.vision.uiautomator.screens.screenapi;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.uiautomator.Helper.waitForObject;

import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import net.gini.android.vision.uiautomator.screens.Screen;

public class MainScreen implements Screen {

    private static final String START_SCANNER_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/button_start_scanner";

    private final UiDevice mUiDevice;

    public MainScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() throws InterruptedException {
        UiObject2 button = waitForObject(By.res(START_SCANNER_BUTTON_RES_ID), mUiDevice);
        return button != null;
    }

    public void startGiniVisionLibrary() throws InterruptedException {
        UiObject2 scannerButton = waitForObject(By.res(START_SCANNER_BUTTON_RES_ID), mUiDevice);
        assertThat(scannerButton).isNotNull();
        scannerButton.click();
    }
}
