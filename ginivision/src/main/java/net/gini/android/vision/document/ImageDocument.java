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
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.util.IntentHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *     A document containing an image.
 * </p>
 *
 */
public final class ImageDocument extends GiniVisionDocument {

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

    /**
     * @exclude
     */
    public static class Source {
        private final String mName;

        public static Source newCameraSource() {
            return new Source("camera");
        }

        public static Source newExternalSource() {
            return new Source("external");
        }

        public static Source newSource(@NonNull final String name) {
            return new Source(name);
        }

        public static Source newUnknownSource() {
            return new Source("");
        }

        private Source(@NonNull final String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Source that = (Source) o;

            return mName.equals(that.mName);
        }

        @Override
        public int hashCode() {
            return mName.hashCode();
        }
    }

    /**
     * @exclude
     */
    public enum ImportMethod {
        OPEN_WITH("openwith"), PICKER("picker"), NONE("");

        private static final Map<String, ImportMethod> sLookup = new HashMap<>();

        static {
            for (final ImportMethod importMethod : ImportMethod.values()) {
                sLookup.put(importMethod.asString(), importMethod);
            }
        }

        public static ImportMethod forName(@NonNull final String name) {
            if (sLookup.containsKey(name)) {
                return sLookup.get(name);
            }
            return ImportMethod.NONE;
        }

        private final String mName;

        ImportMethod(final String name) {
            mName = name;
        }

        public String asString() {
            return mName;
        }
    }

    private final String mDeviceOrientation;
    private final String mDeviceType;
    private final Source mSource;
    private final ImportMethod mImportMethod;
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
        if (/*multipage enabled*/ true) { // TODO: mutipage feature toggle
            imageUri = GiniVision.getInstance().internal().getImageDiskStore()
                    .save(context, uri);
            if (imageUri == null) {
                throw new IllegalArgumentException("Failed to copy to app storage");
            }
        } else {
            imageUri = uri;
        }
        return new ImageDocument(intent, imageUri, ImageFormat.fromMimeType(mimeType), deviceOrientation,
                deviceType, source, importMethod);
    }

    @NonNull
    static ImageDocument fromUri(@NonNull final Uri uri,
            @NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final ImportMethod importMethod) {
        final String mimeType = getMimeType(uri, context);
        if (mimeType == null || !hasMimeTypeWithPrefix(uri, context,
                MimeType.IMAGE_PREFIX.asString())) {
            throw new IllegalArgumentException("Intent must have a mime type of image/*");
        }
        final Source source = getDocumentSource(intent, context);
        final Uri localUri = GiniVision.getInstance().internal().getImageDiskStore()
                .save(context, uri);
        if (localUri == null) {
            throw new IllegalArgumentException("Failed to copy to app storage");
        }
        return new ImageDocument(localUri, ImageFormat.fromMimeType(mimeType), deviceOrientation,
                deviceType, source, importMethod);
    }

    private static Source getDocumentSource(@NonNull final Intent data,
            @NonNull final Context context) {
        final String appName = getSourceAppName(data, context);
        return appName != null ? Source.newSource(appName) : Source.newExternalSource();
    }

    private ImageDocument() {
        this((byte[]) null);
    }

    @VisibleForTesting
    ImageDocument(@Nullable final byte[] data) {
        super(Type.IMAGE,data, null, null, true, false);
        mRotationForDisplay = 0;
        mFormat = ImageFormat.JPEG;
        mDeviceOrientation = "";
        mDeviceType = "";
        mSource = Source.newUnknownSource();
        mImportMethod = ImportMethod.NONE;
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
            @NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
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
            @NonNull final Source source,
            @NonNull final ImportMethod importMethod) {
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
    public Source getSource() {
        return mSource;
    }

    /**
     * @exclude
     */
    public ImportMethod getImportMethod() {
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
        dest.writeString(mSource.getName());
        dest.writeString(mImportMethod.asString());
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
        mSource = Source.newSource(in.readString());
        mImportMethod = ImportMethod.forName(in.readString());
    }
}
