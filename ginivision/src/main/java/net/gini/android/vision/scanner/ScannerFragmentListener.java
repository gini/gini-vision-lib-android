package net.gini.android.vision.scanner;

import net.gini.android.vision.GiniVisionError;

public interface ScannerFragmentListener {
    void onDocumentAvailable(Document document);

    void onError(GiniVisionError error);
}
