package net.gini.android.vision.analysis;

import com.google.common.truth.FailureStrategy;
import com.google.common.truth.Subject;
import com.google.common.truth.SubjectFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.gini.android.vision.Document;

import javax.annotation.Nullable;

public class DocumentSubject extends Subject<DocumentSubject, Document> {

    public static SubjectFactory<DocumentSubject, Document> document() {
        return new SubjectFactory<DocumentSubject, Document>() {
            @Override
            public DocumentSubject getSubject(FailureStrategy fs, Document that) {
                return new DocumentSubject(fs, that);
            }
        };
    }

    public DocumentSubject(FailureStrategy failureStrategy, @Nullable Document subject) {
        super(failureStrategy, subject);
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
}
