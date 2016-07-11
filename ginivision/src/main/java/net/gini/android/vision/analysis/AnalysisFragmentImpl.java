package net.gini.android.vision.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.photo.Photo;
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
    private Photo mPhoto;

    private AnalysisFragmentListener mListener = NO_OP_LISTENER;

    private ImageView mImageDocument;

    public AnalysisFragmentImpl(FragmentImplCallback fragment, Document document) {
        mFragment = fragment;
        mPhoto = Photo.fromDocument(document);
    }

    public void setListener(AnalysisFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        mListener.onAnalyzeDocument(Document.fromPhoto(mPhoto));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_analysis, container, false);
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
        mImageDocument = (ImageView) view.findViewById(R.id.gv_image_picture);
    }

    public void onStart() {
        showDocument();
    }

    private void showDocument() {
        mImageDocument.setImageBitmap(mPhoto.getBitmapPreview());
    }

    public void onDestroy() {
        mPhoto = null;
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

    @Override
    public void showError(String message, String buttonTitle, View.OnClickListener onClickListener, int duration) {

    }
}
