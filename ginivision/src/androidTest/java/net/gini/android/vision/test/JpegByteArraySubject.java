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

    void hasContentIdInUserComment(@Nullable String contentId) {
        isNotNull();
        String verb = "has in User Comment ContentId";

        if (contentId == null) {
            fail(verb, (Object) null);
            return;
        }

        final String userComment = readExifUserComment(verb, contentId, getSubject());
        final String contentIdInUserComment = getValueForKeyfromUserComment("ContentId", userComment);

        if (contentIdInUserComment == null) {
            triggerFailureWithRawMessage(verb, contentId, "It had no ContentId in User Comment");
            return;
        }

        if (!contentId.equals(contentIdInUserComment)) {
            failWithBadResults(verb, contentId, "was", contentIdInUserComment);
        }
    }

    private void triggerFailureWithRawMessage(String verb, String expected, String failureMessage) {
        failWithRawMessage(RAW_MESSAGE_TEMPLATE, getSubject(), verb, expected, failureMessage);
    }

    @NonNull
    private String readExifUserComment(@NonNull final String verb, @NonNull final String expected, @NonNull final byte[] jpeg) {
        try {
            ExifReader reader = new ExifReader(jpeg);
            return reader.getUserComment();
        } catch (ExifReaderException e) {
            triggerFailureWithRawMessage(verb, expected, "An error occurred: " + e.getMessage());
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

    void hasSameContentIdInUserCommentAs(@Nullable byte[] jpeg) {
        isNotNull();
        String verb = "has in User Comment same ContentId";

        if (jpeg == null) {
            fail(verb, (Object) null);
            return;
        }

        final String userComment = readExifUserComment(getSubject());
        final String expectedUserComment = readExifUserComment(jpeg);
        final String subjectUuid = getValueForKeyfromUserComment("ContentId", userComment);
        final String otherUuid = getValueForKeyfromUserComment("ContentId", expectedUserComment);

        if (subjectUuid == null && otherUuid != null) {
            triggerFailureWithRawMessage(verb, otherUuid, "It had no ContentId in User Comment");
            return;
        }
        if (otherUuid == null) {
            triggerFailureWithRawMessage(verb, null, "Target had no ContentId in User Comment");
            return;
        }

        if (!subjectUuid.equals(otherUuid)) {
            failWithBadResults(verb, subjectUuid, "was", otherUuid);
        }
    }

    void hasRotationDeltaInUserComment(final int rotationDelta) {
        isNotNull();
        String verb = "has in User Comment rotation delta";

        final String userComment = readExifUserComment(getSubject());
        final String subjectRotDeltaDegString = getValueForKeyfromUserComment("RotDeltaDeg", userComment);

        if (subjectRotDeltaDegString == null) {
            triggerFailureWithRawMessage(verb, String.valueOf(rotationDelta),
                    "Target had no rotation delta in User Comment");
            return;
        }

        int subjectRotDeltaDeg = Integer.parseInt(subjectRotDeltaDegString);
        if (subjectRotDeltaDeg != rotationDelta) {
            failWithBadResults(verb, rotationDelta, "was", subjectRotDeltaDeg);
        }
    }

    void hasSameUserCommentAs(@Nullable final byte[] other) {
        isNotNull();
        String verb = "has User Comment";

        if (other == null) {
            fail(verb, (Object) null);
            return;
        }

        final String subjectUserComment = readExifUserComment(getSubject());
        final String otherUserComment = readExifUserComment(other);

        if (!otherUserComment.equals(subjectUserComment)) {
            failWithBadResults(verb, otherUserComment, "was", subjectUserComment);
        }
    }
}
