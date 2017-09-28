package net.gini.android.vision.internal.camera.api;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.util.Size;

import java.util.ArrayList;
import java.util.List;

/**
 * @exclude 
 */
public final class SizeSelectionHelper {

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

    @Nullable
    public static Size getLargestSizeWithSimilarAspectRatio(
            @NonNull final List<Camera.Size> sizes, @NonNull final Size referenceSize) {
        List<Camera.Size> sameAspectSizes = getSameAspectRatioSizes(sizes, referenceSize);
        return getLargestSize(sameAspectSizes);
    }

    @NonNull
    private static List<Camera.Size> getSameAspectRatioSizes(final @NonNull List<Camera.Size> sizes,
            final @NonNull Size referenceSize) {
        final float referenceAspectRatio =
                (float) referenceSize.width / (float) referenceSize.height;
        List<Camera.Size> sameAspectSizes = new ArrayList<>();
        for (final Camera.Size size : sizes) {
            final float aspectRatio = (float) size.width / (float) size.height;
            if (isSimilarAspectRatio(aspectRatio, referenceAspectRatio)) {
                sameAspectSizes.add(size);
            }
        }
        return sameAspectSizes;
    }

    private static boolean isSimilarAspectRatio(final float aspectRatio,
            final float referenceAspectRatio) {
        return Math.abs(aspectRatio - referenceAspectRatio) < 0.1f;
    }

    private static long getArea(final Camera.Size size) {
        return size.width * size.height;
    }

    private SizeSelectionHelper() {
    }
}