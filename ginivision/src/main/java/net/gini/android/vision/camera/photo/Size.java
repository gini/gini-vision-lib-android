package net.gini.android.vision.camera.photo;

import android.support.annotation.NonNull;

public class Size implements Comparable<Size> {
    public int width;
    public int height;

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
}
