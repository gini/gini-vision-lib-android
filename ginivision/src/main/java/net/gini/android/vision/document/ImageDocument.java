package net.gini.android.vision.document;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.util.IntentHelper;

import java.io.IOException;

public class ImageDocument extends GiniVisionDocument {

    private final int mRotationForDisplay;

    @NonNull
    public static ImageDocument fromPhoto(@NonNull final Photo photo) {
        return new ImageDocument(photo.getJpeg(), photo.getRotationForDisplay());
    }

    @NonNull
    public static ImageDocument fromIntent(@NonNull final Intent intent) {
        return new ImageDocument(intent);

    }

    public ImageDocument(@Nullable final byte[] data, int rotationForDisplay) {
        super(Type.IMAGE, data, null, true);
        mRotationForDisplay = rotationForDisplay;
    }

    public ImageDocument(@Nullable final Intent intent) {
        super(Type.IMAGE, null, intent, true);
        mRotationForDisplay = 0;
    }

    /**
     * <p>
     * The amount of clockwise rotation needed to display the image in the correct orientation.
     * </p>
     * <p>
     * Degrees are positive and multiples of 90.
     * </p>
     *
     * @return degrees by which the image should be rotated clockwise before displaying
     */
    public int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mRotationForDisplay);
    }

    public static final Creator<ImageDocument> CREATOR = new Creator<ImageDocument>() {
        @Override
        public ImageDocument createFromParcel(Parcel in) {
            return new ImageDocument(in);
        }

        @Override
        public ImageDocument[] newArray(int size) {
            return new ImageDocument[size];
        }
    };

    private ImageDocument(Parcel in) {
        super(in);
        mRotationForDisplay = in.readInt();
    }
}
