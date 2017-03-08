package net.gini.android.vision.test;

import android.support.annotation.Nullable;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import net.gini.android.vision.internal.camera.photo.Photo;

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
        mJpegByteArraySubject = new JpegByteArraySubject(failureStrategy, subject.getJpeg());
    }

    public void hasUUIDinUserComment(final String uuid) {
        isNotNull();
        String verb = "has in User Comment UUID";

        if (uuid == null) {
            fail(verb, (Object) null);
            return;
        }

        mJpegByteArraySubject.hasUUIDinUserComment(uuid);
    }

    public void hasRotationDeltaInUserComment(final int rotationDelta) {
        isNotNull();
        mJpegByteArraySubject.hasRotationDeltaInUserComment(rotationDelta);
    }
}
