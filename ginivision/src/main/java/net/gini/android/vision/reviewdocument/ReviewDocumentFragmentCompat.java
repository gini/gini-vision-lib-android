package net.gini.android.vision.reviewdocument;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewDocumentFragmentCompat extends Fragment implements FragmentImplCallback, ReviewDocumentFragmentInterface {

    private ReviewDocumentFragmentImpl mFragmentImpl;

    public static ReviewDocumentFragmentCompat createInstance(Photo photo) {
        ReviewDocumentFragmentCompat fragment = new ReviewDocumentFragmentCompat();
        fragment.setArguments(ReviewDocumentFragmentHelper.createArguments(photo));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = ReviewDocumentFragmentHelper.createFragmentImpl(this, getArguments());
        ReviewDocumentFragmentHelper.setListener(mFragmentImpl, getActivity());
        mFragmentImpl.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentImpl.onDestroy();
        mFragmentImpl = null;
    }

    @Override
    public void onPhotoAnalyzed() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.onPhotoAnalyzed();
    }
}
