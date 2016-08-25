package net.gini.android.vision;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.camera.photo.ImageCache;
import net.gini.android.vision.internal.camera.photo.Photo;


/**
 * <p>
 * This class is the container for transferring the image of a document as a JPEG between the client application and
 * the
 * Gini Vision Library and between the Fragments of the Gini Vision Library.
 * </p>
 *
 * <p>
 * Due to the size limitations of the {@link android.os.Bundle}, the JPEG has to be stored in a memory cache when
 * parceling and read from the cache when unparceling.
 * </p>
 *
 * <p>
 * <b>Warning:</b> Always retrieve the {@link Document} extras from a Bundle to force unparceling and removing of the
 * reference to
 * the JPEG byte array from the memory cache. Failing to do so will lead to memory leaks.
 * </p>
 */
public class Document implements Parcelable {

    private final byte[] mJpeg;
    private final int mRotationForDisplay;

    /**
     * @exclude
     */
    public static Document fromPhoto(@NonNull Photo photo) {
        return new Document(photo.getJpeg(), photo.getRotationForDisplay());
    }

    private Document(@NonNull byte[] jpeg, int rotationForDisplay) {
        mJpeg = jpeg;
        mRotationForDisplay = rotationForDisplay;
    }

    /**
     * <p>
     * The image of a document as a JPEG.
     *</p>
     * @return a byte array containg a JPEG
     */
    @NonNull
    public byte[] getJpeg() {
        return mJpeg;
    }

    /**
     * <p>
     * The amount of clockwise rotation needed to display the image in the correct orientation.
     * </p>
     * <p>
     * Degrees are positive and multiples of 90.
     * </p>
     * @return degrees by which the image should be rotated clockwise before displaying
     */
    public int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    /**
     * @exclude
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = cache.storeJpeg(mJpeg);
        dest.writeParcelable(token, flags);

        dest.writeInt(mRotationForDisplay);
    }

    /**
     * @exclude
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @exclude
     */
    public static final Creator<Document> CREATOR = new Creator<Document>() {
        @Override
        public Document createFromParcel(Parcel in) {
            return new Document(in);
        }

        @Override
        public Document[] newArray(int size) {
            return new Document[size];
        }
    };

    private Document(Parcel in) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = in.readParcelable(ImageCache.Token.class.getClassLoader());
        mJpeg = cache.getJpeg(token);
        cache.removeJpeg(token);

        mRotationForDisplay = in.readInt();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Document{");
        sb.append("mJpeg=[bytes]");
        sb.append(", mRotationForDisplay=").append(mRotationForDisplay);
        sb.append('}');
        return sb.toString();
    }
}
