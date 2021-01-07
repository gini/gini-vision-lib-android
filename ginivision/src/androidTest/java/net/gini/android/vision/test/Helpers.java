package net.gini.android.vision.test;

import android.app.Instrumentation;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import net.gini.android.vision.Document;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.internal.util.ContextHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.uiautomator.UiDevice;

public class Helpers {

    public static <T extends Parcelable, C extends Parcelable.Creator<T>> T doParcelingRoundTrip(
            final T payload, final C creator) {
        final Parcel parcel = Parcel.obtain();
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
        return loadAsset("invoice.jpg");
    }

    public static byte[] loadAsset(final String filename) throws IOException {
        final AssetManager assetManager = ApplicationProvider.getApplicationContext().getAssets();
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

    private static byte[] inputStreamToByteArray(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes;
        try {
            final byte[] buffer = new byte[8192];
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

    public static Document createDocument(final byte[] jpeg, final int orientation,
            final String deviceOrientation,
            final String deviceType, final ImageDocument.Source source) {
        return DocumentFactory.newImageDocumentFromPhoto(
                PhotoFactory.newPhotoFromJpeg(jpeg, orientation, deviceOrientation, deviceType,
                        source));
    }

    public static boolean isTablet() {
        final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        return ContextHelper.isTablet(instrumentation.getTargetContext());
    }

    public static void resetDeviceOrientation() throws RemoteException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            final UiDevice uiDevice = UiDevice.getInstance(
                    InstrumentationRegistry.getInstrumentation());
            uiDevice.setOrientationNatural();
            waitForWindowUpdate(uiDevice);
            uiDevice.unfreezeRotation();
        }
    }

    public static void waitForWindowUpdate(@NonNull final UiDevice uiDevice) {
        uiDevice.waitForWindowUpdate(BuildConfig.APPLICATION_ID, 1000);
    }

    public static void copyAssetToStorage(@NonNull final String assetFilePath,
            @NonNull final String storageDirPath) throws IOException {
        final AssetManager assetManager = ApplicationProvider.getApplicationContext().getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = assetManager.open(assetFilePath);
            final File file = new File(storageDirPath,
                    Uri.parse(assetFilePath).getLastPathSegment());
            if (file.exists() || file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                copyFile(inputStream, outputStream);
            } else {
                throw new IOException("Could not create file: " + file.getAbsolutePath());
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
        final byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }

    public static void convertJpegToNV21(@NonNull final String inJpegFilename,
            @NonNull final String outNV21Filename) throws IOException {
        final byte[] jpeg = loadAsset(inJpegFilename);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length);
        final byte[] nv21 = toNV21(bitmap);
        saveToFile(outNV21Filename, nv21);
    }

    private static void saveToFile(@NonNull final String filename, @NonNull final byte[] data)
            throws IOException {
        final File file = new File(
                ApplicationProvider.getApplicationContext().getExternalFilesDir(null)
                        + File.separator +
                        filename);
        FileOutputStream fileOutputStream = null;
        try {
            if (!file.exists() && !file.createNewFile()) {
                throw new IOException("Could not create file: " + file.getAbsolutePath());
            }
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(data, 0, data.length);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    /**
     * Source: https://stackoverflow.com/questions/5960247/convert-bitmap-array-to-yuv-ycbcr-nv21
     */
    public static byte[] toNV21(final Bitmap bitmap) {

        final int[] argb = new int[bitmap.getWidth() * bitmap.getHeight()];

        bitmap.getPixels(argb, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        final byte[] yuv = new byte[bitmap.getWidth() * bitmap.getHeight() * 3 / 2];
        encodeYUV420SP(yuv, argb, bitmap.getWidth(), bitmap.getHeight());

        return yuv;
    }

    /**
     * Source: https://stackoverflow.com/questions/5960247/convert-bitmap-array-to-yuv-ycbcr-nv21
     */
    private static void encodeYUV420SP(final byte[] yuv420sp, final int[] argb, final int width,
            final int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0
                        && yuv420sp.length > uvIndex + 2) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }

                index++;
            }
        }
    }

    @NonNull
    public static Uri getAssetFileFileContentUri(@NonNull final String assetFilePath) throws IOException {
        final File fileProviderDir = createAndGetFileProviderDir();
        final File file = new File(fileProviderDir, assetFilePath);
        Helpers.copyAssetToStorage(assetFilePath, fileProviderDir.getPath());
        return FileProvider.getUriForFile(ApplicationProvider.getApplicationContext(),
                "net.gini.android.vision.test.fileprovider", file);
    }

    @NonNull
    public static File getAssetFileAsFileProviderFile(@NonNull final String assetFilePath) {
        return new File(Helpers.getFileProviderDir(), assetFilePath);
    }

    private static File createAndGetFileProviderDir() throws IOException {
        final File fileProviderDir = getFileProviderDir();
        if (!fileProviderDir.exists()) {
            //noinspection ResultOfMethodCallIgnored
            fileProviderDir.mkdirs();
        }
        return fileProviderDir;
    }

    public static void deleteAssetFileFromContentUri(@NonNull final String assetFilePath) throws IOException {
        final File file = new File(createAndGetFileProviderDir(), assetFilePath);
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    @NonNull
    private static File getFileProviderDir() {
        final Context context = ApplicationProvider.getApplicationContext();
        return new File(context.getFilesDir(), "file-provider");
    }
}
