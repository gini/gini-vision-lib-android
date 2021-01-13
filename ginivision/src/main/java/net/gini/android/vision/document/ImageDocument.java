package net.gini.android.vision.document;

import static net.gini.android.vision.internal.util.FeatureConfiguration.isMultiPageEnabled;
import static net.gini.android.vision.util.IntentHelper.getMimeTypes;
import static net.gini.android.vision.util.IntentHelper.getSourceAppName;
import static net.gini.android.vision.util.IntentHelper.hasMimeTypeWithPrefix;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

/**
 * A document containing an image.
 */
public class ImageDocument extends GiniVisionDocument {

    /**
     * Supported image formats.
     */
    public enum ImageFormat {
        JPEG,
        PNG,
        GIF;

        static ImageFormat fromMimeType(@NonNull final String mimeType) {
            switch (MimeType.fromString(mimeType)) {
                case IMAGE_JPEG:
                    return JPEG;
                case IMAGE_PNG:
                    return PNG;
                case IMAGE_GIF:
                    return GIF;
                default:
                    throw new IllegalArgumentException("Unknown mime type: " + mimeType);
            }
        }
    }

    private final String mDeviceOrientation;
    private final String mDeviceType;
    private int mRotationForDisplay;
    private int mRotationDelta;
    private final ImageFormat mFormat;

    @NonNull
    static ImageDocument empty(@NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        return new ImageDocument(source, importMethod);
    }

    @NonNull
    static ImageDocument fromPhoto(@NonNull final Photo photo) {
        return new ImageDocument(photo);
    }

    @NonNull
    static ImageDocument fromPhoto(@NonNull final Photo photo,
            @NonNull final Uri storedAtUri) {
        return new ImageDocument(photo, null, null, storedAtUri);
    }

    @NonNull
    static ImageDocument fromPhotoAndDocument(@NonNull final Photo photo,
            @NonNull final GiniVisionDocument document) {
        return new ImageDocument(photo, document);
    }

    @NonNull
    static ImageDocument fromIntent(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final ImportMethod importMethod) {
        final List<String> mimeTypes = getMimeTypes(intent, context);
        if (mimeTypes.isEmpty() || !hasMimeTypeWithPrefix(intent, context,
                MimeType.IMAGE_PREFIX.asString())) {
            throw new IllegalArgumentException("Intent must have a mime type of image/*");
        }
        final String mimeType = mimeTypes.get(0);
        final Source source = getDocumentSource(intent, context);
        final Uri uri = IntentHelper.getUri(intent);
        if (uri == null) {
            throw new IllegalArgumentException("Intent must have a Uri");
        }
        final Uri imageUri;
        if (isMultiPageEnabled()) {
            imageUri = GiniVision.getInstance().internal().getImageDiskStore()
                    .save(context, uri);
            if (imageUri == null) {
                throw new IllegalArgumentException("Failed to copy to app storage");
            }
        } else {
            imageUri = uri;
        }
        return new ImageDocument(intent, imageUri, ImageFormat.fromMimeType(mimeType),
                deviceOrientation,
                deviceType, source, importMethod);
    }

    @NonNull
    static ImageDocument fromUri(@NonNull final Uri uri,
            @NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final ImportMethod importMethod) {
        if (!GiniVision.hasInstance()) {
            throw new IllegalStateException(
                    "Cannot create ImageDocument from Uri. GiniVision instance not available. Create it with GiniVision.newInstance().");
        }
        final String mimeType = UriHelper.getMimeType(uri, context);
        if (mimeType == null || !hasMimeTypeWithPrefix(uri, context,
                MimeType.IMAGE_PREFIX.asString())) {
            throw new IllegalArgumentException("Intent must have a mime type of image/*");
        }
        final Source source = getDocumentSource(intent, context);
        return new ImageDocument(uri, ImageFormat.fromMimeType(mimeType), deviceOrientation,
                deviceType, source, importMethod);
    }

    private static Source getDocumentSource(@NonNull final Intent data,
            @NonNull final Context context) {
        final String appName = getSourceAppName(data, context);
        return appName != null ? Source.newSource(appName) : Source.newExternalSource();
    }

    private ImageDocument(@NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        this((byte[]) null, source, importMethod);
    }

    @VisibleForTesting
    ImageDocument(@Nullable final byte[] data, @NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        super(Type.IMAGE, source, importMethod, MimeType.IMAGE_JPEG.asString(),
                data, null, null, true);
        mRotationForDisplay = 0;
        mFormat = ImageFormat.JPEG;
        mDeviceOrientation = "";
        mDeviceType = "";
    }

