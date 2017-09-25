package net.gini.android.vision.internal.camera.photo;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.document.ImageDocument;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * @exclude
 */
class MutablePhoto extends ImmutablePhoto implements Parcelable {

    private static final Logger LOG = LoggerFactory.getLogger(MutablePhoto.class);

    private Exif.RequiredTags mRequiredTags;
    private String mContentId = "";
    private int mRotationDelta = 0;
    private String mDeviceOrientation;
    private String mDeviceType;
    private ImageDocument mImageDocument;

    MutablePhoto(@NonNull byte[] data, int orientation,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull ImageDocument.ImageFormat format, final boolean isImported) {
        super(data, orientation, format, isImported);
        mContentId = generateUUID();
        mDeviceOrientation = deviceOrientation;
        mDeviceType = deviceType;
        readRequiredTags();
        updateExif();
    }

    MutablePhoto(@NonNull final ImageDocument document) {
        super(document);
        mImageDocument = document;
        initFieldsFromExif();
    }

    private String generateUUID() {
        return UUID.randomUUID().toString();
    }

    private void initFieldsFromExif() {
        final byte[] data = getData();
        if (data == null) {
            return;
        }

        readRequiredTags();

        try {
            ExifReader exifReader = ExifReader.forJpeg(data);
            String userComment = exifReader.getUserComment();
            mContentId = exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_CONTENT_ID,
                    userComment);
            if (mContentId == null) {
                mContentId = generateUUID();
            }
            String rotationDelta = exifReader.getValueForKeyFromUserComment(
                    Exif.USER_COMMENT_ROTATION_DELTA,
                    userComment);
            if (rotationDelta != null) {
                mRotationDelta = Integer.parseInt(rotationDelta);
            }
            mDeviceOrientation =
                    exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_DEVICE_ORIENTATION,
                            userComment);
            if (mDeviceOrientation == null && mImageDocument != null) {
                mDeviceOrientation = mImageDocument.getDeviceOrientation();
            }
            mDeviceType =
                    exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_DEVICE_TYPE,
                            userComment);
            if (mDeviceType == null && mImageDocument != null) {
                mDeviceType = mImageDocument.getDeviceType();
            }
            if (mImageDocument != null && mImageDocument.isImported()) {
                mRotationForDisplay = exifReader.getOrientationAsDegrees();
            }
        } catch (ExifReaderException | NumberFormatException e) {
            LOG.error("Could not read exif User Comment", e);
        }
    }

    @Override
    public synchronized void updateBitmapPreview() {
        super.mBitmapPreview = createPreview();
    }

    @Override
    public synchronized int getRotationForDisplay() {
        return super.mRotationForDisplay;
    }

    @Override
    public synchronized void setRotationForDisplay(int degrees) {
        // Converts input degrees to degrees between [0,360)
        super.mRotationForDisplay = ((degrees % 360) + 360) % 360;
    }

    @Override
    public synchronized void updateRotationDeltaBy(int degrees) {
        // Converts input degrees to degrees between [0,360)
        mRotationDelta = ((mRotationDelta + degrees % 360) + 360) % 360;
    }

    @Override
    public void setData(final byte[] data) {
        super.mData = data;
    }

    @VisibleForTesting
    synchronized int getRotationDelta() {
        return mRotationDelta;
    }

    @Override
    public String getDeviceOrientation() {
        return mDeviceOrientation;
    }

    @Override
    public String getDeviceType() {
        return mDeviceType;
    }

    @VisibleForTesting
    @NonNull
    synchronized String getContentId() {
        return mContentId;
    }

    private synchronized void readRequiredTags() {
        final byte[] data = getData();
        if (data == null) {
            return;
        }
        try {
            mRequiredTags = Exif.readRequiredTags(data);
        } catch (IOException | ImageReadException e) {
            LOG.error("Could not read exif tags", e);
        }
    }

    @Override
    public synchronized void updateExif() {
        final byte[] data = getData();
        if (data == null) {
            return;
        }
        try {
            boolean addMake = false;
            boolean addModel = false;

            Exif.Builder exifBuilder = Exif.builder(data);

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
            exifBuilder.setOrientationFromDegrees(super.mRotationForDisplay);

            byte[] jpeg = exifBuilder.build().writeToJpeg(data);
            setData(jpeg);
        } catch (ImageReadException | ImageWriteException | IOException e) {
            LOG.error("Could not add required exif tags", e);
        }
    }

    @Override
    public synchronized PhotoEdit edit() {
        return new PhotoEdit(this);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mContentId);
        dest.writeInt(mRotationDelta);
        dest.writeString(mDeviceOrientation);
        dest.writeString(mDeviceType);
    }

    public static final Creator<MutablePhoto> CREATOR = new Creator<MutablePhoto>() {
        @Override
        public MutablePhoto createFromParcel(Parcel in) {
            return new MutablePhoto(in);
        }

        @Override
        public MutablePhoto[] newArray(int size) {
            return new MutablePhoto[size];
        }
    };

    private MutablePhoto(Parcel in) {
        super(in);
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
        if (!super.equals(o)) return false;

        final MutablePhoto that = (MutablePhoto) o;

        if (mRotationDelta != that.mRotationDelta) return false;
        if (mRequiredTags != null ? !mRequiredTags.equals(that.mRequiredTags)
                : that.mRequiredTags != null) {
            return false;
        }
        if (mContentId != null ? !mContentId.equals(that.mContentId) : that.mContentId != null) {
            return false;
        }
        if (mDeviceOrientation != null ? !mDeviceOrientation.equals(that.mDeviceOrientation)
                : that.mDeviceOrientation != null) {
            return false;
        }
        return mDeviceType != null ? mDeviceType.equals(that.mDeviceType)
                : that.mDeviceType == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mRequiredTags != null ? mRequiredTags.hashCode() : 0);
        result = 31 * result + (mContentId != null ? mContentId.hashCode() : 0);
        result = 31 * result + mRotationDelta;
        result = 31 * result + (mDeviceOrientation != null ? mDeviceOrientation.hashCode() : 0);
        result = 31 * result + (mDeviceType != null ? mDeviceType.hashCode() : 0);
        return result;
    }
}
