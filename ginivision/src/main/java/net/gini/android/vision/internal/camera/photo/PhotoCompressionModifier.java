package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import java.io.ByteArrayOutputStream;

/**
 * @exclude
 */
class PhotoCompressionModifier implements PhotoModifier {

    private final Photo mPhoto;
    private final int mQuality;

    PhotoCompressionModifier(final int quality, @NonNull final Photo photo) {
        mQuality = quality;
        mPhoto = photo;
    }

    @VisibleForTesting
    int getQuality() {
        return mQuality;
    }

    @Override
    public void modify() {
        if (mPhoto.getJpeg() == null) {
            return;
        }
        synchronized (mPhoto) {
            final Bitmap originalImage = BitmapFactory.decodeByteArray(mPhoto.getJpeg(), 0,
                    mPhoto.getJpeg().length);

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            originalImage.compress(Bitmap.CompressFormat.JPEG, mQuality, byteArrayOutputStream);

            final byte[] jpeg = byteArrayOutputStream.toByteArray();
            mPhoto.setJpeg(jpeg);
            mPhoto.updateBitmapPreview();

            mPhoto.updateExif();
        }
    }
}
