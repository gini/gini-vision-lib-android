package net.gini.android.vision.internal.camera.photo;

import static org.apache.commons.imaging.Imaging.getMetadata;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;

import java.io.IOException;
import java.util.Arrays;

public class PhotoSubject extends Subject<PhotoSubject, Photo> {

    // Not true that <"subject"> "verb" <"expected">. It "failure message".
    private static final String RAW_MESSAGE_TEMPLATE = "Not true that <%s> %s <%s>. It %s.";

    static SubjectFactory<PhotoSubject, Photo> photo() {
        return new SubjectFactory<PhotoSubject, Photo>() {

            @Override
            public PhotoSubject getSubject(final FailureStrategy fs, final Photo that) {
                return new PhotoSubject(fs, that);
            }
        };
    }

    public PhotoSubject(final FailureStrategy failureStrategy,
            @Nullable final Photo subject) {
        super(failureStrategy, subject);
    }

    public void hasUUIDinUserComment(String uuid) {
        isNotNull();
        String verb = "has in User Comment UUID";

        if (uuid == null) {
            fail(verb, uuid);
            return;
        }

        final String userComment = readExifUserComment(verb, uuid);
        final String uuidInUserComment = getValueForKeyfromUserComment("UUID", userComment);

        if (uuidInUserComment == null) {
            failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, uuid,
                    "had no UUID in User Comment");
            return;
        }

        if (!uuid.equals(uuidInUserComment)) {
            failWithBadResults(verb, uuid, "was", uuidInUserComment);
        }
    }

    @NonNull
    private String readExifUserComment(@NonNull final String verb, @NonNull final String expected) {
        try {
            final Photo photo = getSubject();
            if (photo.getJpeg() == null) {
                failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, expected,
                        "had no jpeg byte array");
            }

            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) getMetadata(photo.getJpeg());
            if (jpegMetadata == null) {
                failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, expected,
                        "had no jpeg metadata");
                return "";
            }

            final TiffField userCommentField = jpegMetadata.findEXIFValue(
                    ExifTagConstants.EXIF_TAG_USER_COMMENT);

            try {
                final byte[] rawUserComment = userCommentField.getByteArrayValue();
                if (rawUserComment == null) {
                    failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, expected,
                            "had null value in User Comment");
                    return "";
                }
                return new String(Arrays.copyOfRange(rawUserComment, 8, rawUserComment.length));
            } catch (ClassCastException e) {
                failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, expected,
                        "had unexpected value type in User Comment: " + e.getMessage());
            }
        } catch (IOException | ImageReadException e) {
            failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, expected,
                    "could not read jpeg metadata: " + e.getMessage());
        }
        return "";
    }

    @Nullable
    private String getValueForKeyfromUserComment(@NonNull final String key, @NonNull final String userComment) {
        String[] keyValuePairs = userComment.split(",");
        for (final String keyValuePair : keyValuePairs) {
            String[] keyAndValue = keyValuePair.split("=");
            if (keyAndValue[0].equals(key)) {
                return keyAndValue[1];
            }
        }
        return null;
    }
}
