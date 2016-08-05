package net.gini.android.vision.test;

import android.content.Intent;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.review.ReviewActivity;

public class NoOpReviewActivity extends ReviewActivity {
    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
    }

    @Override
    public void onAddDataToResult(@NonNull Intent result) {
    }
}
