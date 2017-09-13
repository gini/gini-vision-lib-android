package net.gini.android.vision;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.camera.photo.ImageCache;

public class GiniVisionDocument implements Parcelable {

    public enum Type {
        IMAGE,
        PDF
    }

    private final Type mType;
    private final byte[] mData;


    protected GiniVisionDocument(@NonNull final Type type, @NonNull byte[] jpeg) {
        mType = type;
        mData = jpeg;
    }

    public Type getType() {
        return mType;
    }

    public byte[] getData() {
        return mData;
    }

    /**
     * @exclude
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ImageCache cache = ImageCache.getInstance();
        ImageCache.Token token = cache.storeJpeg(mData);
        dest.writeParcelable(token, flags);

        dest.writeSerializable(mType);
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
    public static final Creator<GiniVisionDocument> CREATOR = new Creator<GiniVisionDocument>() {
        @Override
        public GiniVisionDocument createFromParcel(Parcel in) {
            return new GiniVisionDocument(in);
        }

        @Override
        public GiniVisionDocument[] newArray(int size) {
            return new GiniVisionDocument[size];
        }
    };

    protected GiniVisionDocument(Parcel in) {
        ImageCache cache = ImageCache.getInstance();
        ImageCache.Token token = in.readParcelable(ImageCache.Token.class.getClassLoader());
        mData = cache.getJpeg(token);
        cache.removeJpeg(token);

        mType = (Type) in.readSerializable();
    }

    @Override
    public String toString() {
        // TODO: to string
        return super.toString();
    }
}
