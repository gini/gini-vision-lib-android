package net.gini.android.vision.component.review.compat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.component.R;
import net.gini.android.vision.review.ReviewFragmentListener;

public class ReviewExampleAppCompatActivity extends AppCompatActivity implements
        ReviewFragmentListener {

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";
    private ReviewScreenHandlerAppCompat mReviewScreenHandler;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mReviewScreenHandler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_compat);
        mReviewScreenHandler = new ReviewScreenHandlerAppCompat(this);
        mReviewScreenHandler.onCreate(savedInstanceState);
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {
        mReviewScreenHandler.onShouldAnalyzeDocument(document);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document) {
        mReviewScreenHandler.onProceedToAnalysisScreen(document);
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {
        mReviewScreenHandler.onDocumentReviewedAndAnalyzed(document);
    }

    @Override
    public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation,
            final int newRotation) {
        mReviewScreenHandler.onDocumentWasRotated(document, oldRotation, newRotation);
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        mReviewScreenHandler.onError(error);
    }

    public static Intent newInstance(final Document document, final Context context) {
        final Intent intent = new Intent(context, ReviewExampleAppCompatActivity.class);
        intent.putExtra(ReviewExampleAppCompatActivity.EXTRA_IN_DOCUMENT, document);
        return intent;
    }
}
