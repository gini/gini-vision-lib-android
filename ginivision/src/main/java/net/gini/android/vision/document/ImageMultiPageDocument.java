package net.gini.android.vision.document;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 14.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class ImageMultiPageDocument extends GiniVisionMultiPageDocument<ImageDocument, GiniVisionDocumentError> {

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

    public ImageMultiPageDocument(final boolean isImported) {
        super(Type.IMAGE_MULTI_PAGE, isImported);
    }

    public ImageMultiPageDocument(
            @NonNull final ImageDocument document) {
        super(Type.IMAGE_MULTI_PAGE, document);
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
