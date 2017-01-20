package net.gini.android.vision.uiautomator.screens.screenapi;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.uiautomator.Helper.waitForObject;

import android.os.Build;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

import net.gini.android.vision.uiautomator.screens.Screen;

public class CameraScreen implements Screen {

    private static final String NEXT_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_next";
    private static final String TRIGGER_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_camera_trigger";
    private static final String OPEN_APP_SETTINGS_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_camera_no_permission";

    private final UiDevice mUiDevice;

    public CameraScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() throws InterruptedException {
        UiObject2 nextButton = waitForObject(By.res(NEXT_BUTTON_RES_ID), mUiDevice);
        UiObject2 triggerButton = waitForObject(By.res(TRIGGER_BUTTON_RES_ID), mUiDevice);
        return nextButton != null ||
                triggerButton != null;
    }

    public void triggerCamera() throws InterruptedException {
        UiObject2 triggerButton = waitForObject(By.res(TRIGGER_BUTTON_RES_ID), mUiDevice);
        assertThat(triggerButton).isNotNull();
        triggerButton.click();
    }

    public void dismissOnboarding() throws InterruptedException {
        mUiDevice.pressBack();
        assertThat(isOnboardingVisible()).isFalse();
    }

    public boolean isOnboardingVisible() throws InterruptedException {
        UiObject2 nextButton = waitForObject(By.res(NEXT_BUTTON_RES_ID), mUiDevice);
        return nextButton != null;
    }
}