    private ImageDocument(@NonNull final Photo photo) {
        this(photo, null, null, null);
    }

    private ImageDocument(@NonNull final Photo photo,
            @NonNull final GiniVisionDocument document) {
        this(photo, document.getId(), document.getIntent(), document.getUri());
    }

    private ImageDocument(@NonNull final Photo photo, @Nullable final String uniqueId,
            @Nullable final Intent intent, @Nullable final Uri uri) {
        super(uniqueId, Type.IMAGE,
                photo.getSource() != null ? photo.getSource() : Source.newUnknownSource(),
                photo.getImportMethod() != null ? photo.getImportMethod() : ImportMethod.NONE,
                mimeTypeFromFormat(photo.getImageFormat()), photo.getData(), intent, uri, true);
        mRotationForDisplay = photo.getRotationForDisplay();
        mRotationDelta = photo.getRotationDelta();
        mFormat = photo.getImageFormat();
        mDeviceOrientation = photo.getDeviceOrientation();
        mDeviceType = photo.getDeviceType();
    }

    private ImageDocument(@Nullable final Intent intent, @Nullable final Uri uri,
            @NonNull final ImageFormat format,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        super(Type.IMAGE, source, importMethod, mimeTypeFromFormat(format),
                null, intent, uri, true);
        mRotationForDisplay = 0;
        mFormat = format;
        mDeviceOrientation = deviceOrientation;
        mDeviceType = deviceType;
    }

    private ImageDocument(@Nullable final Uri uri, @NonNull final ImageFormat format,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
        super(Type.IMAGE, source, importMethod, mimeTypeFromFormat(format), null, null, uri, true);
        mRotationForDisplay = 0;
        mFormat = format;
        mDeviceOrientation = deviceOrientation;
        mDeviceType = deviceType;
    }

    private static String mimeTypeFromFormat(@NonNull final ImageFormat format) {
        switch (format) {
            case JPEG:
                return MimeType.IMAGE_JPEG.asString();
            case PNG:
                return MimeType.IMAGE_PNG.asString();
            case GIF:
                return MimeType.IMAGE_GIF.asString();
            default:
                throw new IllegalArgumentException("Unknown image format " + format);
        }
    }

    /**
     * Retrieve the format of the image.
     *
     * @return image format
     */
    @NonNull
    public ImageFormat getFormat() {
        return mFormat;
    }

    /**
     * The amount of clockwise rotation needed to display the image in the correct orientation.
     *
     * <p> Degrees are positive and multiples of 90.
     *
     * @return degrees by which the image should be rotated clockwise before displaying
     */
    @Override
    public int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    public void setRotationForDisplay(final int degrees) {
        // Converts input degrees to degrees between [0,360)
        mRotationForDisplay = ((degrees % 360) + 360) % 360;
    }

    public int getRotationDelta() {
        return mRotationDelta;
    }

    public void updateRotationDeltaBy(final int degrees) {
        // Converts input degrees to degrees between [0,360)
        mRotationDelta = ((mRotationDelta + degrees % 360) + 360) % 360;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public String getDeviceOrientation() {
        return mDeviceOrientation;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public String getDeviceType() {
        return mDeviceType;
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
        super.writeToParcel(dest, flags);
        dest.writeInt(mRotationForDisplay);
        dest.writeInt(mRotationDelta);
        dest.writeSerializable(mFormat);
        dest.writeString(mDeviceOrientation);
        dest.writeString(mDeviceType);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final Creator<ImageDocument> CREATOR = new Creator<ImageDocument>() {
        @Override
        public ImageDocument createFromParcel(final Parcel in) {
            return new ImageDocument(in);
        }

        @Override
        public ImageDocument[] newArray(final int size) {
            return new ImageDocument[size];
        }
    };

    private ImageDocument(final Parcel in) {
        super(in);
        mRotationForDisplay = in.readInt();
        mRotationDelta = in.readInt();
        mFormat = (ImageFormat) in.readSerializable();
        mDeviceOrientation = in.readString();
        mDeviceType = in.readString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        final ImageDocument that = (ImageDocument) o;

        if (mDeviceOrientation != null ? !mDeviceOrientation.equals(that.mDeviceOrientation)
                : that.mDeviceOrientation != null) {
            return false;
        }
        if (mDeviceType != null ? !mDeviceType.equals(that.mDeviceType)
                : that.mDeviceType != null) {
            return false;
        }
        return mFormat == that.mFormat;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (mDeviceOrientation != null ? mDeviceOrientation.hashCode() : 0);
        result = 31 * result + (mDeviceType != null ? mDeviceType.hashCode() : 0);
        result = 31 * result + mFormat.hashCode();
        return result;
    }
}
