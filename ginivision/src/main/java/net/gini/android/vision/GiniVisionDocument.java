package net.gini.android.vision;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.camera.photo.ImageCache;

import java.util.Arrays;

public class GiniVisionDocument implements Document {

    private final Type mType;
    private byte[] mData;
    private final boolean mIsReviewable;
    private final boolean mIsImported;
    private final Intent mIntent;

    protected GiniVisionDocument(@NonNull final Type type,
            @Nullable final byte[] data,
            @Nullable final Intent intent,
            final boolean isReviewable,
            final boolean isImported) {
        mType = type;
        mData = data;
        mIntent = intent;
        mIsReviewable = isReviewable;
        mIsImported = isImported;
    }

    @Override
    public Type getType() {
        return mType;
    }

    @Nullable
    @Override
    public byte[] getData() {
        return mData;
    }

    protected void setData(final byte[] data) {
        mData = data;
    }

    @Nullable
    @Override
    public Intent getIntent() {
        return mIntent;
    }

    @Override
    public boolean isImported() {
        return mIsImported;
    }

    @Override
    public boolean isReviewable() {
        return mIsReviewable;
    }

    /**
     * @exclude
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mData != null) {
            ImageCache cache = ImageCache.getInstance();
            ImageCache.Token token = cache.storeJpeg(mData);
            dest.writeParcelable(token, flags);
        } else {
            dest.writeParcelable(null, flags);
        }

        dest.writeSerializable(mType);
        dest.writeParcelable(mIntent, flags);
        dest.writeInt(mIsReviewable ? 1 : 0);
        dest.writeInt(mIsImported ? 1 : 0);
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
        if (token != null) {
            mData = cache.getJpeg(token);
            cache.removeJpeg(token);
        } else {
            mData = null;
        }

        mType = (Type) in.readSerializable();
        mIntent = in.readParcelable(Uri.class.getClassLoader());
        mIsReviewable = in.readInt() == 1;
        mIsImported = in.readInt() == 1;
    }

    @Override
    public String toString() {
        return "GiniVisionDocument{" +
                "mType=" + mType +
                ", mData=" + Arrays.toString(mData) +
                ", mIsReviewable=" + mIsReviewable +
                ", mIsImported=" + mIsImported +
                ", mIntent=" + mIntent +
                '}';
    }

    @NonNull
    @Override
    public byte[] getJpeg() {
        final byte[] data = getData();
        return data != null ? data : new byte[]{};
    }

    @Override
    public int getRotationForDisplay() {
        return 0;
    }
}
