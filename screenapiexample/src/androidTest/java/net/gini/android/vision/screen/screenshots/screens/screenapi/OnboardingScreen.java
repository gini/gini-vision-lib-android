package net.gini.android.vision.screen.screenshots.screens.screenapi;

import static net.gini.android.vision.screen.screenshots.Helper.isObjectAvailable;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import net.gini.android.vision.screen.screenshots.screens.Screen;

public class OnboardingScreen implements Screen {

    private static final String NEXT_BUTTON_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_button_next";
    private static final String IMAGE_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_image_onboarding";
    private static final String TEXT_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_text_message";

    private final UiDevice mUiDevice;

    public OnboardingScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() {
        boolean isNextButtonAvailable = isObjectAvailable(
                new UiSelector().resourceId(NEXT_BUTTON_RES_ID), mUiDevice);
        boolean isImageAvailable = isObjectAvailable(
                new UiSelector().resourceId(IMAGE_RES_ID), mUiDevice);
        boolean isTextAvailable = isObjectAvailable(
                new UiSelector().resourceId(TEXT_RES_ID), mUiDevice);
        return isNextButtonAvailable && isImageAvailable && isTextAvailable;
    }

    public void goToNextPage() throws UiObjectNotFoundException {
        UiObject button = mUiDevice.findObject(new UiSelector().resourceId(NEXT_BUTTON_RES_ID));
        button.click();
        mUiDevice.waitForIdle();
    }

    public void closeSelf() {
        mUiDevice.pressBack();
        mUiDevice.waitForIdle();
    }
}
