package net.gini.android.vision.document;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.internal.camera.photo.ParcelableMemoryCache;
import net.gini.android.vision.internal.util.UriReaderAsyncTask;
import net.gini.android.vision.util.IntentHelper;

import java.util.Arrays;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * @suppress
 */
public class GiniVisionDocument implements Document {

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final Creator<GiniVisionDocument> CREATOR = new Creator<GiniVisionDocument>() {
        @Override
        public GiniVisionDocument createFromParcel(final Parcel in) {
            return new GiniVisionDocument(in);
        }

        @Override
        public GiniVisionDocument[] newArray(final int size) {
            return new GiniVisionDocument[size];
        }
    };
    private final String mUniqueId;
    private final Intent mIntent;
    private final Uri mUri;
    private final boolean mIsReviewable;
    private final Type mType;
    private final Source mSource;
    private final ImportMethod mImportMethod;
    private final String mMimeType;
    private byte[] mData;
    private String mParcelableMemoryCacheTag;

    GiniVisionDocument(@NonNull final Type type,
            @NonNull final Source source,
            @NonNull final ImportMethod importMethod,
            @NonNull final String mimeType,
            @Nullable final byte[] data,
            @Nullable final Intent intent,
            @Nullable final Uri uri,
            final boolean isReviewable) {
        this(generateUniqueId(), type, source, importMethod, mimeType, data, intent, uri,
                isReviewable);
    }

    GiniVisionDocument(
            @Nullable final String uniqueId,
            @NonNull final Type type,
            @NonNull final Source source,
            @NonNull final ImportMethod importMethod,
            @NonNull final String mimeType,
            @Nullable final byte[] data,
            @Nullable final Intent intent,
            @Nullable final Uri uri,
            final boolean isReviewable) {
        mUniqueId = uniqueId != null ? uniqueId : generateUniqueId();
        mType = type;
        mSource = source;
        mImportMethod = importMethod;
        mMimeType = mimeType;
        mData = data;
        mIntent = intent;
        mUri = uri;
        mIsReviewable = isReviewable;
    }

    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    GiniVisionDocument(final Parcel in) {
        mUniqueId = in.readString();
        final ParcelableMemoryCache cache = ParcelableMemoryCache.getInstance();
        final ParcelableMemoryCache.Token token = in.readParcelable(
                ParcelableMemoryCache.Token.class.getClassLoader());
        if (token != null) {
            mData = cache.getByteArray(token);
            cache.removeByteArray(token);
        }
        mType = (Type) in.readSerializable();
        mSource = in.readParcelable(getClass().getClassLoader());
        mImportMethod = (ImportMethod) in.readSerializable();
        mMimeType = in.readString();
        mIntent = in.readParcelable(Intent.class.getClassLoader());
        mUri = in.readParcelable(Uri.class.getClassLoader());
        mIsReviewable = in.readInt() == 1;
        mParcelableMemoryCacheTag = in.readString();
    }

    @Override
    @NonNull
    public String getId() {
        return mUniqueId;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(mUniqueId);

        final ParcelableMemoryCache cache = ParcelableMemoryCache.getInstance();
        synchronized (this) {
            if (mData != null) {
                final ParcelableMemoryCache.Token token;
                if (mParcelableMemoryCacheTag != null) {
                    token = cache.storeByteArray(mData, mParcelableMemoryCacheTag);
                } else {
                    token = cache.storeByteArray(mData);
                }
                dest.writeParcelable(token, flags);
            } else {
                dest.writeParcelable(null, flags);
            }
        }
        dest.writeSerializable(mType);
        dest.writeParcelable(mSource, flags);
        dest.writeSerializable(mImportMethod);
        dest.writeString(mMimeType);
        dest.writeParcelable(mIntent, flags);
        dest.writeParcelable(mUri, flags);
        dest.writeInt(mIsReviewable ? 1 : 0);
        dest.writeString(mParcelableMemoryCacheTag);
    }

    @Deprecated
    @NonNull
    @Override
    public byte[] getJpeg() {
        final byte[] data = getData();
        return data != null ? data : new byte[]{};
    }

