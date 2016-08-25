package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.Document;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @exclude
 */
public class Photo implements Parcelable {

    private Bitmap mBitmapPreview;
    private byte[] mJpeg;
    private Exif.RequiredTags mRequiredTags;
    private int mRotationForDisplay = 0;

    private PhotoEdit mEditor;

    public static Photo fromJpeg(@NonNull byte[] jpeg, int orientation) {
        Photo photo = new Photo();
        photo.setJpeg(jpeg);
        photo.setBitmapPreview(createPreview(jpeg));
        photo.setRotationForDisplay(orientation);
        photo.setRequiredTags();
        photo.updateExif();
        return photo;
    }

    public static Photo fromDocument(@NonNull Document document) {
        return Photo.fromJpeg(document.getJpeg(), document.getRotationForDisplay());
    }

    public static Bitmap createPreview(byte[] jpeg) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        return BitmapFactory.decodeByteArray(jpeg, 0, jpeg.length, options);
    }

    public Photo() {
    }

    @Nullable
    public synchronized Bitmap getBitmapPreview() {
        return mBitmapPreview;
    }

    public synchronized void setBitmapPreview(@NonNull Bitmap bitmap) {
        mBitmapPreview = bitmap;
    }

    @Nullable
    public synchronized byte[] getJpeg() {
        return mJpeg;
    }

    public synchronized void setJpeg(@NonNull byte[] jpeg) {
        mJpeg = jpeg;
    }

    public synchronized int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    public synchronized void setRotationForDisplay(int degrees) {
        // Converts input degrees to degrees between [0,360]
        mRotationForDisplay = ((degrees % 360) + 360) % 360;
    }

    public synchronized void addRotationForDisplay(int degrees) {
        // Converts input degrees to degrees between [0,360]
        mRotationForDisplay = ((mRotationForDisplay + degrees % 360) + 360) % 360;
    }

    private synchronized void setRequiredTags() {
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

            Exif.Builder exifBuilder = Exif.builder();

            if (mRequiredTags != null) {
                exifBuilder.setRequiredTags(mRequiredTags);
                addMake = mRequiredTags.make == null;
                addModel = mRequiredTags.model == null;
            }

            exifBuilder.setUserComment(addMake, addModel);
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

    @VisibleForTesting
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
                    // TODO log: mLogger.error("Closing FileOutputStream failed for file {}", file.getAbsolutePath(), e);
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
            // TODO log: mLogger.error("Saving preview failed to file {}", file.getAbsolutePath(), e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // TODO log: mLogger.error("Closing FileOutputStream failed for file {}", file.getAbsolutePath(), e);
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
    }
}
