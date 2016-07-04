package net.gini.android.vision.analysis;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

class AnalysisFragmentHelper {

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";

    public static Bundle createArguments(Document document) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_DOCUMENT, document);
        return arguments;
    }

    static AnalysisFragmentImpl createFragmentImpl(FragmentImplCallback fragment, Bundle arguments) {
        if (arguments != null) {
            Document document = arguments.getParcelable(ARGS_DOCUMENT);
            if (document != null) {
                return new AnalysisFragmentImpl(fragment, document);
            } else {
                throw new IllegalStateException("AnalysisFragmentCompat and AnalysisFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
            }
        } else {
            throw new IllegalStateException("AnalysisFragmentCompat and AnalysisFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
        }
    }

    public static void setListener(AnalysisFragmentImpl fragmentImpl, Context context) {
        if (context instanceof AnalysisFragmentListener) {
            fragmentImpl.setListener((AnalysisFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement AnalysisFragmentListener.");
        }
    }
}
