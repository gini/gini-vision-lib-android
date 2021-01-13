package net.gini.android.vision.test;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import net.gini.android.vision.internal.camera.photo.JpegByteArraySubject;
import net.gini.android.vision.internal.camera.photo.Photo;

import androidx.annotation.Nullable;

public class PhotoSubject extends Subject<PhotoSubject, Photo> {

    private final JpegByteArraySubject mJpegByteArraySubject;

    public static SubjectFactory<PhotoSubject, Photo> photo() {
        return new SubjectFactory<PhotoSubject, Photo>() {

            @Override
            public PhotoSubject getSubject(final FailureStrategy fs, final Photo that) {
                return new PhotoSubject(fs, that);
            }
        };
    }

    private PhotoSubject(final FailureStrategy failureStrategy,
            @Nullable final Photo subject) {
        super(failureStrategy, subject);
        isNotNull();
        //noinspection ConstantConditions
        mJpegByteArraySubject = new JpegByteArraySubject(failureStrategy, subject.getData());
    }

    public void hasContentIdInUserComment(final String contentId) {
        isNotNull();
        final String verb = "has in User Comment ContentId";

        if (contentId == null) {
            fail(verb, (Object) null);
            return;
        }

        mJpegByteArraySubject.hasContentIdInUserComment(contentId);
    }

    public void hasRotationDeltaInUserComment(final int rotationDelta) {
        isNotNull();
        mJpegByteArraySubject.hasRotationDeltaInUserComment(rotationDelta);
    }

    public void hasSameUserCommentAs(@Nullable final Photo photo) {
        isNotNull();
        mJpegByteArraySubject.hasSameUserCommentAs(photo.getData());
    }
}
