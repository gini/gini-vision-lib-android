package net.gini.android.vision.screen.screenshots;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.content.Intent;

import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

public class Helper {

    public static final long APP_START_TIMEOUT = 10_000;

    public static void openApp(final String packageName, final UiDevice uiDevice) {
        final Context context = getInstrumentation().getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)),
                APP_START_TIMEOUT);
    }

    public static boolean isObjectAvailable(final UiSelector selector, final UiDevice uiDevice) {
        final UiObject object = uiDevice.findObject(selector);
        return object.exists();
    }

}
