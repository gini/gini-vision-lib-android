package net.gini.android.vision.internal.camera.api;

import static net.gini.android.vision.internal.camera.api.Resolutions.DECREASING_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.INCREASING_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.UNSORTED_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.assertSizeEqualsResolution;
import static net.gini.android.vision.internal.camera.api.Resolutions.toSize;
import static net.gini.android.vision.internal.camera.api.Resolutions.toSizesList;

import android.hardware.Camera;

import net.gini.android.vision.internal.util.Size;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(Parameterized.class)
public class SizeSelectionHelper_GetLargestSizeWithSameAspectRatioTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                // 16:9, (~1.77)
                {"Largest 16:9 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[]{16, 9}, new int[]{4096, 2304}},
                {"Largest 16:9 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[]{16, 9}, new int[]{4096, 2304}},
                {"Largest 16:9 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[]{16, 9}, new int[]{4096, 2304}},
                // 4:3, (~1.33)
                {"Largest 4:3 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[]{4, 3}, new int[]{4096, 3072}},
                {"Largest 4:3 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[]{4, 3}, new int[]{4096, 3072}},
                {"Largest 4:3 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[]{4, 3}, new int[]{4096, 3072}},
                // 14:9 (~1.55)
                {"Largest 14:9 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[]{376, 240}, new int[]{3200, 2048}},
                {"Largest 14:9 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[]{376, 240}, new int[]{3200, 2048}},
                {"Largest 14:9 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[]{376, 240}, new int[]{3200, 2048}},
                // No exact matching aspect ratio for 4224x3136 (~1.346939) but should still find
                // a similar resolution
                {"Largest similar for 4224x3136 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[]{4224, 3136}, new int[]{4096, 3072}},
                {"Largest similar for 4224x3136 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[]{4224, 3136}, new int[]{4096, 3072}},
                {"Largest similar for 4224x3136 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[]{4224, 3136}, new int[]{4096, 3072}},
                // No exact matching aspect ratio for 5376x3752 (~1.432836) but should still find
                // a similar resolution
                {"Largest similar for 5376x3752 from decreasing resolutions", DECREASING_RESOLUTIONS,
                        new int[]{5376, 3752}, new int[]{4096, 3072}},
                {"Largest similar for 5376x3752 from increasing resolutions", INCREASING_RESOLUTIONS,
                        new int[]{5376, 3752}, new int[]{4096, 3072}},
                {"Largest similar for 5376x3752 from unsorted resolutions", UNSORTED_RESOLUTIONS,
                        new int[]{5376, 3752}, new int[]{4096, 3072}},
        });
    }

    private final String description;
    private final int[][] resolutions;
    private final int[] referenceResolution;
    private final int[] expectedResolution;

    public SizeSelectionHelper_GetLargestSizeWithSameAspectRatioTest(final String description,
            final int[][] resolutions,
            final int[] referenceResolution, final int[] expectedResolution) {
        this.description = description;
        this.resolutions = resolutions;
        this.referenceResolution = referenceResolution;
        this.expectedResolution = expectedResolution;
    }

    @Test
    public void should_returnLargestSameAspectRatioSize() {
        List<Camera.Size> sizes = toSizesList(resolutions);
        Size largestSize = SizeSelectionHelper.getLargestSizeWithSimilarAspectRatio(sizes,
                toSize(referenceResolution));
        assertSizeEqualsResolution(largestSize, expectedResolution);
    }
}