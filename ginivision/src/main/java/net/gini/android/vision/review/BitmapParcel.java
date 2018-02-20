package net.gini.android.vision.review;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.camera.photo.ParcelableMemoryCache;

/**
 * Created by Alpar Szotyori on 16.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class BitmapParcel implements Parcelable {

    private final Bitmap bitmap;

    public BitmapParcel(@NonNull final Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private BitmapParcel(final Parcel in) {
        final ParcelableMemoryCache cache = ParcelableMemoryCache.getInstance();
        final ParcelableMemoryCache.Token token = in.readParcelable(
                ParcelableMemoryCache.Token.class.getClassLoader());
        bitmap = cache.getBitmap(token);
        cache.removeBitmap(token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        final ParcelableMemoryCache cache = ParcelableMemoryCache.getInstance();
        final ParcelableMemoryCache.Token token = cache.storeBitmap(bitmap);
        dest.writeParcelable(token, flags);
    }

    public static final Creator<BitmapParcel> CREATOR = new Creator<BitmapParcel>() {
        @Override
        public BitmapParcel createFromParcel(final Parcel in) {
            return new BitmapParcel(in);
        }

        @Override
        public BitmapParcel[] newArray(final int size) {
            return new BitmapParcel[size];
        }
    };
}
