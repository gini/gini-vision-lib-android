package net.gini.android.vision.internal.camera.api;

import android.hardware.Camera;

import androidx.core.util.Pair;

import net.gini.android.vision.internal.util.Size;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static net.gini.android.vision.internal.camera.api.Resolutions.DECREASING_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.INCREASING_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.UNSORTED_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.assertSizeEqualsResolution;
import static net.gini.android.vision.internal.camera.api.Resolutions.toSizesList;

import static com.google.common.truth.Truth.assertThat;


@RunWith(Parameterized.class)
public class SizeSelectionHelper_GetBestSizeTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                // 16:9, (~1.77)
                {"Largest 16:9 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[][]{{16, 9}}, new int[]{1920, 1080}, 8_000_000, 0},
                {"Largest 16:9 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[][]{{16, 9}}, new int[]{1920, 1080}, 8_000_000, 0},
                {"Largest 16:9 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{16, 9}}, new int[]{1920, 1080}, 8_000_000, 0},
                // 4:3, (~1.33)
                {"Largest 4:3 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[]{3264, 2448}, 8_000_000, 0},
                {"Largest 4:3 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[]{3264, 2448}, 8_000_000, 0},
                {"Largest 4:3 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[]{3264, 2448}, 8_000_000, 0},
                // 14:9 (~1.55)
                {"Largest 14:9 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[][]{{376, 240}}, new int[]{3200, 2048}, 8_000_000, 0},
                {"Largest 14:9 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[][]{{376, 240}}, new int[]{3200, 2048}, 8_000_000, 0},
                {"Largest 14:9 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{376, 240}}, new int[]{3200, 2048}, 8_000_000, 0},
                // No exact matching aspect ratio for 4224x3136 (~1.346939) but should still find
                // a similar resolution
                {"Largest similar for 4224x3136 from decreasing resolutions",
                        DECREASING_RESOLUTIONS,
                        new int[][]{{4224, 3136}}, new int[]{3264, 2448}, 8_000_000, 0},
                {"Largest similar for 4224x3136 from increasing resolutions",
                        INCREASING_RESOLUTIONS,
                        new int[][]{{4224, 3136}}, new int[]{3264, 2448}, 8_000_000, 0},
                {"Largest similar for 4224x3136 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{4224, 3136}}, new int[]{3264, 2448}, 8_000_000, 0},
                // No exact matching aspect ratio for 5376x3752 (~1.432836) but should still find
                // a similar resolution
                {"Largest similar for 5376x3752 from decreasing resolutions",
                        DECREASING_RESOLUTIONS,
                        new int[][]{{5376, 3752}}, new int[]{3264, 2448}, 8_000_000, 0},
                {"Largest similar for 5376x3752 from increasing resolutions",
                        INCREASING_RESOLUTIONS,
                        new int[][]{{5376, 3752}}, new int[]{3264, 2448}, 8_000_000, 0},
                {"Largest similar for 5376x3752 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{5376, 3752}}, new int[]{3264, 2448}, 8_000_000, 0},
                {"Fallback to smallest 16:9 above maxArea for unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{16, 9}}, new int[]{3840, 2160}, 8_000_000, 7_000_000},
                {"Fallback to smallest 4:3 above maxArea for unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[]{4096, 3072}, 8_100_000, 8_000_000},
        });
    }

    private final String description;
    private final int[][] pictureResolution;
    private final int[][] previewResolutions;
    private final int[] expectedResolution;
    private final int maxArea;
    private final int minArea;

    public SizeSelectionHelper_GetBestSizeTest(final String description,
                                               final int[][] pictureResolution,
                                               final int[][] previewResolutions,
                                               final int[] expectedResolution,
                                               final int maxArea,
                                               final int minArea) {
        this.description = description;
        this.pictureResolution = pictureResolution;
        this.previewResolutions = previewResolutions;
        this.expectedResolution = expectedResolution;
        this.maxArea = maxArea;
        this.minArea = minArea;
    }

    @Test
    public void should_returnLargestSameAspectRatioSize() {
        final List<Camera.Size> picture = toSizesList(pictureResolution);
        final List<Camera.Size> preview = toSizesList(previewResolutions);
        final Pair<Size, Size> size = SizeSelectionHelper.getBestSize(picture,
                preview, maxArea, minArea);
        assertThat(size).isNotNull();
        assertSizeEqualsResolution(size.first, expectedResolution);
    }
}