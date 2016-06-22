package net.gini.android.vision.analyse;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

class AnalyseDocumentFragmentImpl implements AnalyseDocumentFragmentInterface {

    private static final AnalyseDocumentFragmentListener NO_OP_LISTENER = new AnalyseDocumentFragmentListener() {
        @Override
        public void onAnalyzeDocument(Document document) {
        }

        @Override
        public void onError(GiniVisionError error) {
        }
    };
    private final FragmentImplCallback mFragment;
    private Document mDocument;

    private AnalyseDocumentFragmentListener mListener = NO_OP_LISTENER;

    public AnalyseDocumentFragmentImpl(FragmentImplCallback fragment, Document document) {
        mFragment = fragment;
        mDocument = document;
    }

    public void setListener(AnalyseDocumentFragmentListener listener) {
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
        return null;
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
