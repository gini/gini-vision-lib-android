package net.gini.android.vision.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import net.gini.android.vision.Document;

public class DocumentSubject extends Subject<DocumentSubject, Document> {

    private final JpegByteArraySubject mJpegByteArraySubject;

    public static SubjectFactory<DocumentSubject, Document> document() {
        return new SubjectFactory<DocumentSubject, Document>() {

            @Override
            public DocumentSubject getSubject(final FailureStrategy fs, final Document that) {
                return new DocumentSubject(fs, that);
            }
        };
    }

    private DocumentSubject(final FailureStrategy failureStrategy,
            @Nullable final Document subject) {
        super(failureStrategy, subject);
        isNotNull();
        //noinspection ConstantConditions
        mJpegByteArraySubject = new JpegByteArraySubject(failureStrategy, subject.getJpeg());
    }

    public void isEqualToDocument(Document other) {
        Document document = getSubject();
        if (document == null) {
            fail("is equal to another Document - subject is null");
        }
        if (other == null) {
            fail("is equal to another Document - comparing to null");
        }

        //noinspection ConstantConditions - null check done above
        Bitmap bitmap = BitmapFactory.decodeByteArray(document.getJpeg(), 0, document.getJpeg().length);
        //noinspection ConstantConditions - null check done above
        Bitmap otherBitmap = BitmapFactory.decodeByteArray(other.getJpeg(), 0, other.getJpeg().length);

        if (!bitmap.sameAs(otherBitmap)) {
            fail("is equal to Document " + other + " - contain different bitmaps");
        } else if (document.getRotationForDisplay() != other.getRotationForDisplay()) {
            fail("is equal to Document " + other + " - have different rotation");
        }
    }

    public void hasSameContentIdInUserCommentAs(Document other) {
        isNotNull();
        String verb = "has same User Comment ContentId";

        if (other == null) {
            fail(verb, (Object) null);
            return;
        }

        mJpegByteArraySubject.hasSameContentIdInUserCommentAs(other.getJpeg());
    }

    public void hasRotationDeltaInUserComment(final int rotationDelta) {
        isNotNull();
        mJpegByteArraySubject.hasRotationDeltaInUserComment(rotationDelta);
    }
}
