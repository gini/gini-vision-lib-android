package net.gini.android.vision.internal.util;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public class Size implements Comparable<Size> {
    public final int width;
    public final int height;

    public Size(final int width, final int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public int compareTo(@NonNull final Size other) {
        final int area = width * height;
        final int areaOther = other.width * other.height;
        return area - areaOther;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Size size = (Size) o;

        if (width != size.width) {
            return false;
        }
        return height == size.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }

    @Override
    public String toString() {
        return "Size{width=" + width + ", height=" + height + '}';
    }
}
