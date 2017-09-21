package net.gini.android.vision.document;

import static net.gini.android.vision.internal.util.IntentHelper.getMimeTypes;
import static net.gini.android.vision.internal.util.IntentHelper.hasMimeTypeWithPrefix;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.GiniVisionDocument;
import net.gini.android.vision.internal.camera.photo.Photo;

import java.util.List;

public class ImageDocument extends GiniVisionDocument {

    public enum ImageFormat {
        JPEG,
        TIFF,
        PNG,
        GIF;

        public static ImageFormat fromMimeType(@NonNull final String mimeType) {
            switch (mimeType) {
                case "image/jpeg":
                    return JPEG;
                case "image/tiff":
                    return TIFF;
                case "image/png":
                    return PNG;
                case "image/gif":
                    return GIF;
                default:
                    throw new IllegalArgumentException("Unknown mime type: " + mimeType);
            }
        }
    }

    private final int mRotationForDisplay;
    private final ImageFormat mFormat;

    @NonNull
    public static ImageDocument fromPhoto(@NonNull final Photo photo) {
        return new ImageDocument(photo);
    }

    @NonNull
    public static ImageDocument fromIntent(@NonNull final Intent intent,
            @NonNull final Context context) {
        final List<String> mimeTypes = getMimeTypes(intent, context);
        if (mimeTypes.size() == 0 || !hasMimeTypeWithPrefix(intent, context, "image/")) {
            throw new IllegalArgumentException("Intent must have a mime type of image/*");
        }
        final String mimeType = mimeTypes.get(0);
        return new ImageDocument(intent, ImageFormat.fromMimeType(mimeType));
    }

    public ImageDocument(@NonNull final Photo photo) {
        super(Type.IMAGE, photo.getData(), null, true, photo.isImported());
        mRotationForDisplay = photo.getRotationForDisplay();
        mFormat = photo.getImageFormat();
    }

    public ImageDocument(@Nullable final Intent intent, @NonNull final ImageFormat format) {
        super(Type.IMAGE, null, intent, true, true);
        mRotationForDisplay = 0;
        mFormat = format;
    }

    @NonNull
    public ImageFormat getFormat() {
        return mFormat;
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
        dest.writeSerializable(mFormat);
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
        mFormat = (ImageFormat) in.readSerializable();
    }
}
