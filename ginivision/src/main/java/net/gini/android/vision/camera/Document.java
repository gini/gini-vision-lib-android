package net.gini.android.vision.camera;

import android.os.Parcel;
import android.os.Parcelable;

import net.gini.android.vision.camera.photo.ImageCache;
import net.gini.android.vision.camera.photo.Photo;

/**
 * <p>
 * This class is the container for transferring the image of a document as a JPEG between the client application and the
 * Gini Vision Lib and between the Fragments of the Gini Vision Lib.
 * </p>
 *
 * <p>
 * Due to the size limitations of the {@link android.os.Bundle}, the JPEG has to be stored in a memory cache when
 * parceling and read from the cache when unparceling.
 * </p>
 *
 * <p>
 * <b>Note:</b> Always retrieve the {@link Document} extras from a Bundle to force unparceling and removing of the reference to
 * the JPEG byte array from the memory cache. Failing to do so will lead to memory leaks.
 * </p>
 */
public class Document implements Parcelable {

    private final byte[] mJpeg;

    /**
     * @exclude
     */
    public static Document fromPhoto(Photo photo) {
        return new Document(photo.getJpeg());
    }

    private Document(byte[] jpeg) {
        mJpeg = jpeg;
    }

    /**
     * The image of a document as a JPEG.
     *
     * @return a byte array containg a JPEG
     */
    public byte[] getJpeg() {
        return mJpeg;
    }

    /**
     * @exclude
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = cache.storeJpeg(mJpeg);
        dest.writeParcelable(token, flags);
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
    }
}
