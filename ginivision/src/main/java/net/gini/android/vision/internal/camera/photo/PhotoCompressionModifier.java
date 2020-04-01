package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

/**
 * Internal use only.
 *
 * @suppress
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
        if (mPhoto.getData() == null) {
            return;
        }
        synchronized (mPhoto) {
            final Bitmap originalImage = BitmapFactory.decodeByteArray(mPhoto.getData(), 0,
                    mPhoto.getData().length);
            if (originalImage == null) {
                return;
            }

            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            originalImage.compress(Bitmap.CompressFormat.JPEG, mQuality, byteArrayOutputStream);

            final byte[] jpeg = byteArrayOutputStream.toByteArray();
            mPhoto.setData(jpeg);
            mPhoto.updateBitmapPreview();

            mPhoto.updateExif();
        }
    }
}
