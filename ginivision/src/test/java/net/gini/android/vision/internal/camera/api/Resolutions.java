package net.gini.android.vision.internal.camera.api;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;

import android.hardware.Camera;

import net.gini.android.vision.internal.util.Size;

import java.util.ArrayList;
import java.util.List;

final class Resolutions {

    //4096	×	3072    12,582,912  4:3     (1,33)
    //4096	×	2304	9,437,184   16:9    (1,77)
    //3840	×	2160	8,294,400   16:9    (1,77)
    //3264	×	2448    7,990,272   4:3     (1,33)
    //3200	×	2400	7,680,000   4:3     (1,33)
    //3200	×	2048    6,553,600   14:9    (1,56)
    //2592	×	1944    5,038,848   4:3     (1,33)
    //2048	×	1536    3,145,728   4:3     (1,33)
    //1920	×	1080    2,073,600   16:9    (1,77)
    //1600	×	1200    1,920,000   4:3     (1,33)
    //1280	×	960     1,228,800   4:3     (1,33)
    //1280	×	768     983,040     15:9    (1,66)
    //1280	×	720     921,600     16:9    (1,77)
    //1024	×	768     786,432     4:3     (1,33)
    //800	×	600     480,000     4:3     (1,33)

    static final int[][] DECREASING_RESOLUTIONS = {
            {4096, 3072},
            {4096, 2304},
            {3840, 2160},
            {3264, 2448},
            {3200, 2400},
            {3200, 2048},
            {2592, 1944},
            {2048, 1536},
            {1920, 1080},
            {1600, 1200},
            {1280, 960},
            {1280, 768},
            {1280, 720},
            {1024, 768},
            {800, 600}
    };

    static final int[][] INCREASING_RESOLUTIONS = {
            {800, 600},
            {1024, 768},
            {1280, 720},
            {1280, 768},
            {1280, 960},
            {1600, 1200},
            {1920, 1080},
            {2048, 1536},
            {2592, 1944},
            {3200, 2048},
            {3200, 2400},
            {3264, 2448},
            {3840, 2160},
            {4096, 2304},
            {4096, 3072}
    };

    static final int[][] UNSORTED_RESOLUTIONS = {
            {3200, 2400},
            {3840, 2160},
            {4096, 3072},
            {4096, 2304},
            {3200, 2048},
            {2592, 1944},
            {1280, 768},
            {2048, 1536},
            {800, 600},
            {1600, 1200},
            {1280, 960},
            {3264, 2448},
            {1280, 720},
            {1024, 768},
            {1920, 1080}
    };

    static List<Camera.Size> toSizesList(final int[][] resolutions) {
        final List<Camera.Size> sizes = new ArrayList<>(resolutions.length);
        for (final int[] resolution : resolutions) {
            sizes.add(toCameraSize(resolution));
        }
        return sizes;
    }

    static Camera.Size toCameraSize(final int[] resolution) {
        final Camera.Size size = mock(Camera.Size.class);
        size.width = resolution[0];
        size.height = resolution[1];
        return size;
    }

    static Size toSize(final int[] resolution) {
        return new Size(resolution[0], resolution[1]);
    }

    static void assertSizeEqualsResolution(final Size largestSize, final int[] expectedResolution) {
        assertThat(largestSize).isNotNull();
        assertThat(largestSize.width).isEqualTo(expectedResolution[0]);
        assertThat(largestSize.height).isEqualTo(expectedResolution[1]);
    }
}
