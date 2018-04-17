package net.gini.android.vision.document;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 17.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class PdfMultiPageDocument extends GiniVisionMultiPageDocument<PdfDocument, GiniVisionDocumentError> {

    public static final Creator<PdfMultiPageDocument> CREATOR =
            new Creator<PdfMultiPageDocument>() {
                @Override
                public PdfMultiPageDocument createFromParcel(final Parcel in) {
                    return new PdfMultiPageDocument(in);
                }

                @Override
                public PdfMultiPageDocument[] newArray(final int size) {
                    return new PdfMultiPageDocument[size];
                }
            };

    public PdfMultiPageDocument(final boolean isImported) {
        super(Type.QR_CODE_MULTI_PAGE, isImported);
    }

    public PdfMultiPageDocument(
            @NonNull final PdfDocument document,
            final boolean isImported) {
        super(Type.QR_CODE_MULTI_PAGE, document, isImported);
    }

    private PdfMultiPageDocument(final Parcel in) {
        super(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
    }
}
