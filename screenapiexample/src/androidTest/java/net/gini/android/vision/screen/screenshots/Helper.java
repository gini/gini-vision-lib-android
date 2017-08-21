package net.gini.android.vision.screen.screenshots;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.content.Intent;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

public class Helper {

    public static final long APP_START_TIMEOUT = 10_000;

    public static void openApp(final String packageName, UiDevice uiDevice) {
        Context context = getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        uiDevice.wait(Until.hasObject(By.pkg(packageName).depth(0)),
                APP_START_TIMEOUT);
    }

    public static boolean isObjectAvailable(UiSelector selector, UiDevice uiDevice) {
        UiObject object = uiDevice.findObject(selector);
        return object.exists();
    }

}
