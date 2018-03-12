package net.gini.android.vision.network.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class GiniVisionExtraction implements Parcelable {

    public static final Creator<GiniVisionExtraction> CREATOR = new Creator<GiniVisionExtraction>() {

        @Override
        public GiniVisionExtraction createFromParcel(final Parcel in) {
            return new GiniVisionExtraction(in);
        }

        @Override
        public GiniVisionExtraction[] newArray(final int size) {
            return new GiniVisionExtraction[size];
        }

    };

    private final String mEntity;
    private String mValue;
    private GiniVisionBox mGiniVisionBox;
    private boolean mIsDirty;

    /**
     * Value object for an extraction from the Gini API.
     *
     * @param value         The extraction's value. Changing this value marks the extraction as dirty.
     * @param entity        The extraction's entity.
     * @param box           Optional the box where the extraction is found. Only available on some extractions. Changing
     *                      this value marks the extraction as dirty.
     */
    public GiniVisionExtraction(@NonNull final String value, @NonNull final String entity,
            @Nullable final GiniVisionBox box) {
        mValue = value;
        mEntity = entity;
        mGiniVisionBox = box;
        mIsDirty = false;
    }

    protected GiniVisionExtraction(final Parcel in) {
        mEntity = in.readString();
        mValue = in.readString();
        mGiniVisionBox = in.readParcelable(GiniVisionBox.class.getClassLoader());
        mIsDirty = in.readInt() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mEntity);
        dest.writeString(mValue);
        dest.writeParcelable(mGiniVisionBox, flags);
        dest.writeInt(mIsDirty ? 1 : 0);
    }

    @NonNull
    public synchronized String getValue() {
        return mValue;
    }


    public synchronized void setValue(final @NonNull String newValue) {
        mValue = newValue;
        mIsDirty = true;
    }

    @NonNull
    public synchronized String getEntity() {
        return mEntity;
    }

    @Nullable
    public synchronized GiniVisionBox getBox() {
        return mGiniVisionBox;
    }

    public synchronized void setBox(@Nullable final GiniVisionBox newBox) {
        mGiniVisionBox = newBox;
        mIsDirty = true;
    }

    public synchronized boolean isDirty() {
        return mIsDirty;
    }

    public void setIsDirty(final boolean isDirty) {
        mIsDirty = isDirty;
    }
}
