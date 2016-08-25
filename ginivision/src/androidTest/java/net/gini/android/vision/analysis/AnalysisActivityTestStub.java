package net.gini.android.vision.analysis;

import android.content.Intent;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;

public class AnalysisActivityTestStub extends AnalysisActivity {

    public Intent addDataToResultIntent = null;
    public Document analyzeDocument = null;
    public boolean finishWasCalled = false;

    @Override
    public void onAnalyzeDocument(@NonNull Document document) {
        analyzeDocument = document;
    }

    @Override
    public void onAddDataToResult(Intent result) {
        addDataToResultIntent = result;
    }

    @Override
    public void finish() {
        finishWasCalled = true;
        super.finish();
    }
}
