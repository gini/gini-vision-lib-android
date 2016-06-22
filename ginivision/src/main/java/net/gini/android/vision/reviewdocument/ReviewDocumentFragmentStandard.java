package net.gini.android.vision.reviewdocument;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewDocumentFragmentStandard extends Fragment implements FragmentImplCallback, ReviewDocumentFragmentInterface {

    private ReviewDocumentFragmentImpl mFragmentImpl;

    public static ReviewDocumentFragmentStandard createInstance(Document document) {
        ReviewDocumentFragmentStandard fragment = new ReviewDocumentFragmentStandard();
        fragment.setArguments(ReviewDocumentFragmentHelper.createArguments(document));
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
    public void onDocumentAnalyzed() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.onDocumentAnalyzed();
    }
}
