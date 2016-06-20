package net.gini.android.vision.reviewphoto;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewPhotoFragmentHelper {

    private static final String ARGS_PHOTO = "GV_PHOTO";

    public static Bundle createArguments(Photo photo) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_PHOTO, photo);
        return arguments;
    }

    static ReviewPhotoFragmentImpl createFragmentImpl(FragmentImplCallback fragment, Bundle arguments) {
        if (arguments != null) {
            Photo photo = arguments.getParcelable(ARGS_PHOTO);
            if (photo != null) {
                return new ReviewPhotoFragmentImpl(fragment, photo);
            } else {
                throw new IllegalStateException("ReviewPhotoFragmentCompat and ReviewPhotoFragmentStandard require a Photo. Use the createInstance() method of these classes for instantiating.");
            }
        } else {
            throw new IllegalStateException("ReviewPhotoFragmentCompat and ReviewPhotoFragmentStandard require a Photo. Use the createInstance() method of these classes for instantiating.");
        }
    }

    public static void setListener(ReviewPhotoFragmentImpl fragmentImpl, Context context) {
        if (context instanceof ReviewPhotoFragmentListener) {
            fragmentImpl.setListener((ReviewPhotoFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement ReviewPhotoFragmentListener.");
        }
    }
}
