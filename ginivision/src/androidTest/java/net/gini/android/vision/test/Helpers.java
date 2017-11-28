package net.gini.android.vision.test;

import android.app.Instrumentation;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import net.gini.android.vision.Document;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.internal.util.ContextHelper;

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
        return getTestJpeg("invoice.jpg");
    }

    public static byte[] getTestJpeg(String filename) throws IOException {
        AssetManager assetManager = InstrumentationRegistry.getTargetContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(filename);
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

    public static Document createDocument(byte[] jpeg, int orientation, String deviceOrientation,
            String deviceType, String source) {
        return DocumentFactory.newDocumentFromPhoto(
                PhotoFactory.newPhotoFromJpeg(jpeg, orientation, deviceOrientation, deviceType, source));
    }

    public static boolean isTablet() {
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        return ContextHelper.isTablet(instrumentation.getTargetContext());
    }

    public static void resetDeviceOrientation() throws RemoteException {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
            uiDevice.setOrientationNatural();
            waitForWindowUpdate(uiDevice);
            uiDevice.unfreezeRotation();
        }
    }

    public static void waitForWindowUpdate(@NonNull final UiDevice uiDevice) {
        uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 5000);
    }

    public static void copyAssetToStorage(@NonNull final String assetFilePath,
            @NonNull final String storageDirPath) throws IOException {
        AssetManager assetManager = InstrumentationRegistry.getTargetContext().getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = assetManager.open(assetFilePath);
            final File file = new File(storageDirPath,
                    Uri.parse(assetFilePath).getLastPathSegment());
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                copyFile(inputStream, outputStream);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private static void copyFile(final InputStream inputStream, final OutputStream outputStream)
            throws IOException {
        byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }
}
