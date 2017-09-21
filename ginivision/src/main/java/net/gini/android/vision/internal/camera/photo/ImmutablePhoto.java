package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.document.ImageDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @exclude
 */
public class ImmutablePhoto implements Photo {

    private static final Logger LOG = LoggerFactory.getLogger(ImmutablePhoto.class);

    protected Bitmap mBitmapPreview;
    protected byte[] mData;
    protected int mRotationForDisplay = 0;
    private final ImageDocument.ImageFormat mImageFormat;
    private final boolean mIsImported;

    ImmutablePhoto(@NonNull byte[] data, int orientation,
            @NonNull final ImageDocument.ImageFormat imageFormat, final boolean isImported) {
        mData = data;
        mRotationForDisplay = orientation;
        mImageFormat = imageFormat;
        mIsImported = isImported;
        mBitmapPreview = createPreview();
    }

    ImmutablePhoto(@NonNull ImageDocument imageDocument) {
        mData = imageDocument.getData();
        mRotationForDisplay = imageDocument.getRotationForDisplay();
        mImageFormat = imageDocument.getFormat();
        mIsImported = imageDocument.isImported();
        mBitmapPreview = createPreview();
    }

    @Nullable
    protected Bitmap createPreview() {
        if (mData == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        return BitmapFactory.decodeByteArray(mData, 0, mData.length, options);
    }

    @Nullable
    public synchronized Bitmap getBitmapPreview() {
        return mBitmapPreview;
    }

    @Override
    public ImageDocument.ImageFormat getImageFormat() {
        return mImageFormat;
    }

    @Override
    public PhotoEdit edit() {
        return new NoOpPhotoEdit(this);
    }

    @Override
    public void updateBitmapPreview() {

    }

    @Override
    public void updateExif() {

    }

    @Override
    public void updateRotationDeltaBy(final int i) {

    }

    @Override
    public boolean isImported() {
        return mIsImported;
    }

    @Nullable
    public synchronized byte[] getData() {
        return mData;
    }

    @Override
    public void setData(final byte[] data) {

    }

    @Override
    public int getRotationForDisplay() {
        return 0;
    }

    @Override
    public void setRotationForDisplay(final int rotationDegrees) {

    }

    public synchronized void saveToFile(File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(mData, 0, mData.length);
        } catch (IOException e) {
            LOG.error("Failed to save jpeg to {}", file.getAbsolutePath(), e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    LOG.error("Closing FileOutputStream failed for {}", file.getAbsolutePath(), e);
                }
            }
        }
    }

    @VisibleForTesting
    public synchronized void savePreviewToFile(File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            mBitmapPreview.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        } catch (FileNotFoundException e) {
            LOG.error("Failed to save preview to {}", file.getAbsolutePath(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    LOG.error("Closing FileOutputStream failed for {}", file.getAbsolutePath(), e);
                }
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = cache.storeBitmap(mBitmapPreview);
        dest.writeParcelable(token, flags);

        token = cache.storeJpeg(mData);
        dest.writeParcelable(token, flags);

        dest.writeSerializable(mImageFormat);
        dest.writeInt(mIsImported ? 1 : 0);
    }

    public static final Parcelable.Creator<ImmutablePhoto> CREATOR =
            new Parcelable.Creator<ImmutablePhoto>() {
                @Override
                public ImmutablePhoto createFromParcel(Parcel in) {
                    return new ImmutablePhoto(in);
                }

                @Override
                public ImmutablePhoto[] newArray(int size) {
                    return new ImmutablePhoto[size];
                }
            };

    protected ImmutablePhoto(Parcel in) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = in.readParcelable(ImageCache.Token.class.getClassLoader());
        mBitmapPreview = cache.getBitmap(token);
        cache.removeBitmap(token);

        token = in.readParcelable(ImageCache.Token.class.getClassLoader());
        mData = cache.getJpeg(token);
        cache.removeJpeg(token);

        mImageFormat = (ImageDocument.ImageFormat) in.readSerializable();
        mIsImported = in.readInt() == 1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ImmutablePhoto photo = (ImmutablePhoto) o;

        if (mBitmapPreview != null ? !mBitmapPreview.equals(photo.mBitmapPreview)
                : photo.mBitmapPreview != null) {
            return false;
        }
        return Arrays.equals(mData, photo.mData);

    }

    @Override
    public int hashCode() {
        int result = mBitmapPreview != null ? mBitmapPreview.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(mData);
        return result;
    }
}
