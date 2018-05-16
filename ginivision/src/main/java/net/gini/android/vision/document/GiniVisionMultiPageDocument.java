package net.gini.android.vision.document;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.AsyncCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 19.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionMultiPageDocument<T extends GiniVisionDocument, E extends GiniVisionDocumentError> extends
        GiniVisionDocument {

    public static final Creator<GiniVisionMultiPageDocument> CREATOR =
            new Creator<GiniVisionMultiPageDocument>() {
                @Override
                public GiniVisionMultiPageDocument createFromParcel(final Parcel in) {
                    return new GiniVisionMultiPageDocument(in);
                }

                @Override
                public GiniVisionMultiPageDocument[] newArray(final int size) {
                    return new GiniVisionMultiPageDocument[size];
                }
            };

    private final List<T> mDocuments = new ArrayList<>();
    private final Map<T, E> mDocumentErrorMap = new HashMap<>();

    public GiniVisionMultiPageDocument(@NonNull final Type type, @NonNull final Source source,
            @NonNull final ImportMethod importMethod, @NonNull final String mimeType,
            @NonNull final boolean isReviewable) {
        super(type, source, importMethod, mimeType, null, null, null, isReviewable);
    }

    public GiniVisionMultiPageDocument(@NonNull final Type type, @NonNull final String mimeType,
            @NonNull final T document) {
        super(type, document.getSource(), document.getImportMethod(), mimeType, null, null, null,
                document.isReviewable());
        mDocuments.add(document);
    }

    GiniVisionMultiPageDocument(final Parcel in) {
        super(in);
        final int size = in.readInt();
        for (int i = 0; i < size; i++) {
            //noinspection unchecked
            mDocuments.add((T) in.readParcelable(getClass().getClassLoader()));
        }
        final int mapSize = in.readInt();
        for (int i = 0; i < mapSize; i++) {
            //noinspection unchecked
            mDocumentErrorMap.put(
                    (T) in.readParcelable(getClass().getClassLoader()),
                    (E) in.readParcelable(getClass().getClassLoader())
            );
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mDocuments.size());
        for (final T document : mDocuments) {
            dest.writeParcelable(document, flags);
        }
        dest.writeInt(mDocumentErrorMap.size());
        for (final Map.Entry<T, E> entry : mDocumentErrorMap.entrySet()) {
            dest.writeParcelable(entry.getKey(), flags);
            dest.writeParcelable(entry.getValue(), flags);
        }
    }

    @Override
    public void loadData(@NonNull final Context context,
            @NonNull final AsyncCallback<byte[]> callback) {
        loadImageDocuments(0, context, callback);
    }

    public void addDocument(@NonNull final T document) {
        mDocuments.add(document);
    }

    public void unloadAllDocumentData() {
        for (final T document : mDocuments) {
            document.unloadData();
        }
    }

    public void setErrorForDocument(@NonNull final T document, @NonNull final E error) {
        if (mDocuments.contains(document)) {
            mDocumentErrorMap.put(document, error);
        } else {
            throw new IllegalStateException(
                    "Document not found. Did you add it with addDocument()?");
        }
    }

    @Nullable
    public E getErrorForDocument(@NonNull final T document) {
        return mDocumentErrorMap.get(document);
    }

    public void removeErrorForDocument(@NonNull final T document) {
        mDocumentErrorMap.remove(document);
    }

    public List<T> getDocuments() {
        return mDocuments;
    }

    public void removeImageDocumentAtPosition(final int position) {
        mDocuments.remove(position);
    }

    public void addDocuments(@NonNull final List<T> documents) {
        mDocuments.addAll(documents);
    }

    public boolean hasDocumentError(final ImageDocument imageDocument) {
        return mDocumentErrorMap.containsKey(imageDocument);
    }

    private void loadImageDocuments(final int currentIndex,
            @NonNull final Context context,
            @NonNull final AsyncCallback<byte[]> callback) {
        if (currentIndex < mDocuments.size()) {
            final T imageDocument = mDocuments.get(currentIndex);
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
