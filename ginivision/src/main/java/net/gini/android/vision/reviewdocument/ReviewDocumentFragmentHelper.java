package net.gini.android.vision.reviewdocument;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewDocumentFragmentHelper {

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";

    public static Bundle createArguments(Document document) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_DOCUMENT, document);
        return arguments;
    }

    static ReviewDocumentFragmentImpl createFragmentImpl(FragmentImplCallback fragment, Bundle arguments) {
        if (arguments != null) {
            Document document = arguments.getParcelable(ARGS_DOCUMENT);
            if (document != null) {
                return new ReviewDocumentFragmentImpl(fragment, document);
            } else {
                throw new IllegalStateException("ReviewDocumentFragmentCompat and ReviewDocumentFragmentStandard require a Photo. Use the createInstance() method of these classes for instantiating.");
            }
        } else {
            throw new IllegalStateException("ReviewDocumentFragmentCompat and ReviewDocumentFragmentStandard require a Photo. Use the createInstance() method of these classes for instantiating.");
        }
    }

    public static void setListener(ReviewDocumentFragmentImpl fragmentImpl, Context context) {
        if (context instanceof ReviewDocumentFragmentListener) {
            fragmentImpl.setListener((ReviewDocumentFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement ReviewDocumentFragmentListener.");
        }
    }
}
