package net.gini.android.vision.analyze;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

class AnalyzeDocumentFragmentHelper {

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";

    public static Bundle createArguments(Document document) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_DOCUMENT, document);
        return arguments;
    }

    static AnalyzeDocumentFragmentImpl createFragmentImpl(FragmentImplCallback fragment, Bundle arguments) {
        if (arguments != null) {
            Document document = arguments.getParcelable(ARGS_DOCUMENT);
            if (document != null) {
                return new AnalyzeDocumentFragmentImpl(fragment, document);
            } else {
                throw new IllegalStateException("AnalyzeDocumentFragmentCompat and AnalyzeDocumentFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
            }
        } else {
            throw new IllegalStateException("AnalyzeDocumentFragmentCompat and AnalyzeDocumentFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
        }
    }

    public static void setListener(AnalyzeDocumentFragmentImpl fragmentImpl, Context context) {
        if (context instanceof AnalyzeDocumentFragmentListener) {
            fragmentImpl.setListener((AnalyzeDocumentFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement AnalyzeDocumentFragmentListener.");
        }
    }
}
