package net.gini.android.vision.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    private Matrix mMatrix = new Matrix();
    private int mQuality = DEF_QUALITY;

    public PhotoEdit(@NonNull Photo photo) {
        mPhoto = photo;
    }

    @NonNull
    public PhotoEdit rotate(int degrees) {
        degrees %= 360;
        mMatrix.postRotate(degrees);
        return this;
    }

    @NonNull
    public PhotoEdit compress(int quality) {
        mQuality = quality;
        return this;
    }

    public void apply() {
        applyChanges(mPhoto, mMatrix, mQuality);
        resetToDefaults();
    }

    public void applyAsync(@NonNull final PhotoEditCallback callback) {
        EditAsync async = new EditAsync(mPhoto, mMatrix, mQuality);
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
        mMatrix.reset();
        mQuality = DEF_QUALITY;
    }

    private static void applyChanges(@NonNull Photo photo, @NonNull Matrix matrix, int quality) {
        if (photo.getJpeg() == null) {
            return;
        }

        Bitmap originalImage = BitmapFactory.decodeByteArray(photo.getJpeg(), 0, photo.getJpeg().length);

        Bitmap editedImage = originalImage;
        if (!matrix.isIdentity()) {
            editedImage = Bitmap.createBitmap(originalImage, 0, 0,
                    originalImage.getWidth(), originalImage.getHeight(), matrix, false);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        editedImage.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);

        byte[] jpeg = byteArrayOutputStream.toByteArray();

        photo.setJpeg(jpeg);
        photo.updateExif();
        photo.setBitmapPreview(Photo.createPreview(jpeg));
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
        private final Matrix mMatrix;
        private final int mQuality;
        private PhotoEditCallback mCallback = NO_OP_CALLBACK;

        public EditAsync(@NonNull Photo photo, @NonNull Matrix matrix, int quality) {
            mPhoto = photo;
            mMatrix = matrix;
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
            applyChanges(mPhoto, mMatrix, mQuality);
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
