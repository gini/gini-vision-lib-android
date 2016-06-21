package net.gini.android.vision.reviewdocument;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.Document;

public interface ReviewDocumentFragmentListener {
    // callback for subclasses for uploading the photo before it was reviewed, if the photo is not changed
    // no new upload is required
    void onShouldAnalyzeDocument(Document document);

    void onProceedToAnalyzeScreen(Document document);

    void onDocumentReviewedAndAnalyzed(Document document);

    void onError(GiniVisionError error);
}
