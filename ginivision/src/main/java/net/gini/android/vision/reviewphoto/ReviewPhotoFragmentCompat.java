package net.gini.android.vision.reviewphoto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewPhotoFragmentCompat extends Fragment implements FragmentImplCallback, ReviewPhotoFragmentInterface {

    private ReviewPhotoFragmentImpl mFragmentImpl;

    public static ReviewPhotoFragmentCompat createInstance(Photo photo) {
        ReviewPhotoFragmentCompat fragment = new ReviewPhotoFragmentCompat();
        fragment.setArguments(ReviewPhotoFragmentHelper.createArguments(photo));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = ReviewPhotoFragmentHelper.createFragmentImpl(this, getArguments());
        ReviewPhotoFragmentHelper.setListener(mFragmentImpl, getActivity());
        mFragmentImpl.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onPhotoAnalyzed() {
        mFragmentImpl.onPhotoAnalyzed();
    }
}
