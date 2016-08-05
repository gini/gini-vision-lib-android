package net.gini.android.vision.test;

import android.content.Intent;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.analysis.AnalysisActivity;

public class NoOpAnalysisActivity extends AnalysisActivity {
    @Override
    public void onAnalyzeDocument(@NonNull Document document) {
    }

    @Override
    public void onAddDataToResult(Intent result) {
    }
}
