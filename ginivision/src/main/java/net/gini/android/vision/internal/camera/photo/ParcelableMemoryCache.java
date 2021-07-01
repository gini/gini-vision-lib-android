package net.gini.android.vision.internal.camera.photo;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Internal use only.
 * <p>
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

    private static final long REMOVE_DELAY_MS = 1000;

    private static final int STORE_BYTE_ARRAY = 1;
    private static final int REMOVE_BYTE_ARRAY = 2;
    private static final int STORE_BITMAP = 3;
    private static final int REMOVE_BITMAP = 4;
    private static final int REMOVE_ENTRIES_WITH_TAG = 5;

    private static final Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case STORE_BYTE_ARRAY:
                    INSTANCE.doStoreByteArray((TokenWithPayload<byte[]>) msg.obj);
                    return true;
                case REMOVE_BYTE_ARRAY:
                    INSTANCE.doRemoveByteArray((Token) msg.obj);
                    return true;
                case STORE_BITMAP:
                    INSTANCE.doStoreBitmap((TokenWithPayload<Bitmap>) msg.obj);
                    return true;
                case REMOVE_BITMAP:
                    INSTANCE.doRemoveBitmap((Token) msg.obj);
                    return true;
                case REMOVE_ENTRIES_WITH_TAG:
                    INSTANCE.doRemoveEntriesWithTag((String) msg.obj);
                    return true;
                default:
                    return false;
            }
        }
    });

    private static class TokenWithPayload<P> {
        private final Token token;
        private final P payload;
        private final String tag;

        private TokenWithPayload(@NonNull final Token token,
                                 @NonNull final P payload,
                                 @NonNull final String tag) {
            this.token = token;
            this.payload = payload;
            this.tag = tag;
        }

        private TokenWithPayload(@NonNull final Token token,
                                 @NonNull final P payload) {
            this.token = token;
            this.payload = payload;
            this.tag = null;
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

    @NonNull
    public Token storeByteArray(@NonNull final byte[] documentJpeg) {
        final Token token = Token.next();
        final Message msg = mHandler.obtainMessage(STORE_BYTE_ARRAY,
                new TokenWithPayload<>(token, documentJpeg));
        mHandler.sendMessage(msg);
        return token;
    }

    @NonNull
    public Token storeByteArray(@NonNull final byte[] documentJpeg, @NonNull final String tag) {
        final Token token = Token.next(tag);
        final Message msg = mHandler.obtainMessage(STORE_BYTE_ARRAY,
                new TokenWithPayload<>(token, documentJpeg, tag));
        mHandler.sendMessage(msg);
        return token;
    }

    private void doStoreByteArray(@NonNull final TokenWithPayload<byte[]> tokenWithPayload) {
        mByteArrayCache.put(tokenWithPayload.token, tokenWithPayload.payload);
        if (tokenWithPayload.tag != null) {
            LOG.debug("Store byte array for token {} with tag {}", tokenWithPayload.token,
                    tokenWithPayload.tag);
        } else {
            LOG.debug("Store byte array for token {}", tokenWithPayload.token);
        }
        logCacheSizes();
    }

    public void removeByteArray(@NonNull final Token token) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(REMOVE_BYTE_ARRAY, token),
                REMOVE_DELAY_MS);
    }

    private void doRemoveByteArray(@NonNull final Token token) {
        mByteArrayCache.remove(token);
        LOG.debug("Remove byte array for token {}", token);
        logCacheSizes();
    }

    public void removeEntriesWithTag(@NonNull final String tag) {
        mHandler.sendMessage(mHandler.obtainMessage(REMOVE_ENTRIES_WITH_TAG, tag));
    }

    private void doRemoveEntriesWithTag(@NonNull final String tag) {
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
            final Message msg = mHandler.obtainMessage(STORE_BITMAP,
                    new TokenWithPayload<>(token, documentBitmap));
            mHandler.sendMessage(msg);
        }
        return token;
    }

    @NonNull
    public Token storeBitmap(@Nullable final Bitmap documentBitmap, @NonNull final String tag) {
        final Token token = Token.next(tag);
        if (documentBitmap != null) {
            final Message msg = mHandler.obtainMessage(STORE_BITMAP,
                    new TokenWithPayload<>(token, documentBitmap, tag));
            mHandler.sendMessage(msg);
        }
        return token;
    }

    private void doStoreBitmap(@NonNull final TokenWithPayload<Bitmap> tokenWithPayload) {
        mBitmapCache.put(tokenWithPayload.token, tokenWithPayload.payload);
        if (tokenWithPayload.tag != null) {
            LOG.debug("Store bitmap for token {} with tag {}", tokenWithPayload.token,
                    tokenWithPayload.tag);
        } else {
            LOG.debug("Store bitmap for token {}", tokenWithPayload.token);
        }
        logCacheSizes();
    }

    public void removeBitmap(@NonNull final Token token) {
        mHandler.sendMessageDelayed(mHandler.obtainMessage(REMOVE_BITMAP, token),
                REMOVE_DELAY_MS);
    }

    private void doRemoveBitmap(@NonNull final Token token) {
        mBitmapCache.remove(token);
        LOG.debug("Remove bitmap for token {}", token);
        logCacheSizes();
    }

    @NonNull
    public static ParcelableMemoryCache getInstance() {
        return INSTANCE;
    }

}
