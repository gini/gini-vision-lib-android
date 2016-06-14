package net.gini.android.vision.scanner.photo;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The document cache is a singleton which stores the captured image. This solution is needed
 * because it is not possible to pass large bitmaps via intents.
 *
 * You should never use this service directly but instead work with the Photo.
 */
public enum ImageCache {

    INSTANCE;

    /**
     * Opaque token type to identify a document.
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

        private final int token;

        private Token(final int token) {
            this.token = token;
        }

        private static Token next() {
            return new Token(COUNTER.getAndIncrement());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(final Parcel dest, final int flags) {
            dest.writeInt(token);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            } else if (other instanceof Token) {
                return token == ((Token) other).token;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return token;
        }
    }

    private Map<Token, Bitmap> mBitmapCache = new ConcurrentHashMap<>();
    private Map<Token, byte[]> mJpegCache = new ConcurrentHashMap<>();

    public Bitmap getBitmap(final Token token) {
        return mBitmapCache.get(token);
    }

    public Token storeBitmap(final Bitmap documentBitmap) {
        final Token token = Token.next();
        mBitmapCache.put(token, documentBitmap);
        return token;
    }

    public void removeBitmap(final Token token) {
        mBitmapCache.remove(token);
    }

    public byte[] getJpeg(final Token token) {
        return mJpegCache.get(token);
    }

    public Token storeJpeg(final byte[] documentJpeg) {
        final Token token = Token.next();
        mJpegCache.put(token, documentJpeg);
        return token;
    }

    public void removeJpeg(final Token token) {
        mJpegCache.remove(token);
    }

    public static ImageCache getInstance() {
        return INSTANCE;
    }
}
