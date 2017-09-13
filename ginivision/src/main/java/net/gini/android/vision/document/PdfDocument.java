package net.gini.android.vision.document;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionDocument;

public class PdfDocument extends GiniVisionDocument implements Parcelable {

    protected PdfDocument(@NonNull final byte[] data) {
        super(Type.PDF, data);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
    }

    public static final Creator<PdfDocument> CREATOR = new Creator<PdfDocument>() {
        @Override
        public PdfDocument createFromParcel(Parcel in) {
            return new PdfDocument(in);
        }

        @Override
        public PdfDocument[] newArray(int size) {
            return new PdfDocument[size];
        }
    };

    private PdfDocument(Parcel in) {
        super(in);
    }
}
