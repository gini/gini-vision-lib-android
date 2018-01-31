package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReviewActivity extends net.gini.android.vision.review.ReviewActivity {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewActivity.class);

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAddDataToResult(@NonNull final Intent result) {
        LOG.debug("Add data to result");
        // WIP: networking library poc
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Should analyze document");
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_review");
        // WIP: networking library poc
    }

    @Override
    public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation, final int newRotation) {
        super.onDocumentWasRotated(document, oldRotation, newRotation);
        LOG.debug("Document was rotated");
        // WIP: networking library poc
    }
}

