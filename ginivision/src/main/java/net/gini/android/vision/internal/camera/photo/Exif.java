package net.gini.android.vision.internal.camera.photo;

import static org.apache.commons.imaging.Imaging.getMetadata;

import android.os.Build;
import android.util.Log;

import net.gini.android.vision.BuildConfig;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * @suppress
 */
class Exif {

    static final String USER_COMMENT_MAKE = "Make";
    static final String USER_COMMENT_MODEL = "Model";
    static final String USER_COMMENT_PLATFORM = "Platform";
    static final String USER_COMMENT_OS_VERSION = "OSVer";
    static final String USER_COMMENT_GINI_VISION_VERSION = "GiniVisionVer";
    static final String USER_COMMENT_CONTENT_ID = "ContentId";
    static final String USER_COMMENT_ROTATION_DELTA = "RotDeltaDeg";
    static final String USER_COMMENT_DEVICE_ORIENTATION = "DeviceOrientation";
    static final String USER_COMMENT_DEVICE_TYPE = "DeviceType";
    static final String USER_COMMENT_SOURCE = "Source";
    static final String USER_COMMENT_IMPORT_METHOD = "ImportMethod";

    private final TiffOutputSet mTiffOutputSet;

    private Exif(@NonNull final TiffOutputSet tiffOutputSet) {
        mTiffOutputSet = tiffOutputSet;
    }

    @NonNull
    public static Builder builder(@NonNull final byte[] jpeg)
            throws ImageWriteException, IOException, ImageReadException {
        return new Builder(jpeg);
    }

    static UserCommentBuilder userCommentBuilder() {
        return new UserCommentBuilder();
    }

    @NonNull
    public byte[] writeToJpeg(@NonNull final byte[] jpeg)
            throws ImageWriteException, ImageReadException, IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        final ExifRewriter exifRewriter = new ExifRewriter();
        exifRewriter.updateExifMetadataLossless(jpeg, outputStream, mTiffOutputSet);

