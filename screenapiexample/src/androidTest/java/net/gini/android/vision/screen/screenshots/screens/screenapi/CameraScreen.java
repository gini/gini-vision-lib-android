package net.gini.android.vision.screen.screenshots.screens.screenapi;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.screen.screenshots.Helper.isObjectAvailable;

import net.gini.android.vision.screen.screenshots.screens.Screen;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

public class CameraScreen implements Screen {

    private static final String NEXT_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_next";
    private static final String TRIGGER_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_camera_trigger";
    private static final String ONBOARDING_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_action_show_onboarding";

    private final UiDevice mUiDevice;

    public CameraScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() {
        final boolean isNextButtonAvailable = isObjectAvailable(
                new UiSelector().resourceId(NEXT_BUTTON_RES_ID), mUiDevice);
        final boolean isTriggerButtonAvailable = isObjectAvailable(
                new UiSelector().resourceId(TRIGGER_BUTTON_RES_ID), mUiDevice);
        return isNextButtonAvailable || isTriggerButtonAvailable;
    }

    public void triggerCamera() throws UiObjectNotFoundException {
        final UiObject triggerButton = mUiDevice.findObject(
                new UiSelector().resourceId(TRIGGER_BUTTON_RES_ID));
        triggerButton.clickAndWaitForNewWindow();
    }

    public void dismissOnboarding() throws InterruptedException {
        mUiDevice.pressBack();
        assertThat(isOnboardingVisible()).named("Onboarding is visible").isFalse();
    }

    public boolean isOnboardingVisible() throws InterruptedException {
        return isObjectAvailable(new UiSelector().resourceId(NEXT_BUTTON_RES_ID), mUiDevice);
    }

    public void showOnboarding() throws UiObjectNotFoundException {
        final UiObject button = mUiDevice.findObject(
                new UiSelector().resourceId(ONBOARDING_BUTTON_RES_ID));
        button.clickAndWaitForNewWindow();
    }
}
