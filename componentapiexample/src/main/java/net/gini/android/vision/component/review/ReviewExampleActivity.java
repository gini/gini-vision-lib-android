package net.gini.android.vision.component.review;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.component.R;
import net.gini.android.vision.review.ReviewFragmentListener;

public class ReviewExampleActivity extends Activity implements
        ReviewFragmentListener {

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";
    private ReviewScreenHandler mReviewScreenHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        mReviewScreenHandler = new ReviewScreenHandler(this);
        mReviewScreenHandler.onCreate(savedInstanceState);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mReviewScreenHandler.onActivityResult(requestCode, resultCode, data);
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
        final Intent intent = new Intent(context, ReviewExampleActivity.class);
        intent.putExtra(ReviewExampleActivity.EXTRA_IN_DOCUMENT, document);
        return intent;
    }
}
