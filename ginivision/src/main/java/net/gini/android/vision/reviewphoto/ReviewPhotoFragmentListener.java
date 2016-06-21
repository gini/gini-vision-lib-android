package net.gini.android.vision.reviewphoto;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.photo.Photo;

public interface ReviewPhotoFragmentListener {
    // callback for subclasses for uploading the photo before it was reviewed, if the photo is not changed
    // no new upload is required
    void onShouldAnalyzePhoto(Photo photo);

    void onProceedToAnalyzePhotoScreen(Photo photo);

    void onPhotoReviewedAndAnalyzed(Photo photo);

    void onError(GiniVisionError error);
}
