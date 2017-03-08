package net.gini.android.vision.internal.camera.photo;

import static org.apache.commons.imaging.Imaging.getMetadata;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

/**
 * @exclude
 */
public class Exif {

    private final TiffOutputSet mTiffOutputSet;

    private Exif(@NonNull TiffOutputSet tiffOutputSet) {
        mTiffOutputSet = tiffOutputSet;
    }

    @NonNull
    public static Builder builder(@NonNull final byte[] jpeg)
            throws ImageWriteException, IOException, ImageReadException {
        return new Builder(jpeg);
    }

    public static UserCommentBuilder userCommentBuilder() {
        return new UserCommentBuilder();
    }

    @NonNull
    public byte[] writeToJpeg(@NonNull byte[] jpeg)
            throws ImageWriteException, ImageReadException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        ExifRewriter exifRewriter = new ExifRewriter();
        exifRewriter.updateExifMetadataLossless(jpeg, outputStream, mTiffOutputSet);

        return outputStream.toByteArray();
    }

    @NonNull
    public static RequiredTags readRequiredTags(@NonNull byte[] jpeg)
            throws IOException, ImageReadException {
        RequiredTags requiredTags = new RequiredTags();

        JpegImageMetadata jpegMetadata = (JpegImageMetadata) getMetadata(jpeg);

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

    public static class Builder {

        private TiffOutputSet mTiffOutputSet;
        private TiffOutputDirectory mIfd0Directory;
        private TiffOutputDirectory mExifDirectory;

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

            JpegImageMetadata jpegMetadata = (JpegImageMetadata) getMetadata(jpeg);
            if (jpegMetadata != null) {
                TiffImageMetadata exif = jpegMetadata.getExif();
                if (exif != null) {
                    byteOrder = exif.getOutputSet().byteOrder;
                }
            }

            return new TiffOutputSet(byteOrder);
        }

        @NonNull
        public Builder setRequiredTags(@NonNull RequiredTags requiredTags)
                throws ImageReadException, ImageWriteException {
            // Make
            if (requiredTags.make != null) {
                addStringExif(mIfd0Directory, requiredTags.make);
            }
            // Model
            if (requiredTags.model != null) {
                addStringExif(mIfd0Directory, requiredTags.model);
            }
            // ISO
            if (requiredTags.iso != null) {
                try {
                    TiffOutputField isoField = createTiffOutputField(requiredTags.iso);
                    mExifDirectory.add(isoField);
                } catch (Exception e) {
                    // Ignore, ClassCastException was thrown on a Galaxy Nexus w. Android 4.3
                }
            }
            // Exposure
            if (requiredTags.exposure != null) {
                try {
                    TiffOutputField exposureField = createTiffOutputField(requiredTags.exposure);
                    mExifDirectory.add(exposureField);
                } catch (Exception e) {
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // Aperture
            if (requiredTags.aperture != null) {
                try {
                    TiffOutputField apertureField = createTiffOutputField(requiredTags.aperture);
                    mExifDirectory.add(apertureField);
                } catch (Exception e) {
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // Flash
            if (requiredTags.flash != null) {
                try {
                    TiffOutputField flashField = createTiffOutputField(requiredTags.flash);
                    mExifDirectory.add(flashField);
                } catch (Exception e) {
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            // Compressed bits per pixel
            if (requiredTags.compressedBitsPerPixel != null) {
                try {
                    TiffOutputField compressedBitsPerPixelField = createTiffOutputField(
                            requiredTags.compressedBitsPerPixel);
                    mExifDirectory.add(compressedBitsPerPixelField);
                } catch (Exception e) {
                    // Shouldn't happen, but ignore it, if it does
                }
            }
            return this;
        }

        @NonNull
        private TiffOutputField createTiffOutputField(@NonNull TiffField tiffField) {
            return new TiffOutputField(tiffField.getTagInfo(),
                    tiffField.getFieldType(),
                    (int) tiffField.getCount(),
                    tiffField.getByteArrayValue());
        }

        @NonNull
        public Builder setUserComment(String userComment) {
            addUserCommentStringExif(mExifDirectory, userComment);
            return this;
        }

        @NonNull
        public Builder setOrientationFromDegrees(int degrees) {
            byte[] bytes = new byte[1];
            bytes[0] = (byte) rotationToExifOrientation(degrees);
            TiffOutputField orientationOutputField = new TiffOutputField(
                    TiffTagConstants.TIFF_TAG_ORIENTATION, FieldType.SHORT, 1, bytes);
            mIfd0Directory.add(orientationOutputField);
            return this;
        }

        @NonNull
        public Exif build() {
            return new Exif(mTiffOutputSet);
        }

        private void addStringExif(@NonNull TiffOutputDirectory outputDirectory,
                @NonNull TiffField field) throws ImageReadException {
            byte bytes[] = field.getStringValue().getBytes(Charset.forName("US-ASCII"));
            addStringExif(outputDirectory, field.getTagInfo(), bytes);
        }

        private void addUserCommentStringExif(@NonNull TiffOutputDirectory outputDirectory,
                @NonNull String value) {
            // ASCII character code
            byte characterCode[] = new byte[]{0x41, 0x53, 0x43, 0x49, 0x49, 0x00, 0x00, 0x00};

            byte comment[] = value.getBytes(Charset.forName("US-ASCII"));
            byte userComment[] = new byte[characterCode.length + comment.length];

            System.arraycopy(characterCode, 0, userComment, 0, characterCode.length);
            System.arraycopy(comment, 0, userComment, characterCode.length, comment.length);

            addStringExif(outputDirectory, ExifTagConstants.EXIF_TAG_USER_COMMENT, userComment);
        }

        private void addStringExif(TiffOutputDirectory outputDirectory, TagInfo tagInfo,
                byte[] bytes) {
            TiffOutputField outputField = new TiffOutputField(tagInfo, FieldType.ASCII,
                    bytes.length, bytes);
            outputDirectory.add(outputField);
        }

        private static int rotationToExifOrientation(int degrees) {
            int exifOrientation;
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

    public static class RequiredTags {
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
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            final RequiredTags that = (RequiredTags) o;

            return areEqual(make, that.make)
                    && areEqual(model, that.model)
                    && areEqual(iso, that.iso)
                    && areEqual(exposure, that.exposure)
                    && areEqual(aperture, that.aperture)
                    && areEqual(flash, that.flash)
                    && areEqual(compressedBitsPerPixel, that.compressedBitsPerPixel);
        }

        private boolean areEqual(@Nullable TiffField left, @Nullable TiffField right) {
            boolean leftIsNotNull = left != null;
            boolean rightIsNotNull = right != null;
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

    public static class UserCommentBuilder {
        private boolean mAddMake;
        private boolean mAddModel;
        private String mUUID;
        private int mRotationDelta;

        private UserCommentBuilder() {

        }

        public UserCommentBuilder setAddMake(final boolean addMake) {
            mAddMake = addMake;
            return this;
        }

        public UserCommentBuilder setAddModel(final boolean addModel) {
            mAddModel = addModel;
            return this;
        }

        public UserCommentBuilder setUUID(final String UUID) {
            mUUID = UUID;
            return this;
        }

        public UserCommentBuilder setRotationDelta(final int rotationDelta) {
            mRotationDelta = rotationDelta;
            return this;
        }

        @NonNull
        public String build() {
            if (mUUID == null) {
                throw new IllegalStateException("UUID is required for the User Comment");
            }
            return createUserComment();
        }

        @NonNull
        private String createUserComment() {
            final StringBuilder userCommentBuilder = new StringBuilder();
            // Make
            if (mAddMake) {
                userCommentBuilder.append("Make=");
                userCommentBuilder.append(Build.BRAND);
                userCommentBuilder.append(",");
            }
            // Model
            if (mAddModel) {
                userCommentBuilder.append("Model=");
                userCommentBuilder.append(Build.MODEL);
                userCommentBuilder.append(",");
            }
            // Platform
            userCommentBuilder.append("Platform=Android");
            userCommentBuilder.append(",");
            // OS Version
            userCommentBuilder.append("OSVer=");
            userCommentBuilder.append(String.valueOf(Build.VERSION.RELEASE));
            userCommentBuilder.append(",");
            // GiniVision Version
            userCommentBuilder.append("GiniVisionVer=");
            userCommentBuilder.append(BuildConfig.VERSION_NAME.replace(" ", ""));
            userCommentBuilder.append(",");
            // UUID
            userCommentBuilder.append("UUID=");
            userCommentBuilder.append(mUUID);
            userCommentBuilder.append(",");
            // Rotation Delta
            userCommentBuilder.append("RotDeltaDeg=");
            userCommentBuilder.append(mRotationDelta);

            return userCommentBuilder.toString();
        }
    }
}
