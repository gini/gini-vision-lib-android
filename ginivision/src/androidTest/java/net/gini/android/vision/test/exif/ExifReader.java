package net.gini.android.vision.test.exif;

import static org.apache.commons.imaging.Imaging.getMetadata;

import android.support.annotation.NonNull;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import java.io.IOException;
import java.util.Arrays;

public class ExifReader {

    private final byte[] jpeg;

    public ExifReader(@NonNull final byte[] jpeg) {
        this.jpeg = jpeg;
    }

    @NonNull
    public String getUserComment() {
        try {
            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) getMetadata(jpeg);
            if (jpegMetadata == null) {
                throw new ExifReaderException("No jpeg metadata found");
            }

            final TiffField userCommentField = jpegMetadata.findEXIFValue(
                    ExifTagConstants.EXIF_TAG_USER_COMMENT);

            try {
                final byte[] rawUserComment = userCommentField.getByteArrayValue();
                if (rawUserComment == null) {
                    throw new ExifReaderException("No User Comment found");
                }
                return new String(Arrays.copyOfRange(rawUserComment, 8, rawUserComment.length));
            } catch (ClassCastException e) {
                throw new ExifReaderException("Unexpected type in User Comment: " + e.getMessage(), e);
            }
        } catch (IOException | ImageReadException e) {
            throw new ExifReaderException("Could not read jpeg metadata: " + e.getMessage(), e);
        }
    }
}
