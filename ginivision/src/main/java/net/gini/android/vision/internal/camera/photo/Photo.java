package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.Document;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * @exclude
 */
public class Photo implements Parcelable {

    private static final Logger LOG = LoggerFactory.getLogger(Photo.class);

    private Bitmap mBitmapPreview;
    private byte[] mJpeg;
    private Exif.RequiredTags mRequiredTags;
    private int mRotationForDisplay = 0;
    private String mContentId = "";
    private int mRotationDelta = 0;
    private String mDeviceOrientation;
    private String mDeviceType;

    public static Photo fromJpeg(@NonNull final byte[] jpeg, final int orientation,
            final String deviceOrientation, final String deviceType) {
        return new Photo(jpeg, orientation, deviceOrientation, deviceType);
    }

    public static Photo fromDocument(@NonNull final Document document) {
        return new Photo(document);
    }

    private Photo(@NonNull byte[] jpeg, int orientation,
            final String deviceOrientation, final String deviceType) {
        mJpeg = jpeg;
        mRotationForDisplay = orientation;
        mBitmapPreview = createPreview();
        mContentId = generateUUID();
        mDeviceOrientation = deviceOrientation;
        mDeviceType = deviceType;
        readRequiredTags();
        updateExif();
    }

    private Photo(@NonNull final Document document) {
        mJpeg = document.getJpeg();
        mRotationForDisplay = document.getRotationForDisplay();
        mBitmapPreview = createPreview();
        initFieldsFromExif();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private void initFieldsFromExif() {
        if (mJpeg == null) {
            return;
        }

        readRequiredTags();

        try {
            ExifReader exifReader = ExifReader.forJpeg(mJpeg);
            String userComment = exifReader.getUserComment();
            mContentId = exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_CONTENT_ID, userComment);
            mRotationDelta = Integer.parseInt(
                    exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_ROTATION_DELTA,
                            userComment));
            mDeviceOrientation =
                    exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_DEVICE_ORIENTATION,
                            userComment);
            mDeviceType =
                    exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_DEVICE_TYPE,
                            userComment);
        } catch (ExifReaderException | NumberFormatException e) {
            LOG.error("Could not read exif User Comment", e);
        }
    }

    @Nullable
    private Bitmap createPreview() {
        if (mJpeg == null) {
            return null;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        return BitmapFactory.decodeByteArray(mJpeg, 0, mJpeg.length, options);
    }

    @Nullable
    public synchronized Bitmap getBitmapPreview() {
        return mBitmapPreview;
    }

    synchronized void updateBitmapPreview() {
        mBitmapPreview = createPreview();
    }

    @Nullable
    public synchronized byte[] getJpeg() {
        return mJpeg;
    }

    synchronized void setJpeg(@NonNull byte[] jpeg) {
        mJpeg = jpeg;
    }

    public synchronized int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    synchronized void setRotationForDisplay(int degrees) {
        // Converts input degrees to degrees between [0,360)
        mRotationForDisplay = ((degrees % 360) + 360) % 360;
    }

    synchronized void updateRotationDeltaBy(int degrees) {
        // Converts input degrees to degrees between [0,360)
        mRotationDelta = ((mRotationDelta + degrees % 360) + 360) % 360;
    }

    @VisibleForTesting
    synchronized int getRotationDelta() {
        return mRotationDelta;
    }

    @VisibleForTesting
    @NonNull
    synchronized String getContentId() {
        return mContentId;
    }

    private synchronized void readRequiredTags() {
        if (mJpeg == null) {
            return;
        }
        try {
            mRequiredTags = Exif.readRequiredTags(mJpeg);
        } catch (IOException | ImageReadException e) {
            LOG.error("Could not read exif tags", e);
        }
    }

    synchronized boolean updateExif() {
        if (mJpeg == null) {
            return false;
        }
        try {
            boolean addMake = false;
            boolean addModel = false;

            Exif.Builder exifBuilder = Exif.builder(mJpeg);

            if (mRequiredTags != null) {
                exifBuilder.setRequiredTags(mRequiredTags);
                addMake = mRequiredTags.make == null;
                addModel = mRequiredTags.model == null;
            }

            String userComment = Exif.userCommentBuilder()
                    .setAddMake(addMake)
                    .setAddModel(addModel)
                    .setContentId(mContentId)
                    .setRotationDelta(mRotationDelta)
                    .setDeviceType(mDeviceType)
                    .setDeviceOrientation(mDeviceOrientation)
                    .build();

            exifBuilder.setUserComment(userComment);
            exifBuilder.setOrientationFromDegrees(mRotationForDisplay);

            mJpeg = exifBuilder.build().writeToJpeg(mJpeg);
        } catch (ImageReadException | ImageWriteException | IOException e) {
            LOG.error("Could not add required exif tags", e);
            return false;
        }
        return true;
    }

    public synchronized PhotoEdit edit() {
        return new PhotoEdit(this);
    }

    public synchronized void saveJpegToFile(File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(mJpeg, 0, mJpeg.length);
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

        token = cache.storeJpeg(mJpeg);
        dest.writeParcelable(token, flags);

        dest.writeInt(mRotationForDisplay);
        dest.writeString(mContentId);
        dest.writeInt(mRotationDelta);
        dest.writeString(mDeviceOrientation);
        dest.writeString(mDeviceType);
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    private Photo(Parcel in) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = in.readParcelable(ImageCache.Token.class.getClassLoader());
        mBitmapPreview = cache.getBitmap(token);
        cache.removeBitmap(token);

        token = in.readParcelable(ImageCache.Token.class.getClassLoader());
        mJpeg = cache.getJpeg(token);
        cache.removeJpeg(token);

        mRotationForDisplay = in.readInt();
        mContentId = in.readString();
        mRotationDelta = in.readInt();
        mDeviceOrientation = in.readString();
        mDeviceType = in.readString();

        readRequiredTags();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Photo photo = (Photo) o;

        if (mRotationForDisplay != photo.mRotationForDisplay) return false;
        if (mRotationDelta != photo.mRotationDelta) return false;
        if (mBitmapPreview != null ? !mBitmapPreview.equals(photo.mBitmapPreview)
                : photo.mBitmapPreview != null) {
            return false;
        }
        if (!Arrays.equals(mJpeg, photo.mJpeg)) return false;
        if (mRequiredTags != null ? !mRequiredTags.equals(photo.mRequiredTags)
                : photo.mRequiredTags != null) {
            return false;
        }
        if (mContentId != null ? !mContentId.equals(photo.mContentId) : photo.mContentId != null) {
            return false;
        }
        if (mDeviceOrientation != null ? !mDeviceOrientation.equals(photo.mDeviceOrientation)
                : photo.mDeviceOrientation != null) {
            return false;
        }
        return mDeviceType != null ? mDeviceType.equals(photo.mDeviceType)
                : photo.mDeviceType == null;

    }

    @Override
    public int hashCode() {
        int result = mBitmapPreview != null ? mBitmapPreview.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(mJpeg);
        result = 31 * result + (mRequiredTags != null ? mRequiredTags.hashCode() : 0);
        result = 31 * result + mRotationForDisplay;
        result = 31 * result + (mContentId != null ? mContentId.hashCode() : 0);
        result = 31 * result + mRotationDelta;
        result = 31 * result + (mDeviceOrientation != null ? mDeviceOrientation.hashCode() : 0);
        result = 31 * result + (mDeviceType != null ? mDeviceType.hashCode() : 0);
        return result;
    }
}
