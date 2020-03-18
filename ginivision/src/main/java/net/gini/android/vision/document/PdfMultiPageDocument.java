package net.gini.android.vision.document;

import android.os.Parcel;

import net.gini.android.vision.internal.util.MimeType;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 17.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * A document consisting of multiple pdf documents.
 */
public class PdfMultiPageDocument extends
        GiniVisionMultiPageDocument<PdfDocument, GiniVisionDocumentError> {

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

    public PdfMultiPageDocument(@NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        super(Type.PDF_MULTI_PAGE, source, importMethod,
                MimeType.APPLICATION_PDF.asString(), false);
    }

    public PdfMultiPageDocument(
            @NonNull final PdfDocument document) {
        super(Type.PDF_MULTI_PAGE, MimeType.APPLICATION_PDF.asString(), document);
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
