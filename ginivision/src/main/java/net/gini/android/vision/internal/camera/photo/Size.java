package net.gini.android.vision.internal.camera.photo;

import android.support.annotation.NonNull;

/**
 * @exclude
 */
public class Size implements Comparable<Size> {
    public final int width;
    public final int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int compareTo(@NonNull Size other) {
        int area = width * height;
        int areaOther = other.width * other.height;
        return area - areaOther;
    }

    @Override
    public String toString() {
        return "Size{width=" + width + ", height=" + height + '}';
    }
}
