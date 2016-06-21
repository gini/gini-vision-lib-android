package net.gini.android.vision.scanner;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.photo.Photo;

public interface ScannerFragmentListener {
    void onPhotoTaken(Photo photo);

    void onError(GiniVisionError error);
}
