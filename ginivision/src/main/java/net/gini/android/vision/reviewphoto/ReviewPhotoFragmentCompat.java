package net.gini.android.vision.reviewphoto;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReviewPhotoFragmentCompat extends Fragment {

    private ReviewPhotoFragmentImpl mFragmentImpl = new ReviewPhotoFragmentImpl();

    public void setPhotoWasAnalyzed(boolean photoWasAnalyzed) {
        mFragmentImpl.setPhotoWasAnalyzed(photoWasAnalyzed);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ReviewPhotoFragmentHelper.setListener(mFragmentImpl, context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }
}
