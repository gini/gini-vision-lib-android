package net.gini.android.vision.analysis;

import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

import java.util.Collections;
import java.util.List;
import java.util.Random;

class AnalysisFragmentImpl implements AnalysisFragmentInterface {

    private static final AnalysisFragmentListener NO_OP_LISTENER = new AnalysisFragmentListener() {
        @Override
        public void onAnalyzeDocument(@NonNull Document document) {
        }

        @Override
        public void onError(@NonNull GiniVisionError error) {
        }
    };

    private final FragmentImplCallback mFragment;
    private ImageView mHintImageView;
    private TextView mHintTextView;
    private List<AnalysisHint> mHints;
    private Photo mPhoto;
    private final String mDocumentAnalysisErrorMessage;
    private ImageView mImageDocument;
    private RelativeLayout mLayoutRoot;
    private AnalysisFragmentListener mListener = NO_OP_LISTENER;
    private ProgressBar mProgressActivity;
    private Runnable mRunnable;

    public AnalysisFragmentImpl(FragmentImplCallback fragment, Document document, String documentAnalysisErrorMessage) {
        mFragment = fragment;
        mPhoto = Photo.fromDocument(document);
        mDocumentAnalysisErrorMessage = documentAnalysisErrorMessage;
    }

    @Override
    public void hideError() {
        if (mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.hideExisting(mLayoutRoot);
    }

    @Override
    public void noExtractionsFound() {

    }

    @Override
    public void onDocumentAnalyzed() {

    }

    @Override
    public void showError(@NonNull String message, int duration) {
        if (mFragment.getActivity() == null || mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, null, null,
                duration).show();
    }

    @Override
    public void showError(@NonNull String message, @NonNull String buttonTitle,
            @NonNull View.OnClickListener onClickListener) {
        if (mFragment.getActivity() == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, buttonTitle,
                onClickListener, ErrorSnackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void startScanAnimation() {
        mProgressActivity.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopScanAnimation() {
        mProgressActivity.setVisibility(View.GONE);
    }

    public void onCreate(Bundle savedInstanceState) {
        forcePortraitOrientationOnPhones(mFragment.getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_analysis, container, false);
        bindViews(view);
        showDocument();
        observerViewTree(view);
        return view;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public void onDestroy() {
        mPhoto = null;
        stopScanAnimation();
    }

    public void onStart() {
        mHints = generateRandomHintsList();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                AnalysisHint nextHint= getNextHint();
                final Context context = mFragment.getActivity();
                mHintImageView.setImageDrawable(ContextCompat.getDrawable(context, nextHint.getDrawableResource()));
                mHintTextView.setText(nextHint.getTextResource());

                mHintImageView.setVisibility(View.VISIBLE);
                mHintTextView.setVisibility(View.VISIBLE);

                mHandler.postDelayed(mRunnable, 4000);
            }
        };
        mHandler.postDelayed(mRunnable, 5000);
    }

    private AnalysisHint getNextHint() {
        if(mHints.isEmpty()) {
            mHints = generateRandomHintsList();
        }
        return mHints.remove(0);
    }

    private List<AnalysisHint> generateRandomHintsList() {
        List<AnalysisHint> list = AnalysisHint.getArray();
        Collections.shuffle(list, new Random());
        return list;
    }

    public void onStop() {
        mHandler.removeCallbacks(mRunnable);
    }

    public void setListener(@Nullable AnalysisFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    @VisibleForTesting
    ImageView getImageDocument() {
        return mImageDocument;
    }

    @VisibleForTesting
    ProgressBar getProgressActivity() {
        return mProgressActivity;
    }

    private void analyzeDocument() {
        if (mFragment.getActivity() == null) {
            return;
        }
        if (mDocumentAnalysisErrorMessage != null) {
            showError(mDocumentAnalysisErrorMessage, mFragment.getActivity().getString(R.string.gv_document_analysis_error_retry),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mListener.onAnalyzeDocument(Document.fromPhoto(mPhoto));
                        }
                    });
        } else {
            mListener.onAnalyzeDocument(Document.fromPhoto(mPhoto));
        }
    }

    private void bindViews(@NonNull View view) {
        mLayoutRoot = view.findViewById(R.id.gv_layout_root);
        mImageDocument = view.findViewById(R.id.gv_image_picture);
        mProgressActivity = view.findViewById(R.id.gv_progress_activity);
        mHintImageView = view.findViewById(R.id.gv_analyse_hint_image);
        mHintTextView = view.findViewById(R.id.gv_analyse_hint_text);
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
        analyzeDocument();
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

    private void showDocument() {
        mImageDocument.setImageBitmap(mPhoto.getBitmapPreview());
    }
}
