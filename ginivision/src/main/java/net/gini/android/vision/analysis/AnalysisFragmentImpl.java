package net.gini.android.vision.analysis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;
import net.gini.android.vision.ui.SnackbarError;

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

    private RelativeLayout mLayoutRoot;
    private ImageView mImageDocument;

    public AnalysisFragmentImpl(FragmentImplCallback fragment, Document document) {
        mFragment = fragment;
        // TODO: use Photo.fromDocument() after merging with MSDK-50
        mPhoto = Photo.fromJpeg(document.getJpeg(), 0);
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
        showDocument();
        return view;
    }

    private void bindViews(View view) {
        mLayoutRoot = (RelativeLayout) view.findViewById(R.id.gv_layout_root);
        mImageDocument = (ImageView) view.findViewById(R.id.gv_image_picture);
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
    public void showError(@NonNull String message, @NonNull String buttonTitle, @NonNull View.OnClickListener onClickListener) {
        if (mFragment.getActivity() == null) {
            return;
        }
        SnackbarError.make(mFragment.getActivity(), mLayoutRoot, message, buttonTitle, onClickListener, SnackbarError.LENGTH_INDEFINITE).show();
    }

    @Override
    public void showError(@NonNull String message, int duration) {
        if (mFragment.getActivity() == null || mLayoutRoot == null) {
            return;
        }
        SnackbarError.make(mFragment.getActivity(), mLayoutRoot, message, null, null, duration).show();
    }

    @Override
    public void hideError() {
        if (mLayoutRoot == null) {
            return;
        }
        SnackbarError.hideExisting(mLayoutRoot);
    }
}
