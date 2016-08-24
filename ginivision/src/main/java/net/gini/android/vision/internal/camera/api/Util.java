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
    public static Size getLargestFourThreeRatioSize(@NonNull List<Camera.Size> sizes) {
        Camera.Size bestFit = null;
        for (Camera.Size size : sizes) {
            if (Math.abs((float) size.width / (float) size.height - 4.f / 3.f) < 0.001 &&
                    (bestFit == null || bestFit.width * bestFit.height < size.width * size.height)) {
                bestFit = size;
            }
        }
        return bestFit != null ? new Size(bestFit.width, bestFit.height) : null;
    }

    private Util() {
    }
}