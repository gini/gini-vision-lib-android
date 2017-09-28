package net.gini.android.vision.internal.camera.api;

import static net.gini.android.vision.internal.camera.api.Resolutions.DECREASING_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.INCREASING_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.UNSORTED_RESOLUTIONS;
import static net.gini.android.vision.internal.camera.api.Resolutions.assertSizeEqualsResolution;
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
public class SizeSelectionHelper_GetLargestSizeTest {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][]{
                {"Largest from decreasing resolutions", DECREASING_RESOLUTIONS, new int[]{4096, 3072}},
                {"Largest from increasing resolutions", INCREASING_RESOLUTIONS, new int[]{4096, 3072}},
                {"Largest from unsorted resolutions", UNSORTED_RESOLUTIONS, new int[]{4096, 3072}}
        });
    }

    private final String description;
    private final int[][] resolutions;
    private final int[] expectedResolution;

    public SizeSelectionHelper_GetLargestSizeTest(final String description, final int[][] resolutions,
            final int[] expectedResolution) {
        this.description = description;
        this.resolutions = resolutions;
        this.expectedResolution = expectedResolution;
    }

    @Test
    public void should_returnLargestSize() {
        List<Camera.Size> sizes = toSizesList(resolutions);
        Size largestSize = SizeSelectionHelper.getLargestSize(sizes);
        assertSizeEqualsResolution(largestSize, expectedResolution);
    }
}