package net.gini.android.vision.reviewdocument;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewDocumentFragmentHelper {

    private static final String ARGS_PHOTO = "GV_PHOTO";

    public static Bundle createArguments(Photo photo) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_PHOTO, photo);
        return arguments;
    }

    static ReviewDocumentFragmentImpl createFragmentImpl(FragmentImplCallback fragment, Bundle arguments) {
        if (arguments != null) {
            Photo photo = arguments.getParcelable(ARGS_PHOTO);
            if (photo != null) {
                return new ReviewDocumentFragmentImpl(fragment, photo);
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
