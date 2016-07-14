package net.gini.android.vision.component;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public final class Util {

    public static byte[] readAsset(Context context, String fileName) {
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        byte[] bytes = null;
        try {
            inputStream = context.getAssets().open(fileName);
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int count;
            while ((count = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            // Ignore
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        return bytes;
    }

    private Util() {
    }
}
