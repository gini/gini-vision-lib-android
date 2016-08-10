package net.gini.android.vision.requirements;

import static org.mockito.Mockito.mock;

import android.hardware.Camera;

public final class TestUtil {

    public static Camera.Size createSize(int width, int height) {
        Camera.Size size = mock(Camera.Size.class);
        size.width = width;
        size.height = height;
        return size;
    }

    private TestUtil() {
    }
}
