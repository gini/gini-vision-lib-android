package net.gini.android.vision.analyse;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

public class AnalyseDocumentFragmentStandard extends Fragment implements FragmentImplCallback, AnalyseDocumentFragmentInterface {

    private AnalyseDocumentFragmentImpl mFragmentImpl;

    public static AnalyseDocumentFragmentStandard createInstance(Document document) {
        AnalyseDocumentFragmentStandard fragment = new AnalyseDocumentFragmentStandard();
        fragment.setArguments(AnalyseDocumentFragmentHelper.createArguments(document));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = AnalyseDocumentFragmentHelper.createFragmentImpl(this, getArguments());
        AnalyseDocumentFragmentHelper.setListener(mFragmentImpl, getActivity());
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
    public void startScanAnimation() {
        mFragmentImpl.startScanAnimation();
    }

    @Override
    public void stopScanAnimation() {
        mFragmentImpl.stopScanAnimation();
    }

    @Override
    public void onDocumentAnalyzed() {
        mFragmentImpl.onDocumentAnalyzed();
    }
}
