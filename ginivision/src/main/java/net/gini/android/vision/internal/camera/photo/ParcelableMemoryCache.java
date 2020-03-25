package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * This singleton cache keeps references to byte arrays and Bitmaps to be preserved between
 * parceling and unparceling.
 *
 * <p> This solution is needed because it is not possible to pass large
 * byte arrays and Bitmaps via Intents.
 *
 * @suppress
 */
public enum ParcelableMemoryCache {

    INSTANCE;

    private static final boolean DEBUG = false;
    private static final Logger LOG;
    static {
        if (DEBUG) {
            LOG = LoggerFactory.getLogger(ParcelableMemoryCache.class);
        } else {
            LOG = NOPLogger.NOP_LOGGER;
        }
    }

    /**
     * Opaque tokenId type to identify a document.
     */
    public static final class Token implements Parcelable {

        private static final AtomicInteger COUNTER = new AtomicInteger(0);
        public static final Creator<Token> CREATOR = new Creator<Token>() {
            @Override
            public Token createFromParcel(final Parcel source) {
                final int token = source.readInt();
                final String tag = source.readString();
                return new Token(token, tag);
            }

            @Override
            public Token[] newArray(final int size) {
                return new Token[size];
            }
        };

        private final int tokenId;
        final String tag;

        private Token(final int tokenId) {
            this.tokenId = tokenId;
            tag = "";
        }

        private Token(final int tokenId, @NonNull final String tag) {
            this.tokenId = tokenId;
            this.tag = tag;
        }

        @NonNull
        static Token next() {
            return new Token(COUNTER.getAndIncrement());
        }

        @NonNull
        static Token next(@NonNull final String tag) {
            return new Token(COUNTER.getAndIncrement(), tag);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(tokenId);
            dest.writeString(tag);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            final Token token = (Token) other;

            if (tokenId != token.tokenId) {
                return false;
            }
            return tag.equals(token.tag);
        }

        @Override
        public int hashCode() {
            int result = tokenId;
            result = 31 * result + tag.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Token{"
                    + "tokenId=" + tokenId
                    + ", tag='" + tag + '\''
                    + '}';
        }
    }

    private final Map<Token, byte[]> mByteArrayCache = new ConcurrentHashMap<>();
    private final Map<Token, Bitmap> mBitmapCache = new ConcurrentHashMap<>();

    public byte[] getByteArray(@NonNull final Token token) {
        LOG.debug("Get byte array for token {}", token);
        logCacheSizes();
        return mByteArrayCache.get(token);
    }

    private void logCacheSizes() {
        LOG.debug("Cached byte arrays: {}; Cached bitmaps: {}", mByteArrayCache.size(),
                mBitmapCache.size());
    }

    public Token storeByteArray(@NonNull final byte[] documentJpeg) {
        final Token token = Token.next();
        mByteArrayCache.put(token, documentJpeg);
        LOG.debug("Store byte array for token {}", token);
        logCacheSizes();
        return token;
    }

    public Token storeByteArray(@NonNull final byte[] documentJpeg, @NonNull final String tag) {
        final Token token = Token.next(tag);
        mByteArrayCache.put(token, documentJpeg);
        LOG.debug("Store byte array for token {} with tag {}", token, tag);
        logCacheSizes();
        return token;
    }

    public void removeByteArray(@NonNull final Token token) {
        mByteArrayCache.remove(token);
        LOG.debug("Remove byte array for token {}", token);
        logCacheSizes();
    }

    public void removeEntriesWithTag(@NonNull final String tag) {
        removeByteArraysWithTag(tag);
        removeBitmapsWithTag(tag);
        LOG.debug("Remove entries for tag {}", tag);
        logCacheSizes();
    }

    private void removeByteArraysWithTag(@NonNull final String tag) {
        removeTokensWithTag(mByteArrayCache.keySet(), tag);
    }

    private void removeBitmapsWithTag(@NonNull final String tag) {
        removeTokensWithTag(mBitmapCache.keySet(), tag);
    }

    private void removeTokensWithTag(final Set<Token> tokens,
            @NonNull final String tag) {
        final Iterator<Token> iterator = tokens.iterator();
        while (iterator.hasNext()) {
            final Token token = iterator.next();
            if (token.tag.equals(tag)) {
                iterator.remove();
            }
        }
    }

    @Nullable
    public Bitmap getBitmap(@NonNull final Token token) {
        LOG.debug("Get bitmap for token {}", token);
        logCacheSizes();
        return mBitmapCache.get(token);
    }

    @NonNull
    public Token storeBitmap(@Nullable final Bitmap documentBitmap) {
        final Token token = Token.next();
        if (documentBitmap != null) {
            mBitmapCache.put(token, documentBitmap);
        }
        LOG.debug("Store bitmap for token {}", token);
        logCacheSizes();
        return token;
    }

    @NonNull
    public Token storeBitmap(@Nullable final Bitmap documentBitmap, @NonNull final String tag) {
        final Token token = Token.next(tag);
        if (documentBitmap != null) {
            mBitmapCache.put(token, documentBitmap);
        }
        LOG.debug("Store bitmap for token {} with tag {}", token, tag);
        logCacheSizes();
        return token;
    }

    public void removeBitmap(@NonNull final Token token) {
        mBitmapCache.remove(token);
        LOG.debug("Remove bitmap for token {}", token);
        logCacheSizes();
    }

    @NonNull
    public static ParcelableMemoryCache getInstance() {
        return INSTANCE;
    }

}
