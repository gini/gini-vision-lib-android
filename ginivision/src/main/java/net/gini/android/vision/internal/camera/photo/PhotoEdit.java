package net.gini.android.vision.internal.camera.photo;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

/**
 * Internal use only.
 *
 * @suppress
 */
public class PhotoEdit {

    private static final int DEFAULT_JPEG_COMPRESSION_QUALITY = 50;

    private final Photo mPhoto;
    @VisibleForTesting
    List<PhotoModifier> mPhotoModifiers;

    PhotoEdit(@NonNull final Photo photo) {
        mPhoto = photo;
    }

    protected Photo getPhoto() {
        return mPhoto;
    }

    private List<PhotoModifier> getPhotoModifiers() {
        if (mPhotoModifiers == null) {
            mPhotoModifiers = new ArrayList<>();
        }
        return mPhotoModifiers;
    }

    @NonNull
    public PhotoEdit rotateTo(final int degrees) {
        final PhotoRotationModifier rotationModifier = new PhotoRotationModifier(degrees, mPhoto);
        getPhotoModifiers().add(rotationModifier);
        return this;
    }

    @NonNull
    public PhotoEdit compressBy(final int quality) {
        removeCompressionModifier();
        final PhotoCompressionModifier compressionModifier = new PhotoCompressionModifier(quality,
                mPhoto);
        getPhotoModifiers().add(compressionModifier);
        return this;
    }

    @NonNull
    public PhotoEdit compressByDefault() {
        removeCompressionModifier();
        final PhotoCompressionModifier compressionModifier = new PhotoCompressionModifier(
                DEFAULT_JPEG_COMPRESSION_QUALITY,
                mPhoto);
        getPhotoModifiers().add(compressionModifier);
        return this;
    }

    private void removeCompressionModifier() {
        final List<PhotoModifier> photoModifiers = getPhotoModifiers();
        for (final PhotoModifier photoModifier : photoModifiers) {
            if (photoModifier.getClass() == PhotoCompressionModifier.class) {
                photoModifiers.remove(photoModifier);
                return;
            }
        }
    }

    public void apply() {
        applyChanges(mPhotoModifiers);
        mPhotoModifiers = null; // NOPMD
    }

    public void applyAsync(@NonNull final PhotoEditCallback callback) {
        final EditAsync async = new EditAsync(mPhoto, mPhotoModifiers);
        mPhotoModifiers = null; // NOPMD
        async.setCallback(callback);
        async.execute((Void[]) null);
    }

    private static void applyChanges(@Nullable final List<PhotoModifier> modifiers) {
        if (modifiers == null) {
            return;
        }
        for (final PhotoModifier modifier : modifiers) {
            modifier.modify();
        }
    }

    private static class EditAsync extends AsyncTask<Void, Void, Photo> {

        private static final PhotoEditCallback NO_OP_CALLBACK = new PhotoEditCallback() {
            @Override
            public void onDone(@NonNull final Photo photo) {
            }

            @Override
            public void onFailed() {
            }
        };

        private final Photo mPhoto;
        private final List<PhotoModifier> mPhotoModifiers;
        private PhotoEditCallback mCallback = NO_OP_CALLBACK;

        EditAsync(@NonNull final Photo photo, @Nullable final List<PhotoModifier> photoModifiers) {
            mPhoto = photo;
            mPhotoModifiers = photoModifiers;
        }

        public void setCallback(@Nullable final PhotoEditCallback callback) {
            if (callback == null) {
                mCallback = NO_OP_CALLBACK;
            } else {
                mCallback = callback;
            }
        }

        @Override
        protected Photo doInBackground(final Void... params) {
            applyChanges(mPhotoModifiers);
            return mPhoto;
        }

        @Override
        protected void onPostExecute(final Photo result) {
            if (result != null) {
                mCallback.onDone(result);
            } else {
                mCallback.onFailed();
            }
        }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public interface PhotoEditCallback {

        void onDone(@NonNull Photo photo);

        void onFailed();
    }
}
