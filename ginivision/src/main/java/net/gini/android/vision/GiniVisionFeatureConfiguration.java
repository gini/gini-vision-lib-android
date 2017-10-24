package net.gini.android.vision;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 *
 */

public class GiniVisionFeatureConfiguration implements Parcelable {

    public static final Creator<GiniVisionFeatureConfiguration> CREATOR = new Creator<GiniVisionFeatureConfiguration>() {
        @Override
        public GiniVisionFeatureConfiguration createFromParcel(Parcel in) {
            return new GiniVisionFeatureConfiguration(in);
        }

        @Override
        public GiniVisionFeatureConfiguration[] newArray(int size) {
            return new GiniVisionFeatureConfiguration[size];
        }
    };

    private final DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes;
    private final boolean mOpenWithEnabled;

    private GiniVisionFeatureConfiguration(Parcel in) {
        mDocumentImportEnabledFileTypes = (DocumentImportEnabledFileTypes) in.readSerializable();
        mOpenWithEnabled = in.readByte() != 0;
    }

    private GiniVisionFeatureConfiguration(Builder builder) {
        mDocumentImportEnabledFileTypes = builder.getDocumentImportEnabledFileTypes();
        mOpenWithEnabled = builder.isOpenWithEnabled();
    }

    @NonNull
    public DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes() {
        return mDocumentImportEnabledFileTypes;
    }

    public boolean isOpenWithEnabled() {
        return mOpenWithEnabled;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel parcel, final int i) {
        parcel.writeSerializable(mDocumentImportEnabledFileTypes);
        parcel.writeByte((byte) (mOpenWithEnabled ? 1 : 0));
    }

    @NonNull
    public static Builder buildNewConfiguration() {
        return new Builder();
    }

    public static class Builder {

        private DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes =
                DocumentImportEnabledFileTypes.NONE;
        private boolean mOpenWithEnabled = false;

        private Builder() {
        }

        public GiniVisionFeatureConfiguration build() {
            return new GiniVisionFeatureConfiguration(this);
        }

        @Nullable
        private DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes() {
            return mDocumentImportEnabledFileTypes;
        }

        @NonNull
        public Builder setDocumentImportEnabledFileTypes(
                @NonNull final DocumentImportEnabledFileTypes documentImportEnabledFileTypes) {
            mDocumentImportEnabledFileTypes = documentImportEnabledFileTypes;
            return this;
        }

        private boolean isOpenWithEnabled() {
            return mOpenWithEnabled;
        }

        @NonNull
        public Builder setOpenWithEnabled(final boolean openWithEnabled) {
            mOpenWithEnabled = openWithEnabled;
            return this;
        }
    }

}
