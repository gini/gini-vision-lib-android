package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This singleton cache keeps references to byte arrays and Bitmaps to be preserved between
 * parceling and unparceling.
 * <p>
 * This solution is needed because it is not possible to pass large byte arrays and Bitmaps via
 * Intents.
 *
 * @exclude
 */
public enum ParcelableMemoryCache {

    INSTANCE;

    /**
     * Opaque tokenId type to identify a document.
     */
    public static final class Token implements Parcelable {

        private static final AtomicInteger COUNTER = new AtomicInteger(0);
        public static final Creator<Token> CREATOR = new Creator<Token>() {
            @Override
            public Token createFromParcel(final Parcel source) {
                final int token = source.readInt();
                return new Token(token);
            }

            @Override
            public Token[] newArray(final int size) {
                return new Token[size];
            }
        };

        private final int tokenId;

        private Token(final int tokenId) {
            this.tokenId = tokenId;
        }

        @NonNull
        static Token next() {
            return new Token(COUNTER.getAndIncrement());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(tokenId);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof Token) {
                return tokenId == ((Token) other).tokenId;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return tokenId;
        }
    }

    private final Map<Token, byte[]> mByteArrayCache = new ConcurrentHashMap<>();
    private final Map<Token, Bitmap> mBitmapCache = new ConcurrentHashMap<>();

    public byte[] getByteArray(@NonNull final Token token) {
        return mByteArrayCache.get(token);
    }

    public Token storeByteArray(@NonNull final byte[] documentJpeg) {
        final Token token = Token.next();
        mByteArrayCache.put(token, documentJpeg);
        return token;
    }

    public void removeByteArray(@NonNull final Token token) {
        mByteArrayCache.remove(token);
    }

    @Nullable
    public Bitmap getBitmap(@NonNull final Token token) {
        return mBitmapCache.get(token);
    }

    @NonNull
    public Token storeBitmap(@Nullable final Bitmap documentBitmap) {
        final Token token = Token.next();
        if (documentBitmap != null) {
            mBitmapCache.put(token, documentBitmap);
        }
        return token;
    }

    public void removeBitmap(@NonNull final Token token) {
        mBitmapCache.remove(token);
    }

    @NonNull
    public static ParcelableMemoryCache getInstance() {
        return INSTANCE;
    }
}
