package net.gini.android.vision.review;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

final class ReviewFragmentHelper{

    private static final String ARGS_DOCUMENT = "GV_ARGS_DOCUMENT";

    public static Bundle createArguments(@NonNull final Document document) {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_DOCUMENT, document);
        return arguments;
    }

    static ReviewFragmentImpl createFragmentImpl(@NonNull final FragmentImplCallback fragment,
            @NonNull final Bundle arguments) {
        final Document document = arguments.getParcelable(ARGS_DOCUMENT);
        if (document != null) {
            return new ReviewFragmentImpl(fragment, document);
        } else {
            throw new IllegalStateException(
                    "ReviewFragmentCompat and ReviewFragmentStandard require a Document. Use the createInstance() method of these classes for instantiating.");
        }
    }

    public static void setListener(@NonNull final ReviewFragmentImpl fragmentImpl,
            @NonNull final Context context) {
        if (context instanceof ReviewFragmentListener) {
            fragmentImpl.setListener((ReviewFragmentListener) context);
        } else {
            throw new IllegalStateException(
                    "Hosting activity must implement ReviewFragmentListener.");
        }
    }

    private ReviewFragmentHelper() {
    }
}
