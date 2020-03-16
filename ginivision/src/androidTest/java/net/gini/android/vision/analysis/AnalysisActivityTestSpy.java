package net.gini.android.vision.analysis;

import android.content.Intent;

import net.gini.android.vision.Document;

import androidx.annotation.NonNull;

public class AnalysisActivityTestSpy extends AnalysisActivity {

    public Intent addDataToResultIntent = null;
    public Document analyzeDocument = null;
    public boolean finishWasCalled = false;

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        analyzeDocument = document;
    }

    @Override
    public void onAddDataToResult(final Intent result) {
        addDataToResultIntent = result;
    }

    @Override
    public void finish() {
        finishWasCalled = true;
        super.finish();
    }
}
