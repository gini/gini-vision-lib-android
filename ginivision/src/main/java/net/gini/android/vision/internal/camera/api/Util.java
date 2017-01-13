package net.gini.android.vision.internal.camera.api;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.camera.photo.Size;

import java.util.List;

/**
 * @exclude 
 */
public final class Util {

    @Nullable
    public static Size getLargestSize(@NonNull List<Camera.Size> sizes) {
        Camera.Size largest = null;
        for (Camera.Size size : sizes) {
            if (largest == null || getArea(largest) < getArea(size)) {
                largest = size;
            }
        }
        return largest != null ? new Size(largest.width, largest.height) : null;
    }

    private static long getArea(final Camera.Size size) {
        return size.width * size.height;
    }

    private Util() {
    }
}