package net.gini.android.vision.scanner;

import android.os.Parcel;
import android.os.Parcelable;

import net.gini.android.vision.scanner.photo.ImageCache;
import net.gini.android.vision.scanner.photo.Photo;

public class Document implements Parcelable {

    private final byte[] mJpeg;

    public static Document fromPhoto(Photo photo) {
        return new Document(photo.getJpeg());
    }

    public Document(byte[] jpeg) {
        mJpeg = jpeg;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = cache.storeJpeg(mJpeg);
        dest.writeParcelable(token, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

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

    protected Document(Parcel in) {
        ImageCache cache = ImageCache.getInstance();

        ImageCache.Token token = in.readParcelable(ImageCache.Token.class.getClassLoader());
        mJpeg = cache.getJpeg(token);
        cache.removeJpeg(token);
    }
}
