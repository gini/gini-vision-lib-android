package net.gini.android.vision.document;

import static net.gini.android.vision.util.IntentHelper.getMimeTypes;
import static net.gini.android.vision.util.IntentHelper.getSourceAppName;
import static net.gini.android.vision.util.IntentHelper.hasMimeTypeWithPrefix;
import static net.gini.android.vision.util.UriHelper.getMimeType;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.util.IntentHelper;

import java.util.List;

/**
 * <p>
 *     A document containing an image.
 * </p>
 *
 */
public final class ImageDocument extends GiniVisionDocument {

    private final String mDeviceOrientation;
    private final String mDeviceType;
    private final String mSource;
    private final String mImportMethod;

    /**
     * <p>
     *     Supported image formats.
     * </p>
     */
    public enum ImageFormat {
        JPEG,
        PNG,
        GIF;

        static ImageFormat fromMimeType(@NonNull final String mimeType) {
            switch (mimeType) {
                case "image/jpeg":
                    return JPEG;
                case "image/png":
                    return PNG;
                case "image/gif":
                    return GIF;
                default:
                    throw new IllegalArgumentException("Unknown mime type: " + mimeType);
            }
        }
    }

    private int mRotationForDisplay;
    private final ImageFormat mFormat;

    @NonNull
    static ImageDocument empty() {
        return new ImageDocument();
    }

    @NonNull
    static ImageDocument fromPhoto(@NonNull final Photo photo) {
        return new ImageDocument(photo);
    }

    @NonNull
    static ImageDocument fromPhoto(@NonNull final Photo photo,
            @NonNull final Uri storedAtUri) {
        return new ImageDocument(photo, null, storedAtUri);
    }

    @NonNull
    static ImageDocument fromPhotoAndDocument(@NonNull final Photo photo,
            @NonNull final Document document) {
        return new ImageDocument(photo, document);
    }

    @NonNull
    static ImageDocument fromIntent(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final String importMethod) {
        final List<String> mimeTypes = getMimeTypes(intent, context);
        if (mimeTypes.isEmpty() || !hasMimeTypeWithPrefix(intent, context,
                IntentHelper.MimeType.IMAGE_PREFIX.asString())) {
            throw new IllegalArgumentException("Intent must have a mime type of image/*");
        }
        final String mimeType = mimeTypes.get(0);
        final String source = getDocumentSource(intent, context);
        final Uri uri = IntentHelper.getUri(intent);
        if (uri == null) {
            throw new IllegalArgumentException("Intent must have a Uri");
        }
        final Uri localUri = GiniVision.getInstance().internal().getImageDiskStore()
                .save(context, uri);
        if (localUri == null) {
            throw new IllegalArgumentException("Failed to copy to app storage");
        }
        return new ImageDocument(intent, localUri, ImageFormat.fromMimeType(mimeType), deviceOrientation,
                deviceType, source, importMethod);
    }

    @NonNull
    static ImageDocument fromUri(@NonNull final Uri uri,
            @NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final String importMethod) {
        final String mimeType = getMimeType(uri, context);
        if (mimeType == null || !hasMimeTypeWithPrefix(uri, context,
                IntentHelper.MimeType.IMAGE_PREFIX.asString())) {
            throw new IllegalArgumentException("Intent must have a mime type of image/*");
        }
        final String source = getDocumentSource(intent, context);
        final Uri localUri = GiniVision.getInstance().internal().getImageDiskStore()
                .save(context, uri);
        if (localUri == null) {
            throw new IllegalArgumentException("Failed to copy to app storage");
        }
        return new ImageDocument(localUri, ImageFormat.fromMimeType(mimeType), deviceOrientation,
                deviceType, source, importMethod);
    }

    private static String getDocumentSource(@NonNull final Intent data,
            @NonNull final Context context) {
        final String appName = getSourceAppName(data, context);
        return appName != null ? appName : "external";
    }

    private ImageDocument() {
        super(Type.IMAGE,null, null, null, true, false);
        mRotationForDisplay = 0;
        mFormat = ImageFormat.JPEG;
        mDeviceOrientation = "";
        mDeviceType = "";
        mSource = "";
        mImportMethod = "";
    }

    private ImageDocument(@NonNull final Photo photo) {
        this(photo, null, null);
    }

    private ImageDocument(@NonNull final Photo photo,
            @NonNull final Document document) {
        this(photo, document.getIntent(), document.getUri());
    }

    private ImageDocument(@NonNull final Photo photo,
            @Nullable final Intent intent, @Nullable final Uri uri) {
        super(Type.IMAGE, photo.getData(), intent, uri, true, photo.isImported());
        mRotationForDisplay = photo.getRotationForDisplay();
        mFormat = photo.getImageFormat();
        mDeviceOrientation = photo.getDeviceOrientation();
        mDeviceType = photo.getDeviceType();
        mSource = photo.getSource();
        mImportMethod = photo.getImportMethod();
    }

    private ImageDocument(@Nullable final Intent intent, @Nullable final Uri uri,
            @NonNull final ImageFormat format,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final String source,
            @NonNull final String importMethod) {
        super(Type.IMAGE, null, intent, uri, true, true);
        mRotationForDisplay = 0;
        mFormat = format;
        mDeviceOrientation = deviceOrientation;
        mDeviceType = deviceType;
        mSource = source;
        mImportMethod = importMethod;
    }

    private ImageDocument(@Nullable final Uri uri, @NonNull final ImageFormat format,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final String source,
            @NonNull final String importMethod) {
        super(Type.IMAGE, null, null, uri, true, true);
        mRotationForDisplay = 0;
        mFormat = format;
        mDeviceOrientation = deviceOrientation;
        mDeviceType = deviceType;
        mSource = source;
        mImportMethod = importMethod;
    }

    /**
     * <p>
     *     Retrieve the format of the image.
     * </p>
     *
     * @return image format
     */
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
    @Override
    public int getRotationForDisplay() {
        return mRotationForDisplay;
    }

    public synchronized void setRotationForDisplay(final int degrees) {
        // Converts input degrees to degrees between [0,360)
        mRotationForDisplay = ((degrees % 360) + 360) % 360;
    }

    /**
     * @exclude
     */
    public String getDeviceOrientation() {
        return mDeviceOrientation;
    }

    /**
     * @exclude
     */
    public String getDeviceType() {
        return mDeviceType;
    }

    /**
     * @exclude
     */
    public String getSource() {
        return mSource;
    }

    /**
     * @exclude
     */
    public String getImportMethod() {
        return mImportMethod;
    }

    /**
     * @exclude
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * @exclude
     */
    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mRotationForDisplay);
        dest.writeSerializable(mFormat);
        dest.writeString(mDeviceOrientation);
        dest.writeString(mDeviceType);
        dest.writeString(mSource);
        dest.writeString(mImportMethod);
    }

    /**
     * @exclude
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
        mFormat = (ImageFormat) in.readSerializable();
        mDeviceOrientation = in.readString();
        mDeviceType = in.readString();
        mSource = in.readString();
        mImportMethod = in.readString();
    }
}
