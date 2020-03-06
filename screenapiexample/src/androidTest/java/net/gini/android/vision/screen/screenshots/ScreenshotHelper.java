package net.gini.android.vision.screen.screenshots;

import static net.gini.android.vision.screen.testhelper.PermissionsHelper.grantExternalStoragePermission;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.test.uiautomator.UiDevice;

public class ScreenshotHelper {

    public static void takeEspressoScreenshot(final File destination, final Activity activity)
            throws IOException, InterruptedException {
        final Bitmap bitmap = takeScreenshotFromActivity(activity);
        writeBitmapToFile(bitmap, destination);
    }

    private static Bitmap takeScreenshotFromActivity(final Activity activity)
            throws InterruptedException {
        grantExternalStoragePermission();
        final View scrView = activity.getWindow().getDecorView().getRootView();
        scrView.setDrawingCacheEnabled(true);
        final Bitmap bitmap = Bitmap.createBitmap(scrView.getDrawingCache());
        scrView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    private static void writeBitmapToFile(final Bitmap bitmap, final File destination)
            throws IOException {
        OutputStream out = null;
        try {
            out = new FileOutputStream(destination);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    public static void takeUIAutomatorScreenshot(final File destination, final UiDevice device)
            throws InterruptedException {
        grantExternalStoragePermission();
        device.takeScreenshot(destination);
    }

    public static File screenshotFileForBitBar(final String name) {
        final File screenshotsDir = getBitBarScreenshotsDir();
        final String fileName = name + ".png";
        return new File(screenshotsDir, fileName);
    }

    private static File getBitBarScreenshotsDir() {
        final String screenshotsDirPath = String.format("%s%s%s",
                Environment.getExternalStorageDirectory().getPath(), File.separator,
                "test-screenshots");
        final File screenshotsDir = new File(screenshotsDirPath);
        screenshotsDir.mkdir();
        return screenshotsDir;
    }
}
