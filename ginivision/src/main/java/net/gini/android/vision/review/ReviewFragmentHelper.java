package net.gini.android.vision.review;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

class ReviewFragmentHelper {

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";

    public static Bundle createArguments(@NonNull Document document) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_DOCUMENT, document);
        return arguments;
    }

    static ReviewFragmentImpl createFragmentImpl(@NonNull FragmentImplCallback fragment, @NonNull Bundle arguments) {
        Document document = arguments.getParcelable(ARGS_DOCUMENT);
        if (document != null) {
            return new ReviewFragmentImpl(fragment, document);
        } else {
            throw new IllegalStateException("ReviewFragmentCompat and ReviewFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
        }
    }

    public static void setListener(@NonNull ReviewFragmentImpl fragmentImpl, @NonNull Context context) {
        if (context instanceof ReviewFragmentListener) {
            fragmentImpl.setListener((ReviewFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement ReviewFragmentListener.");
        }
    }
}
