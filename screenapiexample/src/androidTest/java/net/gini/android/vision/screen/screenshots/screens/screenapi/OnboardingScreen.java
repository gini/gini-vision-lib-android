package net.gini.android.vision.screen.screenshots.screens.screenapi;

import static net.gini.android.vision.screen.screenshots.Helper.isObjectAvailable;

import net.gini.android.vision.screen.screenshots.screens.Screen;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

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
        final boolean isNextButtonAvailable = isObjectAvailable(
                new UiSelector().resourceId(NEXT_BUTTON_RES_ID), mUiDevice);
        final boolean isImageAvailable = isObjectAvailable(
                new UiSelector().resourceId(IMAGE_RES_ID), mUiDevice);
        final boolean isTextAvailable = isObjectAvailable(
                new UiSelector().resourceId(TEXT_RES_ID), mUiDevice);
        return isNextButtonAvailable && isImageAvailable && isTextAvailable;
    }

    public void goToNextPage() throws UiObjectNotFoundException {
        final UiObject button = mUiDevice.findObject(new UiSelector().resourceId(NEXT_BUTTON_RES_ID));
        button.click();
        mUiDevice.waitForIdle();
    }

    public void closeSelf() {
        mUiDevice.pressBack();
        mUiDevice.waitForIdle();
    }
}