    @Deprecated
    @Override
    public int getRotationForDisplay() {
        return 0;
    }

    @Override
    public Type getType() {
        return mType;
    }

    @Override
    public String getMimeType() {
        return mMimeType;
    }

    @Nullable
    @Override
    public synchronized byte[] getData() {
        return mData;
    }

    public synchronized void setData(final byte[] data) {
        mData = data;
    }

    @Nullable
    @Override
    public Intent getIntent() {
        return mIntent;
    }

    @Nullable
    @Override
    public Uri getUri() {
        return mUri;
    }

    @Override
    public boolean isImported() {
        return mImportMethod != null && mImportMethod != ImportMethod.NONE;
    }

    @NonNull
    @Override
    public ImportMethod getImportMethod() {
        return mImportMethod;
    }

    @NonNull
    @Override
    public Source getSource() {
        return mSource;
    }

    @Override
    public boolean isReviewable() {
        return mIsReviewable;
    }

    public void setParcelableMemoryCacheTag(@NonNull final String tag) {
        mParcelableMemoryCacheTag = tag;
    }

    @Nullable
    public String getParcelableMemoryCacheTag() {
        return mParcelableMemoryCacheTag;
    }

    @Override
    public String toString() {
        return "GiniVisionDocument{"
                + "mUniqueId='" + mUniqueId + '\''
                + ", mIntent=" + mIntent
                + ", mUri=" + mUri
                + ", mIsReviewable=" + mIsReviewable
                + ", mType=" + mType
                + ", mSource=" + mSource
                + ", mImportMethod=" + mImportMethod
                + ", mMimeType='" + mMimeType + '\''
                + ", mData=" + Arrays.toString(mData)
                + '}';
    }

    public synchronized void loadData(@NonNull final Context context,
            @NonNull final AsyncCallback<byte[], Exception> callback) {
        if (mData != null) {
            callback.onSuccess(mData);
            return;
        }
        Uri uri = mUri;
        if (uri == null) {
            if (mIntent == null) {
                callback.onError(new IllegalStateException("No Intent to load the data from"));
                return;
            }
            uri = IntentHelper.getUri(mIntent);
        }
        if (uri == null) {
            callback.onError(new IllegalStateException("No Uri to load the data from"));
            return;
        }
        final UriReaderAsyncTask asyncTask = new UriReaderAsyncTask(context,
                new AsyncCallback<byte[], Exception>() {
                    @Override
                    public void onSuccess(final byte[] result) {
                        setData(result);
                        callback.onSuccess(mData);
                    }

                    @Override
                    public void onError(final Exception exception) {
                        callback.onError(exception);
                    }

                    @Override
                    public void onCancelled() {
                        callback.onCancelled();
                    }
                });
        asyncTask.execute(uri);
    }

    public synchronized void unloadData() {
        mData = null; // NOPMD
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final GiniVisionDocument that = (GiniVisionDocument) o;

        if (mIsReviewable != that.mIsReviewable) {
            return false;
        }
        if (!mUniqueId.equals(that.mUniqueId)) {
            return false;
        }
        if (mIntent != null ? !mIntent.equals(that.mIntent) : that.mIntent != null) {
            return false;
        }
        if (mUri != null ? !mUri.equals(that.mUri) : that.mUri != null) {
            return false;
        }
        if (mType != that.mType) {
            return false;
        }
        if (!mSource.equals(that.mSource)) {
            return false;
        }
        if (mImportMethod != that.mImportMethod) {
            return false;
        }
        return mMimeType.equals(that.mMimeType);
    }

    @Override
    public int hashCode() {
        int result = mUniqueId.hashCode();
        result = 31 * result + (mIntent != null ? mIntent.hashCode() : 0);
        result = 31 * result + (mUri != null ? mUri.hashCode() : 0);
        result = 31 * result + (mIsReviewable ? 1 : 0);
        result = 31 * result + mType.hashCode();
        result = 31 * result + mSource.hashCode();
        result = 31 * result + mImportMethod.hashCode();
        result = 31 * result + mMimeType.hashCode();
        return result;
    }
}
