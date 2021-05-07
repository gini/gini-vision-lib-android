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
                        new int[][]{{16, 9}}, new int[][]{{1920, 1080}, {16, 9}}, 8_000_000, 0, 4f / 3f},
                {"Largest 16:9 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[][]{{16, 9}}, new int[][]{{1920, 1080}, {16, 9}}, 8_000_000, 0, 4f / 3f},
                {"Largest 16:9 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{16, 9}}, new int[][]{{1920, 1080}, {16, 9}}, 8_000_000, 0, 4f / 3f},
                // 4:3, (~1.33)
                {"Largest 4:3 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[][]{{3264, 2448}, {4, 3}}, 8_000_000, 0, 4f / 3f},
                {"Largest 4:3 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[][]{{3264, 2448}, {4, 3}}, 8_000_000, 0, 4f / 3f},
                {"Largest 4:3 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[][]{{3264, 2448}, {4, 3}}, 8_000_000, 0, 4f / 3f},
                // 14:9 (~1.55)
                {"Largest 14:9 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[][]{{376, 240}}, new int[][]{{3200, 2048}, {376, 240}}, 8_000_000, 0, 4f / 3f},
                {"Largest 14:9 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[][]{{376, 240}}, new int[][]{{3200, 2048}, {376, 240}}, 8_000_000, 0, 4f / 3f},
                {"Largest 14:9 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{376, 240}}, new int[][]{{3200, 2048}, {376, 240}}, 8_000_000, 0, 4f / 3f},
                // No exact matching aspect ratio for 4224x3136 (~1.346939) but should still find
                // a similar resolution
                {"Largest similar for 4224x3136 from decreasing resolutions",
                        DECREASING_RESOLUTIONS,
                        new int[][]{{4224, 3136}}, new int[][]{{3264, 2448}, {4224, 3136}}, 8_000_000, 0, 4f / 3f},
                {"Largest similar for 4224x3136 from increasing resolutions",
                        INCREASING_RESOLUTIONS,
                        new int[][]{{4224, 3136}}, new int[][]{{3264, 2448}, {4224, 3136}}, 8_000_000, 0, 4f / 3f},
                {"Largest similar for 4224x3136 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{4224, 3136}}, new int[][]{{3264, 2448}, {4224, 3136}}, 8_000_000, 0, 4f / 3f},
                // No exact matching aspect ratio for 5376x3752 (~1.432836) but should still find
                // a similar resolution
                {"Largest similar for 5376x3752 from decreasing resolutions",
                        DECREASING_RESOLUTIONS,
                        new int[][]{{5376, 3752}}, new int[][]{{3264, 2448}, {5376, 3752}}, 8_000_000, 0, 4f / 3f},
                {"Largest similar for 5376x3752 from increasing resolutions",
                        INCREASING_RESOLUTIONS,
                        new int[][]{{5376, 3752}}, new int[][]{{3264, 2448}, {5376, 3752}}, 8_000_000, 0, 4f / 3f},
                {"Largest similar for 5376x3752 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{5376, 3752}}, new int[][]{{3264, 2448}, {5376, 3752}}, 8_000_000, 0, 4f / 3f},
                {"Fallback to smallest 16:9 above maxArea for unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{16, 9}}, new int[][]{{3840, 2160}, {16, 9}}, 8_000_000, 7_000_000, 4f / 3f},
                {"Fallback to smallest 4:3 above maxArea for unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[][]{{4, 3}}, new int[][]{{4096, 3072}, {4, 3}}, 8_100_000, 8_000_000, 4f / 3f},
                {"Largest resolution with at least 4:3 aspect ratio (landscape sizes)", new int[][]{{400, 300}, {500, 500}, {800, 700}},
                        new int[][]{{4, 3}, {5, 5}}, new int[][]{{400, 300}, {4, 3}}, 8_000_000, 0, 4f / 3f},
                {"Largest resolution with at least 4:3 aspect ratio (portrait sizes)", new int[][]{{300, 400}, {500, 500}, {700, 800}},
                        new int[][]{{3, 4}, {5, 5}}, new int[][]{{300, 400}, {3, 4}}, 8_000_000, 0, 4f / 3f},
        });
    }

    private final String description;
    private final int[][] pictureResolution;
    private final int[][] previewResolutions;
    private final int[][] expectedSizes;
    private final int maxArea;
    private final int minArea;
    private final float minAspectRatio;

    public SizeSelectionHelper_GetBestSizeTest(final String description,
                                               final int[][] pictureResolution,
                                               final int[][] previewResolutions,
                                               final int[][] expectedSizes,
                                               final int maxArea,
                                               final int minArea,
                                               final float minAspectRatio) {
        this.description = description;
        this.pictureResolution = pictureResolution;
        this.previewResolutions = previewResolutions;
        this.expectedSizes = expectedSizes;
        this.maxArea = maxArea;
        this.minArea = minArea;
        this.minAspectRatio = minAspectRatio;
    }

    @Test
    public void should_returnLargestPictureSizeWithMatchingPreviewSize() {
        final List<Camera.Size> picture = toSizesList(pictureResolution);
        final List<Camera.Size> preview = toSizesList(previewResolutions);
        final Pair<Size, Size> size = SizeSelectionHelper.getBestSize(picture,
                preview, maxArea, minArea, minAspectRatio);
        assertThat(size).isNotNull();
        assertSizeEqualsResolution(size.first, expectedSizes[0]);
        assertSizeEqualsResolution(size.second, expectedSizes[1]);
    }
}