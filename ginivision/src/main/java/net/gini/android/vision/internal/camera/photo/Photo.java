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

    private Bitmap mBitmapPreview;
    private byte[] mJpeg;
    private Exif.RequiredTags mRequiredTags;
    private int mRotationForDisplay = 0;
    private String mUUID = "";
    private int mRotationDelta = 0;

    private PhotoEdit mEditor;

    public static Photo fromJpeg(@NonNull final byte[] jpeg, final int orientation) {
        return new Photo(jpeg, orientation);
    }

    public static Photo fromDocument(@NonNull final Document document) {
        return new Photo(document);
    }

    private Photo(@NonNull byte[] jpeg, int orientation) {
        mJpeg = jpeg;
        mRotationForDisplay = orientation;
        mBitmapPreview = createPreview();
        mUUID = generateUUID();
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
            ExifReader exifReader = new ExifReader(mJpeg);
            String userComment = exifReader.getUserComment();
            mUUID = exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_UUID, userComment);
            mRotationDelta = Integer.parseInt(
                    exifReader.getValueForKeyFromUserComment(Exif.USER_COMMENT_ROTATION_DELTA,
                            userComment));
        } catch (ExifReaderException | NumberFormatException e) {
            // TODO log
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
    int getRotationDelta() {
        return mRotationDelta;
    }

    @VisibleForTesting
    @NonNull
    String getUUID() {
        return mUUID;
    }

    private synchronized void readRequiredTags() {
        if (mJpeg == null) {
            return;
        }
        try {
            mRequiredTags = Exif.readRequiredTags(mJpeg);
        } catch (IOException | ImageReadException e) {
            // TODO log: mLogger.error("Could not read required exif tags", e);
        }
    }

    public boolean updateExif() {
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
                    .setUUID(mUUID)
                    .setRotationDelta(mRotationDelta)
                    .build();

            exifBuilder.setUserComment(userComment);
            exifBuilder.setOrientationFromDegrees(mRotationForDisplay);

            mJpeg = exifBuilder.build().writeToJpeg(mJpeg);
        } catch (ImageReadException | ImageWriteException | IOException e) {
            // TODO log: mLogger.error("Could not add required exif tags", e);
            return false;
        }
        return true;
    }

    public synchronized PhotoEdit edit() {
        if (mEditor == null) {
            mEditor = new PhotoEdit(this);
        }
        return mEditor;
    }

    public synchronized void saveJpegToFile(File file) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(mJpeg, 0, mJpeg.length);
        } catch (IOException e) {
            // TODO log: mLogger.error("Saving jpeg failed to file {}", file.getAbsolutePath(), e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    // TODO log: mLogger.error("Closing FileOutputStream failed for file {}",
                    // file.getAbsolutePath(), e);
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
            // TODO log: mLogger.error("Saving preview failed to file {}", file.getAbsolutePath()
            // , e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // TODO log: mLogger.error("Closing FileOutputStream failed for file {}",
                    // file.getAbsolutePath(), e);
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
        dest.writeString(mUUID);
        dest.writeInt(mRotationDelta);
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
        mUUID = in.readString();
        mRotationDelta = in.readInt();
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
        if (mUUID != null ? !mUUID.equals(photo.mUUID) : photo.mUUID != null) return false;
        return mEditor != null ? mEditor.equals(photo.mEditor) : photo.mEditor == null;

    }

    @Override
    public int hashCode() {
        int result = mBitmapPreview != null ? mBitmapPreview.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(mJpeg);
        result = 31 * result + (mRequiredTags != null ? mRequiredTags.hashCode() : 0);
        result = 31 * result + mRotationForDisplay;
        result = 31 * result + (mUUID != null ? mUUID.hashCode() : 0);
        result = 31 * result + mRotationDelta;
        result = 31 * result + (mEditor != null ? mEditor.hashCode() : 0);
        return result;
    }
}
