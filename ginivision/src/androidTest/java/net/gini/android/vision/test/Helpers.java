package net.gini.android.vision.test;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.view.View;

import net.gini.android.vision.Document;
import net.gini.android.vision.internal.camera.photo.Photo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Helpers {

    public static <T extends Parcelable, C extends Parcelable.Creator<T>> T doParcelingRoundTrip(
            T payload, C creator) {
        Parcel parcel = Parcel.obtain();
        payload.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        return creator.createFromParcel(parcel);
    }

    public static void prepareLooper() {
        if (Looper.myLooper() == null) {
            Looper.prepare();
        }
    }

    public static byte[] getTestJpeg() throws IOException {
        AssetManager assetManager = InstrumentationRegistry.getTargetContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("invoice.jpg");
            return inputStreamToByteArray(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static byte[] inputStreamToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes;
        //noinspection TryFinallyCanBeTryWithResources - only for minSdkVersion 19 and above
        try {
            byte[] buffer = new byte[8192];
            int readBytes;
            while ((readBytes = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readBytes);
            }
            bytes = outputStream.toByteArray();
        } finally {
            outputStream.close();
        }
        return bytes;
    }

    public static Document createDocument(byte[] jpeg, int orientation) {
        Photo photo = new Photo();
        photo.setJpeg(jpeg);
        photo.setRotationForDisplay(orientation);
        return Document.fromPhoto(photo);
    }

    public static void takeScreenshotForBitBar(String name, Activity activity)
            throws IOException {
        // Make sure there is a screenshots directory
        String screenshotsPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test-screenshots/";
        File screenshotsDir = new File(screenshotsPath);
        screenshotsDir.mkdir();

        // In Testdroid Cloud, taken screenshots are always stored
        // under /test-screenshots/ folder and this ensures those screenshots
        // be shown under Test Results
        String path =
                screenshotsPath + name +".png";

        View scrView = activity.getWindow().getDecorView().getRootView();
        scrView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(scrView.getDrawingCache());
        scrView.setDrawingCacheEnabled(false);

        OutputStream out = null;
        File imageFile = new File(path);

        out = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        out.flush();
    }
}