        return outputStream.toByteArray();
    }

    @NonNull
    public static RequiredTags readRequiredTags(@NonNull final byte[] jpeg)
            throws IOException, ImageReadException {
        final RequiredTags requiredTags = new RequiredTags();

        JpegImageMetadata jpegMetadata = null;
        try {
            jpegMetadata = (JpegImageMetadata) getMetadata(jpeg);
        } catch (final ClassCastException e) { // NOPMD
            // Ignore
        }

        if (jpegMetadata != null) {
            requiredTags.make = jpegMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_MAKE);
            requiredTags.model = jpegMetadata.findEXIFValue(TiffTagConstants.TIFF_TAG_MODEL);
            requiredTags.iso = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_ISO);
            requiredTags.exposure = jpegMetadata.findEXIFValue(
                    ExifTagConstants.EXIF_TAG_EXPOSURE_TIME);
            requiredTags.aperture = jpegMetadata.findEXIFValue(
                    ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
            requiredTags.flash = jpegMetadata.findEXIFValue(ExifTagConstants.EXIF_TAG_FLASH);
            requiredTags.compressedBitsPerPixel = jpegMetadata.findEXIFValue(
                    ExifTagConstants.EXIF_TAG_COMPRESSED_BITS_PER_PIXEL);
        }

        return requiredTags;
    }

    static class Builder {

        private final TiffOutputSet mTiffOutputSet;
        private TiffOutputDirectory mIfd0Directory;
        private final TiffOutputDirectory mExifDirectory;

        private Builder(@NonNull final byte[] jpeg)
                throws ImageWriteException, IOException, ImageReadException {
            // Create a new exif metadata set, to keep only the required exif tags
            mTiffOutputSet = createOutputSetForJpeg(jpeg, TiffConstants.DEFAULT_TIFF_BYTE_ORDER);

            mExifDirectory = mTiffOutputSet.getOrCreateExifDirectory();
            mIfd0Directory = mTiffOutputSet.findDirectory(
                    TiffDirectoryType.TIFF_DIRECTORY_IFD0.directoryType);
            if (mIfd0Directory == null) {
                mIfd0Directory = new TiffOutputDirectory(
                        TiffDirectoryType.TIFF_DIRECTORY_IFD0.directoryType,
                        mTiffOutputSet.byteOrder);
                mTiffOutputSet.addDirectory(mIfd0Directory);
            }
        }

        private static TiffOutputSet createOutputSetForJpeg(@NonNull final byte[] jpeg,
                final ByteOrder defaultByteOrder)
                throws IOException, ImageReadException, ImageWriteException {
            ByteOrder byteOrder = defaultByteOrder;

            final JpegImageMetadata jpegMetadata;
            try {
                jpegMetadata = (JpegImageMetadata) getMetadata(jpeg);
            } catch (final ClassCastException e) {
                throw new ImageReadException(
                        "Wrong metadata type, only JpegImageMetadata supported", e);
            }

            if (jpegMetadata != null) {
                final TiffImageMetadata exif = jpegMetadata.getExif();
                if (exif != null) {
                    byteOrder = exif.getOutputSet().byteOrder;
                }
            }

            return new TiffOutputSet(byteOrder);
        }

        @NonNull
        public Builder setRequiredTags(@NonNull final RequiredTags requiredTags)
                throws ImageReadException, ImageWriteException {
            // Make
            if (requiredTags.make != null) {
                try {
                    final TiffOutputField makeField = createTiffOutputField(requiredTags.make);
                    mIfd0Directory.add(makeField);
                } catch (final Exception e) { // NOPMD
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // Model
            if (requiredTags.model != null) {
                try {
                    final TiffOutputField modelField = createTiffOutputField(requiredTags.model);
                    mIfd0Directory.add(modelField);
                } catch (final Exception e) { // NOPMD
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // ISO
            if (requiredTags.iso != null) {
                try {
                    final TiffOutputField isoField = createTiffOutputField(requiredTags.iso);
                    mExifDirectory.add(isoField);
                } catch (final Exception e) { // NOPMD
                    // Ignore, ClassCastException was thrown on a Galaxy Nexus w. Android 4.3
                }
            }
            // Exposure
            if (requiredTags.exposure != null) {
                try {
                    final TiffOutputField exposureField = createTiffOutputField(
                            requiredTags.exposure);
                    mExifDirectory.add(exposureField);
                } catch (final Exception e) { // NOPMD
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // Aperture
            if (requiredTags.aperture != null) {
                try {
                    final TiffOutputField apertureField = createTiffOutputField(
                            requiredTags.aperture);
                    mExifDirectory.add(apertureField);
                } catch (final Exception e) { // NOPMD
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // Flash
            if (requiredTags.flash != null) {
                try {
                    final TiffOutputField flashField = createTiffOutputField(requiredTags.flash);
                    mExifDirectory.add(flashField);
                } catch (final Exception e) { // NOPMD
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // Compressed bits per pixel
            if (requiredTags.compressedBitsPerPixel != null) {
                try {
                    final TiffOutputField compressedBitsPerPixelField = createTiffOutputField(
                            requiredTags.compressedBitsPerPixel);
                    mExifDirectory.add(compressedBitsPerPixelField);
                } catch (final Exception e) { // NOPMD
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            return this;
        }

        @NonNull
        private TiffOutputField createTiffOutputField(@NonNull final TiffField tiffField) {
            return new TiffOutputField(tiffField.getTagInfo(),
                    tiffField.getFieldType(),
                    (int) tiffField.getCount(),
                    tiffField.getByteArrayValue());
        }

        @NonNull
        public Builder setUserComment(final String userComment) {
            addUserCommentStringExif(mExifDirectory, userComment);
            return this;
        }

        @NonNull
        public Builder setOrientationFromDegrees(final int degrees) {
            try {
                final short orientation = rotationToExifOrientation(degrees); // NOPMD
                final byte[] bytes = FieldType.SHORT.writeData(orientation,
                        mTiffOutputSet.byteOrder);
                final TiffOutputField orientationOutputField = new TiffOutputField(
                        TiffTagConstants.TIFF_TAG_ORIENTATION, FieldType.SHORT, 1, bytes);
                mIfd0Directory.add(orientationOutputField);
            } catch (final ImageWriteException ignore) {
                // Ignored
            }
            return this;
        }

        @NonNull
        public Exif build() {
            return new Exif(mTiffOutputSet);
        }

        private void addUserCommentStringExif(@NonNull final TiffOutputDirectory outputDirectory,
                @NonNull final String value) {
            // ASCII character code
            final byte[] characterCode = new byte[]{0x41, 0x53, 0x43, 0x49, 0x49, 0x00, 0x00, 0x00};

            final byte[] comment = value.getBytes(Charset.forName("US-ASCII"));
            final byte[] userComment = new byte[characterCode.length + comment.length];

            System.arraycopy(characterCode, 0, userComment, 0, characterCode.length);
            System.arraycopy(comment, 0, userComment, characterCode.length, comment.length);

            addStringExif(outputDirectory, ExifTagConstants.EXIF_TAG_USER_COMMENT, userComment);
        }

        private void addStringExif(final TiffOutputDirectory outputDirectory, final TagInfo tagInfo,
                final byte[] bytes) {
            final TiffOutputField outputField = new TiffOutputField(tagInfo, FieldType.ASCII,
                    bytes.length, bytes);
            outputDirectory.add(outputField);
        }

        private static short rotationToExifOrientation(final int degrees) { // NOPMD
            final short exifOrientation; // NOPMD
            switch (degrees) {
                case 0:
                    exifOrientation = 1; // 0CW
                    break;
                case 90:
                    exifOrientation = 6; // 270CW
                    break;
                case 180:
                    exifOrientation = 3; // 180CW
                    break;
                case 270:
                    exifOrientation = 8; // 90CW
                    break;
                default:
                    exifOrientation = 1; // 0CW
                    break;
            }

            return exifOrientation;
        }
    }

    static class RequiredTags {

        public TiffField make;
        public TiffField model;
        public TiffField iso;
        public TiffField exposure;
        public TiffField aperture;
        public TiffField flash;
        public TiffField compressedBitsPerPixel;
        // User Comment is also required, but added manually
        // Orientation is also required, but added manually


        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final RequiredTags that = (RequiredTags) o;

            return areEqual(make, that.make)
                    && areEqual(model, that.model)
                    && areEqual(iso, that.iso)
                    && areEqual(exposure, that.exposure)
                    && areEqual(aperture, that.aperture)
                    && areEqual(flash, that.flash)
                    && areEqual(compressedBitsPerPixel, that.compressedBitsPerPixel);
        }

        private boolean areEqual(@Nullable final TiffField left, @Nullable final TiffField right) {
            final boolean leftIsNotNull = left != null;
            final boolean rightIsNotNull = right != null;
            Log.d("RequiredTags", "left : " + (leftIsNotNull ? left.toString() : "null"));
            Log.d("RequiredTags", "right: " + (rightIsNotNull ? right.toString() : "null"));
            return leftIsNotNull && rightIsNotNull ? left.getValueDescription().equals(
                    right.getValueDescription())
                    : leftIsNotNull == rightIsNotNull;
        }

        @Override
        public int hashCode() {
            int result = make != null ? make.hashCode() : 0;
            result = 31 * result + (model != null ? model.hashCode() : 0);
            result = 31 * result + (iso != null ? iso.hashCode() : 0);
            result = 31 * result + (exposure != null ? exposure.hashCode() : 0);
            result = 31 * result + (aperture != null ? aperture.hashCode() : 0);
            result = 31 * result + (flash != null ? flash.hashCode() : 0);
            result = 31 * result + (compressedBitsPerPixel != null
                    ? compressedBitsPerPixel.hashCode() : 0);
            return result;
        }
    }

    static class UserCommentBuilder {

        private boolean mAddMake;
        private boolean mAddModel;
        private String mContentId;
        private int mRotationDelta;
        private String mDeviceOrientation;
        private String mDeviceType;
        private String mSource;
        private String mImportMethod;

        private UserCommentBuilder() {

        }

        UserCommentBuilder setAddMake(final boolean addMake) {
            mAddMake = addMake;
            return this;
        }

        UserCommentBuilder setAddModel(final boolean addModel) {
            mAddModel = addModel;
            return this;
        }

        UserCommentBuilder setContentId(final String contentId) {
            mContentId = contentId;
            return this;
        }

        UserCommentBuilder setRotationDelta(final int rotationDelta) {
            mRotationDelta = rotationDelta;
            return this;
        }

        UserCommentBuilder setDeviceOrientation(final String orientation) {
            mDeviceOrientation = orientation;
            return this;
        }

        UserCommentBuilder setDeviceType(final String type) {
            mDeviceType = type;
            return this;
        }

        UserCommentBuilder setSource(final String source) {
            mSource = source;
            return this;
        }

        UserCommentBuilder setImportMethod(final String importMethod) {
            mImportMethod = importMethod;
            return this;
        }

        @NonNull
        public String build() {
            if (mContentId == null) {
                throw new IllegalStateException("ContentId is required for the User Comment");
            }
            return createUserComment();
        }

        @NonNull
        private String createUserComment() {
            final Map<String, String> keyValueMap = createKeyValueMap();
            return convertMapToCSV(keyValueMap);
        }

        @NonNull
        private Map<String, String> createKeyValueMap() {
            final Map<String, String> map = new LinkedHashMap<>(); // NOPMD
            // Make
            if (mAddMake) {
                map.put(USER_COMMENT_MAKE, Build.BRAND);
            }
            // Model
            if (mAddModel) {
                map.put(USER_COMMENT_MODEL, Build.MODEL);
            }
            // Platform
            map.put(USER_COMMENT_PLATFORM, "Android");
            // OS Version
            map.put(USER_COMMENT_OS_VERSION, String.valueOf(Build.VERSION.RELEASE));
            // GiniVision Version
            map.put(USER_COMMENT_GINI_VISION_VERSION, BuildConfig.VERSION_NAME.replace(" ", ""));
            // Content ID
            map.put(USER_COMMENT_CONTENT_ID, mContentId);
            // Rotation Delta
            map.put(USER_COMMENT_ROTATION_DELTA, String.valueOf(mRotationDelta));
            // Device Orientation
            map.put(USER_COMMENT_DEVICE_ORIENTATION, mDeviceOrientation);
            // Device Type
            map.put(USER_COMMENT_DEVICE_TYPE, mDeviceType);
            // Source
            map.put(USER_COMMENT_SOURCE, mSource);
            // Import Method
            if (mImportMethod != null) {
                map.put(USER_COMMENT_IMPORT_METHOD, mImportMethod);
            }
            return map;
        }

        @NonNull
        private String convertMapToCSV(@NonNull final Map<String, String> keyValueMap) {
            final StringBuilder csvBuilder = new StringBuilder();
            boolean isFirst = true;
            for (final Map.Entry<String, String> keyValueEntry : keyValueMap.entrySet()) {
                if (!isFirst) {
                    csvBuilder.append(',');
                }
                isFirst = false;

                csvBuilder.append(keyValueEntry.getKey())
                        .append('=')
                        .append(keyValueEntry.getValue());
            }
            return csvBuilder.toString();
        }

    }
}
