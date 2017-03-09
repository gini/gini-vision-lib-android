package net.gini.android.vision.internal.camera.photo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @exclude
 */
public class PhotoEdit {

    private final Photo mPhoto;
    private final List<PhotoModifier> mPhotoModifiers;

    PhotoEdit(@NonNull Photo photo) {
        mPhoto = photo;
        mPhotoModifiers = new ArrayList<>();
    }

    @NonNull
    public PhotoEdit rotateTo(int degrees) {
        PhotoRotationModifier rotationModifier = new PhotoRotationModifier(degrees, mPhoto);
        mPhotoModifiers.add(rotationModifier);
        return this;
    }

    @NonNull
    public PhotoEdit compressBy(int quality) {
        PhotoCompressionModifier compressionModifier = new PhotoCompressionModifier(quality, mPhoto);
        mPhotoModifiers.add(compressionModifier);
        return this;
    }

    public void apply() {
        applyChanges(mPhotoModifiers);
        clearModifiers();
    }

    public void applyAsync(@NonNull final PhotoEditCallback callback) {
        EditAsync async = new EditAsync(mPhoto, mPhotoModifiers);
        async.setCallback(new PhotoEditCallback() {
            @Override
            public void onDone(@NonNull Photo photo) {
                callback.onDone(photo);
                clearModifiers();
            }

            @Override
            public void onFailed() {
                callback.onFailed();
                clearModifiers();
            }
        });
        async.execute((Void[]) null);
    }

    private void clearModifiers() {
        mPhotoModifiers.clear();
    }

    private static void applyChanges(@NonNull final List<PhotoModifier> modifiers) {
        for (final PhotoModifier modifier : modifiers) {
            modifier.modify();
        }
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
        private final List<PhotoModifier> mPhotoModifiers;
        private PhotoEditCallback mCallback = NO_OP_CALLBACK;

        EditAsync(@NonNull final Photo photo, @NonNull final List<PhotoModifier> photoModifiers) {
            mPhoto = photo;
            mPhotoModifiers = photoModifiers;
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
            applyChanges(mPhotoModifiers);
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
