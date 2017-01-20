package net.gini.android.vision.test;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;

import android.os.Build;

public class PermissionsHelper {

    private static final long GRANT_PERMISSION_PAUSE_DURATION = 500;

    public static void grantExternalStoragePermission() throws InterruptedException {
        grantExternalStoragePermission(getTargetContext().getPackageName());
    }

    public static void grantExternalStoragePermission(final String packageName)
            throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + packageName
                            + " android.permission.READ_EXTERNAL_STORAGE");
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + packageName
                            + " android.permission.WRITE_EXTERNAL_STORAGE");
        }
        // A delay is needed for the permission
        Thread.sleep(GRANT_PERMISSION_PAUSE_DURATION);
    }

    public static void grantCameraPermission() throws InterruptedException {
        grantCameraPermission(getTargetContext().getPackageName());
    }

    public static void grantCameraPermission(final String packageName) throws InterruptedException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + packageName
                            + " android.permission.CAMERA");
        }
        // A delay is needed for the permission
        Thread.sleep(GRANT_PERMISSION_PAUSE_DURATION);
    }
}
