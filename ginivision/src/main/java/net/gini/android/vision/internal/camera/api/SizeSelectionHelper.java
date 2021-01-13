package net.gini.android.vision.internal.camera.api;

import android.hardware.Camera;

import net.gini.android.vision.internal.util.Size;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;

import static java.lang.Math.abs;

/**
 * Internal use only.
 *
 * @suppress
 */
public final class SizeSelectionHelper {

    @Nullable
    public static Pair<Size, Size> getBestSize(
            @NonNull final List<Camera.Size> pictureSizes,
            @NonNull final List<Camera.Size> previewSizes,
            final int maxArea,
            final int minArea
    ) {
        Camera.Size bestPicture = null;
        Size bestPreview = null;
        for (final Camera.Size size : pictureSizes) {
            final long area = getArea(size);
            if (minArea < area && area < maxArea && (bestPicture == null || getArea(bestPicture) < area)) {
                final Size preview = getClosestSizeWithSimilarAspectRatio(previewSizes, new Size(size.width, size.height), maxArea);
                if (preview != null) {
                    bestPicture = size;
                    bestPreview = preview;
                }
            }
        }
        if (bestPicture != null && bestPreview != null) {
            return new Pair<>(new Size(bestPicture.width, bestPicture.height), bestPreview);
        }
        for (final Camera.Size size : pictureSizes) {
            final long area = getArea(size);
            if (maxArea < area && (bestPicture == null || area < getArea(bestPicture))) {
                final Size preview = getClosestSizeWithSimilarAspectRatio(previewSizes, new Size(size.width, size.height), maxArea);
                if (preview != null) {
                    bestPicture = size;
                    bestPreview = preview;
                }
            }
        }
        if (bestPicture != null && bestPreview != null) {
            return new Pair<>(new Size(bestPicture.width, bestPicture.height), bestPreview);
        }
        return null;
    }

    @Nullable
    public static Size getLargestAllowedSize(@NonNull final List<Camera.Size> sizes, final int maxArea) {
        Camera.Size largest = null;
        for (final Camera.Size size : sizes) {
            if ((largest == null || getArea(largest) < getArea(size)) && getArea(size) <= maxArea) {
                largest = size;
            }
        }
        return largest != null ? new Size(largest.width, largest.height) : null;
    }

    @Nullable
    public static Size getLargestAllowedSizeWithSimilarAspectRatio(
            @NonNull final List<Camera.Size> sizes, @NonNull final Size referenceSize, final int maxArea) {
        final List<Camera.Size> sameAspectSizes = getSameAspectRatioSizes(sizes, referenceSize);
        return getLargestAllowedSize(sameAspectSizes, maxArea);
    }

    @Nullable
    public static Size getClosestSizeWithSimilarAspectRatio(
            @NonNull final List<Camera.Size> sizes, @NonNull final Size referenceSize, final int maxArea) {
        final List<Camera.Size> sameAspectSizes = getSameAspectRatioSizes(sizes, referenceSize);
        return getClosestSize(sameAspectSizes, maxArea);
    }

    private static Size getClosestSize(List<Camera.Size> sizes, int maxArea) {
        Camera.Size closest = null;
        for (final Camera.Size size : sizes) {
            if (closest == null || abs(getArea(closest) - maxArea) > abs(getArea(size) - maxArea)) {
                closest = size;
            }
        }
        return closest != null ? new Size(closest.width, closest.height) : null;
    }

    @NonNull
    private static List<Camera.Size> getSameAspectRatioSizes(@NonNull final List<Camera.Size> sizes,
            @NonNull final Size referenceSize) {
        final float referenceAspectRatio =
                (float) referenceSize.width / (float) referenceSize.height;
        final List<Camera.Size> sameAspectSizes = new ArrayList<>();
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
        return abs(aspectRatio - referenceAspectRatio) < 0.1f;
    }

    private static long getArea(final Camera.Size size) {
        return size.width * size.height;
    }

    private SizeSelectionHelper() {
    }
}