package net.gini.android.vision.internal.camera.photo;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
class PhotoRotationModifier implements PhotoModifier {

    private final Photo mPhoto;
    private final int mRotationDegrees;

    PhotoRotationModifier(final int rotationDegrees, @NonNull final Photo photo) {
        mRotationDegrees = rotationDegrees;
        mPhoto = photo;
    }

    @Override
    public void modify() {
        if (mPhoto.getData() == null) {
            return;
        }
        synchronized (mPhoto) {
            mPhoto.updateRotationDeltaBy(mRotationDegrees - mPhoto.getRotationForDisplay());
            mPhoto.setRotationForDisplay(mRotationDegrees);

            mPhoto.updateExif();
        }
    }
}
