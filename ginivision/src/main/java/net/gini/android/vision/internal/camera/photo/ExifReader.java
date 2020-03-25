package net.gini.android.vision.internal.camera.photo;

import static org.apache.commons.imaging.Imaging.getMetadata;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * @suppress
 */
final class ExifReader {

    private final JpegImageMetadata mJpegMetadata;

    static ExifReader forJpeg(@NonNull final byte[] jpeg) {
        try {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) getMetadata(jpeg);
            if (jpegMetadata == null) {
                throw new ExifReaderException("No jpeg metadata found");
            }
            return new ExifReader(jpegMetadata);
        } catch (final IOException | ImageReadException | ClassCastException e) {
            throw new ExifReaderException("Could not read jpeg metadata: " + e.getMessage(), e);
        }
    }

    private ExifReader(@NonNull final JpegImageMetadata jpegMetadata) {
        mJpegMetadata = jpegMetadata;
    }

    @NonNull
    String getUserComment() {
        final TiffField userCommentField = mJpegMetadata.findEXIFValue(
                ExifTagConstants.EXIF_TAG_USER_COMMENT);
        if (userCommentField == null) {
            throw new ExifReaderException("No User Comment found");
        }

        final byte[] rawUserComment = userCommentField.getByteArrayValue();
        if (rawUserComment == null) {
            throw new ExifReaderException("No User Comment found");
        }

        if (rawUserComment.length >= 8) {
            return new String(Arrays.copyOfRange(rawUserComment, 8, rawUserComment.length),
                    Charset.forName("US-ASCII"));
        } else {
            return new String(rawUserComment, Charset.forName("US-ASCII"));
        }
    }

    @Nullable
    static String getValueForKeyFromUserComment(@NonNull final String key,
            @NonNull final String userComment) {
        final String[] keyValuePairs = userComment.split(",");
        for (final String keyValuePair : keyValuePairs) {
            final String[] keyAndValue = keyValuePair.split("=");
            if (keyAndValue.length > 1 && keyAndValue[0].equals(key)) {
                return keyAndValue[1];
            }
        }
        return null;
    }

    int getOrientationAsDegrees() {
        final TiffField orientation = mJpegMetadata.findEXIFValue(
                TiffTagConstants.TIFF_TAG_ORIENTATION);
        if (orientation != null) {
            try {
                return exifOrientationToRotation(orientation.getIntValue());
            } catch (final ImageReadException e) {
                return 0;
            }
        }
        return 0;
    }

    private int exifOrientationToRotation(final int exifOrientation) {
        final int degrees;
        switch (exifOrientation) {
            case 1:
                degrees = 0; // 0CW
                break;
            case 6:
                degrees = 90; // 270CW
                break;
            case 3:
                degrees = 180; // 180CW
                break;
            case 8:
                degrees = 270; // 90CW
                break;
            default:
                degrees = 0; // 0CW
                break;
        }

        return degrees;
    }
}
