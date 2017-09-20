package net.gini.android.vision.internal.camera.photo;

import android.support.annotation.NonNull;

/**
 * Created by aszotyori on 21.09.17.
 */

public class NoOpPhotoEdit extends PhotoEdit {

    NoOpPhotoEdit(@NonNull final Photo photo) {
        super(photo);
    }

    @NonNull
    @Override
    public PhotoEdit rotateTo(final int degrees) {
        return this;
    }

    @NonNull
    @Override
    public PhotoEdit compressBy(final int quality) {
        return this;
    }
}
