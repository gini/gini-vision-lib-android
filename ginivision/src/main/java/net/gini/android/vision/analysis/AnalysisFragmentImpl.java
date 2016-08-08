package net.gini.android.vision.analysis;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
        public void onAnalyzeDocument(@NonNull Document document) {
        }

        @Override
        public void onError(@NonNull GiniVisionError error) {
        }
    };

    private static final long SCAN_ANIM_DURATION = 900;

    private final FragmentImplCallback mFragment;
    private Photo mPhoto;
    private AnalysisFragmentListener mListener = NO_OP_LISTENER;

    private RelativeLayout mLayoutRoot;
    private ImageView mImageDocument;
    private ProgressBar mProgressActivity;

    public AnalysisFragmentImpl(FragmentImplCallback fragment, Document document) {
        mFragment = fragment;
        mPhoto = Photo.fromDocument(document);
    }

    public void setListener(@Nullable AnalysisFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_analysis, container, false);
        bindViews(view);
        showDocument();
        observerViewTree(view);
        return view;
    }



    public void onStart() {
        mListener.onAnalyzeDocument(Document.fromPhoto(mPhoto));
    }

    public void onStop() {
    }

    private void bindViews(@NonNull View view) {
        mLayoutRoot = (RelativeLayout) view.findViewById(R.id.gv_layout_root);
        mImageDocument = (ImageView) view.findViewById(R.id.gv_image_picture);
        mProgressActivity = (ProgressBar) view.findViewById(R.id.gv_progress_activity);
    }

    private void showDocument() {
        mImageDocument.setImageBitmap(mPhoto.getBitmapPreview());
    }

    private void observerViewTree(@NonNull final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onViewLayoutFinished();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void onViewLayoutFinished() {
        rotateDocumentImageView();
    }

    private void rotateDocumentImageView() {
        int newWidth = mLayoutRoot.getWidth();
        int newHeight = mLayoutRoot.getHeight();
        if (mPhoto.getRotationForDisplay() == 90 || mPhoto.getRotationForDisplay() == 270) {
            newWidth = mLayoutRoot.getHeight();
            newHeight = mLayoutRoot.getWidth();
        }

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
        layoutParams.width = newWidth;
        layoutParams.height = newHeight;
        mImageDocument.setLayoutParams(layoutParams);
        mImageDocument.setRotation(mPhoto.getRotationForDisplay());
    }

    public void onDestroy() {
        mPhoto = null;
        stopScanAnimation();
    }

    @Override
    public void startScanAnimation() {
        mProgressActivity.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopScanAnimation() {
        mProgressActivity.setVisibility(View.GONE);
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
