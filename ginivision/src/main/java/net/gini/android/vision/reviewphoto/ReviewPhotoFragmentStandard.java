package net.gini.android.vision.reviewphoto;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewPhotoFragmentStandard extends Fragment implements FragmentImplCallback, ReviewPhotoFragmentInterface {

    private ReviewPhotoFragmentImpl mFragmentImpl;

    public static ReviewPhotoFragmentStandard createInstance(Photo photo) {
        ReviewPhotoFragmentStandard fragment = new ReviewPhotoFragmentStandard();
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
