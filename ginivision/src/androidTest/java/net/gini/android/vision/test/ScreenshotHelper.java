package net.gini.android.vision.test;

import static net.gini.android.vision.test.PermissionsHelper.grantExternalStoragePermission;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.test.uiautomator.UiDevice;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ScreenshotHelper {

    public static void takeEspressoScreenshot(File destination, Activity activity)
            throws IOException, InterruptedException {
        Bitmap bitmap = takeScreenshotFromActivity(activity);
        writeBitmapToFile(bitmap, destination);
    }

    private static Bitmap takeScreenshotFromActivity(final Activity activity)
            throws InterruptedException {
        grantExternalStoragePermission();
        View scrView = activity.getWindow().getDecorView().getRootView();
        scrView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(scrView.getDrawingCache());
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
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static void takeUIAutomatorScreenshot(File destination, UiDevice device)
            throws InterruptedException {
        grantExternalStoragePermission();
        device.takeScreenshot(destination);
    }

    public static File screenshotFileForBitBar(String name) {
        File screenshotsDir = getBitBarScreenshotsDir();
        String fileName = name + ".png";
        return new File(screenshotsDir, fileName);
    }

    private static File getBitBarScreenshotsDir() {
        String screenshotsDirPath = String.format("%s%s%s",
                Environment.getExternalStorageDirectory().getPath(), File.separator,
                "test-screenshots");
        File screenshotsDir = new File(screenshotsDirPath);
        screenshotsDir.mkdir();
        return screenshotsDir;
    }
}
