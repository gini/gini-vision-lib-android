package net.gini.android.vision.internal.camera.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.View;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.internal.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 15.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraControllerFake implements CameraInterface {

    private static final Size DEFAULT_PREVIEW_SIZE = new Size(900, 1200);
    private Photo mPhoto;
    private Camera.PreviewCallback mPreviewCallback;
    private Size mPreviewSize = DEFAULT_PREVIEW_SIZE;
    private SurfaceHolder mSurfaceHolder;
    private boolean mFlashEnabled;

    @NonNull
    @Override
    public CompletableFuture<Void> open() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void close() {

    }

    @NonNull
    @Override
    public CompletableFuture<Void> startPreview(@NonNull final SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
        return CompletableFuture.completedFuture(null);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> startPreview() {
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void stopPreview() {

    }

    @Override
    public boolean isPreviewRunning() {
        return true;
    }

    @Override
    public void enableTapToFocus(@NonNull final View tapView,
            @Nullable final TapToFocusListener listener) {

    }

    @Override
    public void disableTapToFocus(@NonNull final View tapView) {

    }

    @NonNull
    @Override
    public CompletableFuture<Boolean> focus() {
        return CompletableFuture.completedFuture(true);
    }

    @NonNull
    @Override
    public CompletableFuture<Photo> takePicture() {
        return CompletableFuture.completedFuture(mPhoto);
    }

    @NonNull
    @Override
    public Size getPreviewSize() {
        return mPreviewSize;
    }

    @NonNull
    @Override
    public Size getPreviewSizeForDisplay() {
        return mPreviewSize;
    }

    @NonNull
    @Override
    public Size getPictureSize() {
        return mPreviewSize;
    }

    @Nullable
    public Camera.PreviewCallback getPreviewCallback() {
        return mPreviewCallback;
    }

    @Override
    public void setPreviewCallback(@NonNull final Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }

    @Override
    public int getCameraRotation() {
        return 0;
    }

    @Override
    public boolean isFlashAvailable() {
        return true;
    }

    @Override
    public boolean isFlashEnabled() {
        return mFlashEnabled;
    }

    @Override
    public void setFlashEnabled(final boolean enabled) {
        mFlashEnabled = enabled;
    }

    public void showImageAsPreview(@NonNull final byte[] image, @Nullable final byte[] imageNV21) {
        if (mSurfaceHolder == null) {
            return;
        }
        mPhoto = PhotoFactory.newPhotoFromJpeg(image, 0, "portrait", "photo",
                ImageDocument.Source.newCameraSource());

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(image, 0, image.length, options);
        mPreviewSize = new Size(options.outWidth, options.outHeight);

        final Canvas canvas = mSurfaceHolder.lockCanvas();
        final Bitmap previewBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        canvas.drawBitmap(previewBitmap, null,
                new Rect(0, 0, mSurfaceHolder.getSurfaceFrame().right,
                        mSurfaceHolder.getSurfaceFrame().bottom), null);
        mSurfaceHolder.unlockCanvasAndPost(canvas);

        if (mPreviewCallback != null && imageNV21 != null) {
            mPreviewCallback.onPreviewFrame(imageNV21, null);
        }
    }


}
