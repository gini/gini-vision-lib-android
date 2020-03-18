package net.gini.android.vision.document;

import android.os.Parcel;

import net.gini.android.vision.internal.util.MimeType;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 14.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * A document consisting of multiple image documents.
 */
public class ImageMultiPageDocument extends
        GiniVisionMultiPageDocument<ImageDocument, GiniVisionDocumentError> {

    public static final Creator<ImageMultiPageDocument> CREATOR =
            new Creator<ImageMultiPageDocument>() {
                @Override
                public ImageMultiPageDocument createFromParcel(final Parcel in) {
                    return new ImageMultiPageDocument(in);
                }

                @Override
                public ImageMultiPageDocument[] newArray(final int size) {
                    return new ImageMultiPageDocument[size];
                }
            };

    public ImageMultiPageDocument(@NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        super(Type.IMAGE_MULTI_PAGE, source, importMethod, MimeType.IMAGE_WILDCARD.asString(),
                true);
    }

    public ImageMultiPageDocument(
            @NonNull final ImageDocument document) {
        super(Type.IMAGE_MULTI_PAGE, MimeType.IMAGE_WILDCARD.asString(), document);
    }

    private ImageMultiPageDocument(final Parcel in) {
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
