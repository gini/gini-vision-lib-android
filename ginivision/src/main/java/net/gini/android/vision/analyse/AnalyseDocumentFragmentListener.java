package net.gini.android.vision.analyse;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.Document;

public interface AnalyseDocumentFragmentListener {
    void onAnalyzeDocument(Document document);
    void onError(GiniVisionError error);
}
