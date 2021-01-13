package net.gini.android.vision.test;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.UiAutomation;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import androidx.annotation.NonNull;

public class PermissionsHelper {

    private static final String LOG_TAG = "GrantPermission";

    public static void grantExternalStoragePermission() throws InterruptedException {
        grantExternalStoragePermission(getApplicationContext().getPackageName());
    }

    public static void grantExternalStoragePermission(final String packageName)
            throws InterruptedException {
        grantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, packageName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            grantPermission(Manifest.permission.READ_EXTERNAL_STORAGE, packageName);
        }
    }

    public static void grantCameraPermission() throws InterruptedException {
        grantCameraPermission(getApplicationContext().getPackageName());
    }

    public static void grantCameraPermission(final String packageName) throws InterruptedException {
        grantPermission(Manifest.permission.CAMERA, packageName);
    }

    private static void grantPermission(final String permission, final String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasPermission(permission, packageName)) {
                return;
            }
            final String command = "pm grant " + packageName + " " + permission;
            executeShellCommand(command);
        }
    }

    public static void revokeCameraPermission() throws InterruptedException {
        revokePermission(Manifest.permission.CAMERA, getApplicationContext().getPackageName());
    }

    private static void revokePermission(final String permission, final String packageName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasPermission(permission, packageName)) {
                return;
            }
            final String command = "pm revoke " + packageName + " " + permission;
            executeShellCommand(command);
        }
    }

    private static boolean hasPermission(final String permission, final String packageName) {
        final int checkResult = getApplicationContext().getPackageManager().checkPermission(
                permission,
                packageName);
        return checkResult == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Executes a command on the shell using {@link UiAutomation#executeShellCommand(String)}. It
     * blocks until the standard out was read.
     *
     * @param command command to execute on the shell
     * @return command's standard out
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    private static String executeShellCommand(final String command) {
        final UiAutomation uiAutomation = getInstrumentation().getUiAutomation();
        try (final ParcelFileDescriptor parcelFd = uiAutomation.executeShellCommand(command);
             final FileReader fileReader = new FileReader(parcelFd.getFileDescriptor());
             final BufferedReader reader = new BufferedReader(fileReader)) {
            final StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            return stringBuilder.toString();
        } catch (final IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return "";
    }

}
