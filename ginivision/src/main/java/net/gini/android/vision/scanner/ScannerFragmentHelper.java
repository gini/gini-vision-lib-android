package net.gini.android.vision.scanner;

import android.content.Context;

public class ScannerFragmentHelper {

    public static void setListener(ScannerFragmentImpl fragmentImpl, Context context) {
        if (context instanceof ScannerFragmentListener) {
            fragmentImpl.setListener((ScannerFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement ScannerFragmentListener.");
        }
    }
}
