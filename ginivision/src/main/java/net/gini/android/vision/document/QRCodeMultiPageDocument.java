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
 * A document consisting of multiple QRCode documents.
 */
public class QRCodeMultiPageDocument extends
        GiniVisionMultiPageDocument<QRCodeDocument, GiniVisionDocumentError> {

    public static final Creator<QRCodeMultiPageDocument> CREATOR =
            new Creator<QRCodeMultiPageDocument>() {
                @Override
                public QRCodeMultiPageDocument createFromParcel(final Parcel in) {
                    return new QRCodeMultiPageDocument(in);
                }

                @Override
                public QRCodeMultiPageDocument[] newArray(final int size) {
                    return new QRCodeMultiPageDocument[size];
                }
            };

    public QRCodeMultiPageDocument(@NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        super(Type.QR_CODE_MULTI_PAGE, source, importMethod,
                MimeType.APPLICATION_JSON.asString(), false);
    }

    public QRCodeMultiPageDocument(
            @NonNull final QRCodeDocument document) {
        super(Type.QR_CODE_MULTI_PAGE, MimeType.APPLICATION_JSON.asString(), document);
    }

    private QRCodeMultiPageDocument(final Parcel in) {
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
