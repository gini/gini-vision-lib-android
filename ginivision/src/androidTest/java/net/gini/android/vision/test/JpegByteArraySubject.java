package net.gini.android.vision.test;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import net.gini.android.vision.test.exif.ExifReader;
import net.gini.android.vision.test.exif.ExifReaderException;

class JpegByteArraySubject extends Subject<JpegByteArraySubject, byte[]> {

    // Not true that <"subject"> "verb" <"expected">. "failure message".
    private static final String RAW_MESSAGE_TEMPLATE = "Not true that <%s> %s <%s>. %s.";

    static SubjectFactory<JpegByteArraySubject, byte[]> jpegByteArray() {
        return new SubjectFactory<JpegByteArraySubject, byte[]>() {

            @Override
            public JpegByteArraySubject getSubject(final FailureStrategy fs, final byte[] that) {
                return new JpegByteArraySubject(fs, that);
            }
        };
    }

    JpegByteArraySubject(@NonNull final FailureStrategy failureStrategy,
            @Nullable final byte[] subject) {
        super(failureStrategy, subject);
    }

    void hasUUIDinUserComment(@Nullable  String uuid) {
        isNotNull();
        String verb = "has in User Comment UUID";

        if (uuid == null) {
            fail(verb, (Object) null);
            return;
        }

        final String userComment = readExifUserComment(verb, uuid, getSubject());
        final String uuidInUserComment = getValueForKeyfromUserComment("UUID", userComment);

        if (uuidInUserComment == null) {
            failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, uuid,
                    "It had no UUID in User Comment");
            return;
        }

        if (!uuid.equals(uuidInUserComment)) {
            failWithBadResults(verb, uuid, "was", uuidInUserComment);
        }
    }

    @NonNull
    private String readExifUserComment(@NonNull final String verb, @NonNull final String expected, @NonNull final byte[] jpeg) {
        try {
            ExifReader reader = new ExifReader(jpeg);
            return reader.getUserComment();
        } catch (ExifReaderException e) {
            failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, expected,
                    "An error occurred: " + e.getMessage());
        }
        return "";
    }

    @NonNull
    private String readExifUserComment(@NonNull final byte[] jpeg) {
        try {
            ExifReader reader = new ExifReader(jpeg);
            return reader.getUserComment();
        } catch (ExifReaderException e) {
            failWithRawMessage("Could not read User Comment from <%s> due to error: %s", jpeg, e.getMessage());
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

    void hasSameUUIDinUserCommentAs(@Nullable byte[] jpeg) {
        isNotNull();
        String verb = "has in User Comment same UUID";

        if (jpeg == null) {
            fail(verb, (Object) null);
            return;
        }

        final String userComment = readExifUserComment(getSubject());
        final String expectedUserComment = readExifUserComment(jpeg);
        final String subjectUuid = getValueForKeyfromUserComment("UUID", userComment);
        final String otherUuid = getValueForKeyfromUserComment("UUID", expectedUserComment);

        if (subjectUuid == null && otherUuid != null) {
            failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, otherUuid,
                    "It had no UUID in User Comment");
            return;
        }
        if (otherUuid == null) {
            failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, null,
                    "Target had no UUID in User Comment.");
            return;
        }

        if (!subjectUuid.equals(otherUuid)) {
            failWithBadResults(verb, subjectUuid, "was", otherUuid);
        }
    }
}
