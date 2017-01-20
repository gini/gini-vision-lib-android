package net.gini.android.vision.uiautomator;

import static android.support.test.InstrumentationRegistry.getInstrumentation;

import android.content.Context;
import android.content.Intent;
import android.support.test.uiautomator.BySelector;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject2;

public class Helper {

    private static final long FIND_OBJECT_TIMEOUT = 10_000;
    private static final long FIND_OBJECT_DELAY = 1_000;

    public static void openApp(final String packageName) {
        Context context = getInstrumentation().getContext();
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static UiObject2 waitForObject(BySelector selector, UiDevice device)
            throws InterruptedException {
        UiObject2 object = null;
        long startTime = System.currentTimeMillis();
        while (object == null) {
            object = device.findObject(selector);
            Thread.sleep(FIND_OBJECT_DELAY);
            if (System.currentTimeMillis() - startTime > FIND_OBJECT_TIMEOUT) {
                return null;
            }
        }
        return object;
    }

}
