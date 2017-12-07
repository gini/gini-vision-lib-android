package net.gini.android.vision.screen.screenshots.screens.screenapi;

import static net.gini.android.vision.screen.screenshots.Helper.isObjectAvailable;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiSelector;

import net.gini.android.vision.screen.screenshots.screens.Screen;

public class AnalysisScreen implements Screen {

    private static final String IMAGE_RES_ID =
            "net.gini.android.vision.screenapiexample:id/gv_image_picture";

    private final UiDevice mUiDevice;

    public AnalysisScreen(final UiDevice uiDevice) {
        mUiDevice = uiDevice;
    }

    @Override
    public boolean isVisible() {
        return isObjectAvailable(
                new UiSelector().resourceId(IMAGE_RES_ID), mUiDevice);
    }

}
