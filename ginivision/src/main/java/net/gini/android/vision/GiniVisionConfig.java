package net.gini.android.vision;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public final class GiniVisionConfig implements Parcelable {

    public enum DocumentImportFileTypes {
        PDF,
        PDF_AND_IMAGES
    }

    private DocumentImportFileTypes mDocumentImportFileTypes;

    public GiniVisionConfig() {
    }

    public void enableDocumentImport(@NonNull final DocumentImportFileTypes documentImportFileTypes) {
        mDocumentImportFileTypes = documentImportFileTypes;
    }

    public boolean isDocumentImportEnabled() {
        return mDocumentImportFileTypes != null;
    }

    @Nullable
    public DocumentImportFileTypes getDocumentImportFileTypes() {
        return mDocumentImportFileTypes;
    }

    private GiniVisionConfig(Parcel in) {
        mDocumentImportFileTypes = (DocumentImportFileTypes) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mDocumentImportFileTypes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GiniVisionConfig> CREATOR = new Creator<GiniVisionConfig>() {
        @Override
        public GiniVisionConfig createFromParcel(Parcel in) {
            return new GiniVisionConfig(in);
        }

        @Override
        public GiniVisionConfig[] newArray(int size) {
            return new GiniVisionConfig[size];
        }
    };
}
