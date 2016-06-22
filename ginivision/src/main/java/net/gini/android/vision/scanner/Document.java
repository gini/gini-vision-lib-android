package net.gini.android.vision.scanner;

import android.os.Parcel;
import android.os.Parcelable;

import net.gini.android.vision.scanner.photo.ImageCache;
import net.gini.android.vision.scanner.photo.Photo;

public class Document implements Parcelable {

    private final byte[] mJpeg;

    /**
     * @exclude
     * @param photo
     * @return
     */
    public static Document fromPhoto(Photo photo) {
        return new Document(photo.getJpeg());
    }

    private Document(byte[] jpeg) {
        mJpeg = jpeg;
    }

    public byte[] getJpeg() {
        return mJpeg;
    }

    /**
     * @exclude
     * @param dest
     * @param flags
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = cache.storeJpeg(mJpeg);
        dest.writeParcelable(token, flags);
    }

    /**
     * @exclude
     * @return
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
