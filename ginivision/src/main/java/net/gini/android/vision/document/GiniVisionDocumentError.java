package net.gini.android.vision.document;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 15.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionDocumentError implements Parcelable {

    private final String mMessage;

    public GiniVisionDocumentError(@NonNull final String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }

    GiniVisionDocumentError(final Parcel in) {
        mMessage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(mMessage);
    }

    public static final Creator<GiniVisionDocumentError> CREATOR =
            new Creator<GiniVisionDocumentError>() {
                @Override
                public GiniVisionDocumentError createFromParcel(final Parcel in) {
                    return new GiniVisionDocumentError(in);
                }

                @Override
                public GiniVisionDocumentError[] newArray(final int size) {
                    return new GiniVisionDocumentError[size];
                }
            };
}
