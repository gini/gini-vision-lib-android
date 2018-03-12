package net.gini.android.vision.document;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alpar Szotyori on 19.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class MultiPageDocument extends GiniVisionDocument {

    public static final Creator<MultiPageDocument> CREATOR =
            new Creator<MultiPageDocument>() {
                @Override
                public MultiPageDocument createFromParcel(final Parcel in) {
                    return new MultiPageDocument(in);
                }

                @Override
                public MultiPageDocument[] newArray(final int size) {
                    return new MultiPageDocument[size];
                }
            };

    private final List<ImageDocument> mImageDocuments;

    public MultiPageDocument(final boolean isImported) {
        super(Type.MULTI_PAGE, null, null, null, true, isImported);
        mImageDocuments = new ArrayList<>();
    }

    public MultiPageDocument(@NonNull final ImageDocument imageDocument, final boolean isImported) {
        super(Type.MULTI_PAGE, null, null, null, true, isImported);
        mImageDocuments = new ArrayList<>();
        mImageDocuments.add(imageDocument);
    }

    private MultiPageDocument(final Parcel in) {
        super(in);
        final int size = in.readInt();
        mImageDocuments = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            mImageDocuments.add((ImageDocument)
                    in.readParcelable(ImageDocument.class.getClassLoader()));
        }
    }

    public void addImageDocument(@NonNull final ImageDocument document) {
        mImageDocuments.add(document);
    }

    public List<ImageDocument> getImageDocuments() {
        return mImageDocuments;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mImageDocuments.size());
        for (final ImageDocument imageDocument : mImageDocuments) {
            dest.writeParcelable(imageDocument, flags);
        }
    }

    public void removeImageDocumentAtPosition(final int position) {
        mImageDocuments.remove(position);
    }

    @Override
    public void loadData(@NonNull final Context context,
            @NonNull final AsyncCallback<byte[]> callback) {
        loadImageDocuments(0, context, callback);
    }

    private void loadImageDocuments(final int currentIndex,
            @NonNull final Context context,
            @NonNull final AsyncCallback<byte[]> callback) {
        if (currentIndex < mImageDocuments.size()) {
            final ImageDocument imageDocument = mImageDocuments.get(currentIndex);
            imageDocument.loadData(context, new AsyncCallback<byte[]>() {
                @Override
                public void onSuccess(final byte[] result) {
                    loadImageDocuments(currentIndex + 1, context, callback);
                }

                @Override
                public void onError(final Exception exception) {
                    loadImageDocuments(currentIndex + 1, context, callback);
                }
            });
        } else {
            callback.onSuccess(null);
        }
    }
}
