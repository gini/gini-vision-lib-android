package net.gini.android.vision.review;

import static net.gini.android.vision.test.Helpers.createDocument;
import static net.gini.android.vision.test.Helpers.getTestJpeg;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.test.FragmentHostActivity;

import java.io.IOException;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class ReviewFragmentHostActivityNotListener extends
        FragmentHostActivity<ReviewFragmentCompat> {

    static ReviewFragmentListener sListener;

    @Override
    protected void setListener() {
        if (sListener != null) {
            getFragment().setListener(sListener);
        }
    }

    @Override
    protected ReviewFragmentCompat createFragment() {
        try {
            return ReviewFragmentCompat.createInstance(
                    createDocument(getTestJpeg(), 0, "portrait", "phone", ImageDocument.Source.newCameraSource()));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
