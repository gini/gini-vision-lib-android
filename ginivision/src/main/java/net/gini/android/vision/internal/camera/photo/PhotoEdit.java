package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;

/**
 * @exclude
 */
public class PhotoEdit {

    private static final int DEF_QUALITY = 100;

    private final Photo mPhoto;
    private int mRotationDegrees;
    private int mQuality = DEF_QUALITY;

    public PhotoEdit(@NonNull Photo photo) {
        mPhoto = photo;
        resetToDefaults();
    }

    @NonNull
    public PhotoEdit rotate(int degrees) {
        degrees %= 360;
        mRotationDegrees = degrees;
        return this;
    }

    @NonNull
    public PhotoEdit compress(int quality) {
        mQuality = quality;
        return this;
    }

    public void apply() {
        applyChanges(mPhoto, mRotationDegrees, mQuality);
        resetToDefaults();
    }

    public void applyAsync(@NonNull final PhotoEditCallback callback) {
        EditAsync async = new EditAsync(mPhoto, mRotationDegrees, mQuality);
        async.setCallback(new PhotoEditCallback() {
            @Override
            public void onDone(@NonNull Photo photo) {
                callback.onDone(photo);
                resetToDefaults();
            }

            @Override
            public void onFailed() {
                callback.onFailed();
                resetToDefaults();
            }
        });
        async.execute((Void[]) null);
    }

    private void resetToDefaults() {
        mRotationDegrees = mPhoto.getRotationForDisplay();
        mQuality = DEF_QUALITY;
    }

    private static void applyChanges(@NonNull Photo photo, int rotationDegrees, int quality) {
        if (photo.getJpeg() == null) {
            return;
        }

        if (quality != DEF_QUALITY) {
            Bitmap originalImage = BitmapFactory.decodeByteArray(photo.getJpeg(), 0, photo.getJpeg().length);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            originalImage.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

            byte[] jpeg = byteArrayOutputStream.toByteArray();
            photo.setJpeg(jpeg);
            photo.setBitmapPreview(Photo.createPreview(jpeg));
        }

        photo.setRotationForDisplay(rotationDegrees);
        photo.updateExif();
    }

    private static class EditAsync extends AsyncTask<Void, Void, Photo> {

        private static final PhotoEditCallback NO_OP_CALLBACK = new PhotoEditCallback() {
            @Override
            public void onDone(@NonNull Photo photo) {
            }

            @Override
            public void onFailed() {
            }
        };

        private final Photo mPhoto;
        private final int mRotationDegrees;
        private final int mQuality;
        private PhotoEditCallback mCallback = NO_OP_CALLBACK;

        public EditAsync(@NonNull Photo photo, int rotationDegrees, int quality) {
            mPhoto = photo;
            mRotationDegrees = rotationDegrees;
            mQuality = quality;
        }

        public void setCallback(@Nullable PhotoEditCallback callback) {
            if (callback == null) {
                mCallback = NO_OP_CALLBACK;
            } else {
                mCallback = callback;
            }
        }

        @Override
        protected Photo doInBackground(Void... params) {
            applyChanges(mPhoto, mRotationDegrees, mQuality);
            return mPhoto;
        }

        @Override
        protected void onPostExecute(Photo result) {
            if (result != null) {
                mCallback.onDone(result);
            } else {
                mCallback.onFailed();
            }
        }
    }

    public interface PhotoEditCallback {
        void onDone(@NonNull Photo photo);

        void onFailed();
    }
}
