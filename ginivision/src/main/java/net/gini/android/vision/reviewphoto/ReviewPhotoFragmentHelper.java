package net.gini.android.vision.reviewphoto;

import android.content.Context;

public class ReviewPhotoFragmentHelper {

    public static void setListener(ReviewPhotoFragmentImpl fragmentImpl, Context context) {
        if (context instanceof ReviewPhotoFragmentListener) {
            fragmentImpl.setListener((ReviewPhotoFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement ReviewPhotoFragmentListener.");
        }
    }
}
