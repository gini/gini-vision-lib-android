package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import net.gini.android.vision.Document;
import net.gini.android.vision.document.ImageDocument;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

/**
 * Internal use only.
 *
 * @suppress
 */
class ImmutablePhoto implements Photo {

    private static final Logger LOG = LoggerFactory.getLogger(ImmutablePhoto.class);

    Bitmap mBitmapPreview;
    byte[] mData;
    int mRotationForDisplay;
    private final ImageDocument.ImageFormat mImageFormat;
    private final boolean mIsImported;
    private String mParcelableMemoryCacheTag;

    ImmutablePhoto(@NonNull final byte[] data, final int orientation,
            @NonNull final ImageDocument.ImageFormat imageFormat, final boolean isImported) {
        mData = data;
        mRotationForDisplay = orientation;
        mImageFormat = imageFormat;
        mIsImported = isImported;
        mBitmapPreview = createPreview();
    }

    ImmutablePhoto(@NonNull final ImageDocument imageDocument) {
        mData = imageDocument.getData();
        mRotationForDisplay = imageDocument.getRotationForDisplay();
        mImageFormat = imageDocument.getFormat();
        mIsImported = imageDocument.isImported();
        mBitmapPreview = createPreview();
    }

    @Nullable
    final Bitmap createPreview() {
        if (mData == null) {
            return null;
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        return BitmapFactory.decodeByteArray(mData, 0, mData.length, options);
    }

    @Override
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

    @Override
    @Nullable
    public synchronized byte[] getData() {
        return mData;
    }

    @Override
    public void setData(final byte[] data) {

    }

    @Override
    public int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    @Override
    public int getRotationDelta() {
        return 0;
    }

    @Override
    public void setRotationForDisplay(final int rotationDegrees) {
        mRotationForDisplay = rotationDegrees;
    }

    @Override
    public String getDeviceOrientation() {
        return null;
    }

    @Override
    public String getDeviceType() {
        return null;
    }

    @Override
    public Document.Source getSource() {
        return null;
    }

    @Override
    public Document.ImportMethod getImportMethod() {
        return null;
    }

    @Override
    public synchronized void saveToFile(final File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(mData, 0, mData.length);
        } catch (final IOException e) {
            LOG.error("Failed to save jpeg to {}", file.getAbsolutePath(), e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (final IOException e) {
                    LOG.error("Closing FileOutputStream failed for {}", file.getAbsolutePath(), e);
                }
            }
        }
    }

    @Override
    public void setParcelableMemoryCacheTag(@NonNull final String tag) {
        mParcelableMemoryCacheTag = tag;
    }

    @Nullable
    @Override
    public String getParcelableMemoryCacheTag() {
        return mParcelableMemoryCacheTag;
    }

    @VisibleForTesting
    public synchronized void savePreviewToFile(final File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            mBitmapPreview.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        } catch (final FileNotFoundException e) {
            LOG.error("Failed to save preview to {}", file.getAbsolutePath(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException e) {
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
    public void writeToParcel(final Parcel dest, final int flags) {
        final ParcelableMemoryCache cache = ParcelableMemoryCache.getInstance();

        ParcelableMemoryCache.Token token;
        if (mParcelableMemoryCacheTag != null) {
            token = cache.storeBitmap(mBitmapPreview, mParcelableMemoryCacheTag);
        } else {
            token = cache.storeBitmap(mBitmapPreview);
        }
        dest.writeParcelable(token, flags);

        if (mParcelableMemoryCacheTag != null) {
            token = cache.storeByteArray(mData, mParcelableMemoryCacheTag);
        } else {
            token = cache.storeByteArray(mData);
        }
        dest.writeParcelable(token, flags);

        dest.writeInt(mRotationForDisplay);
        dest.writeSerializable(mImageFormat);
        dest.writeInt(mIsImported ? 1 : 0);
        dest.writeString(mParcelableMemoryCacheTag);
    }

    public static final Parcelable.Creator<ImmutablePhoto> CREATOR =
            new Parcelable.Creator<ImmutablePhoto>() {
                @Override
                public ImmutablePhoto createFromParcel(final Parcel in) {
                    return new ImmutablePhoto(in);
                }

                @Override
                public ImmutablePhoto[] newArray(final int size) {
                    return new ImmutablePhoto[size];
                }
            };

    protected ImmutablePhoto(final Parcel in) {
        final ParcelableMemoryCache cache = ParcelableMemoryCache.getInstance();
        ParcelableMemoryCache.Token token = in.readParcelable(
                ParcelableMemoryCache.Token.class.getClassLoader());
        mBitmapPreview = cache.getBitmap(token);
        cache.removeBitmap(token);

        token = in.readParcelable(ParcelableMemoryCache.Token.class.getClassLoader());
        mData = cache.getByteArray(token);
        cache.removeByteArray(token);

        mRotationForDisplay = in.readInt();
        mImageFormat = (ImageDocument.ImageFormat) in.readSerializable();
        mIsImported = in.readInt() == 1;
        mParcelableMemoryCacheTag = in.readString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

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
