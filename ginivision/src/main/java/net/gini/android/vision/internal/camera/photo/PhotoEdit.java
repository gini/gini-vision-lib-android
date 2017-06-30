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
    private List<PhotoModifier> mPhotoModifiers;

    PhotoEdit(@NonNull Photo photo) {
        mPhoto = photo;
    }

    private List<PhotoModifier> getPhotoModifiers() {
        if (mPhotoModifiers == null) {
            mPhotoModifiers = new ArrayList<>();
        }
        return mPhotoModifiers;
    }

    @NonNull
    public PhotoEdit rotateTo(int degrees) {
        final PhotoRotationModifier rotationModifier = new PhotoRotationModifier(degrees, mPhoto);
        getPhotoModifiers().add(rotationModifier);
        return this;
    }

    @NonNull
    public PhotoEdit compressBy(int quality) {
        final PhotoCompressionModifier compressionModifier = new PhotoCompressionModifier(quality, mPhoto);
        getPhotoModifiers().add(compressionModifier);
        return this;
    }

    public void apply() {
        applyChanges(mPhotoModifiers);
        mPhotoModifiers = null;
    }

    public void applyAsync(@NonNull final PhotoEditCallback callback) {
        final EditAsync async = new EditAsync(mPhoto, mPhotoModifiers);
        mPhotoModifiers = null;
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
            public void onDone(@NonNull Photo photo) {
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
