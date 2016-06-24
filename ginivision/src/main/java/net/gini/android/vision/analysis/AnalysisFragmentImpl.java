package net.gini.android.vision.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

class AnalysisFragmentImpl implements AnalysisFragmentInterface {

    private static final AnalysisFragmentListener NO_OP_LISTENER = new AnalysisFragmentListener() {
        @Override
        public void onAnalyzeDocument(Document document) {
        }

        @Override
        public void onError(GiniVisionError error) {
        }
    };
    private final FragmentImplCallback mFragment;
    private Document mDocument;

    private AnalysisFragmentListener mListener = NO_OP_LISTENER;

    public AnalysisFragmentImpl(FragmentImplCallback fragment, Document document) {
        mFragment = fragment;
        mDocument = document;
    }

    public void setListener(AnalysisFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        mListener.onAnalyzeDocument(mDocument);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gv_fragment_analysis, container, false);
    }

    public void onDestroy() {
        mDocument = null;
    }

    @Override
    public void startScanAnimation() {

    }

    @Override
    public void stopScanAnimation() {

    }

    @Override
    public void onDocumentAnalyzed() {

    }
}
