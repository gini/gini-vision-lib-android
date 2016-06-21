package net.gini.android.vision.analyse;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

public class AnalyseDocumentFragmentHelper {

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";

    public static Bundle createArguments(Document document) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_DOCUMENT, document);
        return arguments;
    }

    static AnalyseDocumentFragmentImpl createFragmentImpl(FragmentImplCallback fragment, Bundle arguments) {
        if (arguments != null) {
            Document document = arguments.getParcelable(ARGS_DOCUMENT);
            if (document != null) {
                return new AnalyseDocumentFragmentImpl(fragment, document);
            } else {
                throw new IllegalStateException("AnalyseDocumentFragmentCompat and AnalyseDocumentFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
            }
        } else {
            throw new IllegalStateException("AnalyseDocumentFragmentCompat and AnalyseDocumentFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
        }
    }

    public static void setListener(AnalyseDocumentFragmentImpl fragmentImpl, Context context) {
        if (context instanceof AnalyseDocumentFragmentListener) {
            fragmentImpl.setListener((AnalyseDocumentFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement AnalyseDocumentFragmentListener.");
        }
    }
}
