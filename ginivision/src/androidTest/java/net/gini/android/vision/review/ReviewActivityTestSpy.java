package net.gini.android.vision.review;

import android.content.Intent;

import net.gini.android.vision.Document;

import androidx.annotation.NonNull;

public class ReviewActivityTestSpy extends ReviewActivity {

    private ListenerHook mListenerHook;

    private Document mShouldAnalyzeDocument = null;
    private Intent mAddDataToResultIntent = null;
    private Document mProceedToAnalysisDocument = null;
    private Document mDocumentReviewedAndAnalyzedDocument = null;
    private Document mDocumentWasRotatedDocument = null;
    private int mDocumentWasRotatedDegreesOld = Integer.MAX_VALUE;
    private int mDocumentWasRotatedDegreesNew = Integer.MAX_VALUE;

    public void setListenerHook(final ListenerHook listenerHook) {
        mListenerHook = listenerHook;
        if (mShouldAnalyzeDocument != null) {
            mListenerHook.onShouldAnalyzeDocument(mShouldAnalyzeDocument);
        }
        if (mAddDataToResultIntent != null) {
            mListenerHook.onAddDataToResult(mAddDataToResultIntent);
        }
        if (mProceedToAnalysisDocument != null) {
            mListenerHook.onProceedToAnalysisScreen(mProceedToAnalysisDocument);
        }
        if (mDocumentReviewedAndAnalyzedDocument != null) {
            mListenerHook.onDocumentReviewedAndAnalyzed(mDocumentReviewedAndAnalyzedDocument);
        }
        if (mDocumentWasRotatedDocument != null
                && mDocumentWasRotatedDegreesOld != Integer.MAX_VALUE &&
                mDocumentWasRotatedDegreesNew != Integer.MAX_VALUE) {
            mListenerHook.onDocumentWasRotated(mDocumentWasRotatedDocument,
                    mDocumentWasRotatedDegreesOld, mDocumentWasRotatedDegreesNew);
        }
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {
        if (mListenerHook != null) {
            mListenerHook.onShouldAnalyzeDocument(document);
        } else {
            mShouldAnalyzeDocument = document;
        }
    }

    @Override
    public void onAddDataToResult(@NonNull final Intent result) {
        if (mListenerHook != null) {
            mListenerHook.onAddDataToResult(result);
        } else {
            mAddDataToResultIntent = result;
        }
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document) {
        super.onProceedToAnalysisScreen(document);
        if (mListenerHook != null) {
            mListenerHook.onProceedToAnalysisScreen(document);
        } else {
            mProceedToAnalysisDocument = document;
        }
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {
        super.onDocumentReviewedAndAnalyzed(document);
        if (mListenerHook != null) {
            mListenerHook.onDocumentReviewedAndAnalyzed(document);
        } else {
            mDocumentReviewedAndAnalyzedDocument = document;
        }
    }

    @Override
    public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation, final int newRotation) {
        super.onDocumentWasRotated(document, oldRotation, newRotation);
        if (mListenerHook != null) {
            mListenerHook.onDocumentWasRotated(document, oldRotation, newRotation);
        } else {
            mDocumentWasRotatedDocument = document;
            mDocumentWasRotatedDegreesOld = oldRotation;
            mDocumentWasRotatedDegreesNew = newRotation;
        }
    }

    public static abstract class ListenerHook {

        public void onShouldAnalyzeDocument(@NonNull final Document document) {
        }

        public void onAddDataToResult(@NonNull final Intent result) {
        }

        public void onProceedToAnalysisScreen(@NonNull final Document document) {
        }

        public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {
        }

        public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation,
                final int newRotation) {
        }
    }
}
