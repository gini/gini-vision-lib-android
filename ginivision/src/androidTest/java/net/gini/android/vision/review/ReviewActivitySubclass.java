package net.gini.android.vision.review;

import android.content.Intent;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;

public class ReviewActivitySubclass extends ReviewActivity {

    private ListenerHook mListenerHook;

    private Document mShouldAnalyzeDocument = null;
    private Intent mAddDataToResultIntent = null;
    private Document mProceedToAnalysisDocument = null;
    private Document mDocumentReviewedAndAnalyzedDocument = null;

    public void setListenerHook(ListenerHook listenerHook) {
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
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
        if (mListenerHook != null) {
            mListenerHook.onShouldAnalyzeDocument(document);
        } else {
            mShouldAnalyzeDocument = document;
        }
    }

    @Override
    public void onAddDataToResult(@NonNull Intent result) {
        if (mListenerHook != null) {
            mListenerHook.onAddDataToResult(result);
        } else {
            mAddDataToResultIntent = result;
        }
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        super.onProceedToAnalysisScreen(document);
        if (mListenerHook != null) {
            mListenerHook.onProceedToAnalysisScreen(document);
        } else {
            mProceedToAnalysisDocument = document;
        }
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        super.onDocumentReviewedAndAnalyzed(document);
        if (mListenerHook != null) {
            mListenerHook.onDocumentReviewedAndAnalyzed(document);
        } else {
            mDocumentReviewedAndAnalyzedDocument = document;
        }
    }

    public interface ListenerHook {
        void onShouldAnalyzeDocument(@NonNull Document document);

        void onAddDataToResult(@NonNull Intent result);

        void onProceedToAnalysisScreen(@NonNull Document document);

        void onDocumentReviewedAndAnalyzed(@NonNull Document document);
    }
}
