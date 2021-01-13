package net.gini.android.vision.internal.camera.photo;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
class NoOpPhotoEdit extends PhotoEdit {

    NoOpPhotoEdit(@NonNull final Photo photo) {
        super(photo);
    }

    @NonNull
    @Override
    public PhotoEdit rotateTo(final int degrees) {
        getPhoto().setRotationForDisplay(degrees);
        return this;
    }

    @NonNull
    @Override
    public PhotoEdit compressBy(final int quality) {
        return this;
    }

    @NonNull
    @Override
    public PhotoEdit compressByDefault() {
        return this;
    }
}
