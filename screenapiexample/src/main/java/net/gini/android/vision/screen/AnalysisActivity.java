package net.gini.android.vision.screen;

import android.content.Intent;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisActivity extends net.gini.android.vision.analysis.AnalysisActivity {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisActivity.class);

    @Override
    public void onAddDataToResult(@NonNull final Intent result) {
        LOG.debug("Add data to result");
        // WIP: networking library poc
    }

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Analyze document");
        // WIP: networking library poc
    }

}
