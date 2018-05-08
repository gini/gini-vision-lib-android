package net.gini.android.vision.review.multipage.previews;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class PreviewsAdapter extends FragmentStatePagerAdapter {

    private final ImageMultiPageDocument mMultiPageDocument;

    public PreviewsAdapter(@NonNull final FragmentManager fm,
            @NonNull final ImageMultiPageDocument multiPageDocument) {
        super(fm);
        mMultiPageDocument = multiPageDocument;
    }

    @Override
    public int getCount() {
        return mMultiPageDocument.getDocuments().size();
    }

    @Override
    public int getItemPosition(final Object object) {
        // Required for reloading the visible fragment
        return POSITION_NONE;
    }

    @Override
    public Fragment getItem(final int position) {
        final ImageDocument document =
                mMultiPageDocument.getDocuments().get(position);
        final GiniVisionDocumentError documentError =
                mMultiPageDocument.getErrorForDocument(document);
        String errorMessage = null;
        if (documentError != null) {
            errorMessage = documentError.getMessage();
        }
        return PreviewFragment.createInstance(document, errorMessage);
    }

    public void rotateImageInCurrentItemBy(@NonNull final ViewPager viewPager, final int degrees) {
        final PreviewFragment fragment = (PreviewFragment) instantiateItem(viewPager,
                viewPager.getCurrentItem());
        fragment.rotateImageViewBy(degrees, true);
    }
}
