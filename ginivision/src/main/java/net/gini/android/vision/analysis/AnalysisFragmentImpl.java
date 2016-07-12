package net.gini.android.vision.analysis;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;
import net.gini.android.vision.ui.SnackbarError;
import net.gini.android.vision.util.promise.SimpleDeferred;
import net.gini.android.vision.util.promise.SimplePromise;

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
    private SimpleDeferred mStartAnimationDeferred = new SimpleDeferred();

    private RelativeLayout mLayoutRoot;
    private ImageView mImageDocument;
    private ImageView mImageScannerLine;

    private ValueAnimator mScanAnimation;
    private boolean mStopped = true;

    public AnalysisFragmentImpl(FragmentImplCallback fragment, Document document) {
        mFragment = fragment;
        // TODO: use Photo.fromDocument() after merging with MSDK-50
        mPhoto = Photo.fromJpeg(document.getJpeg(), 0);
    }

    public void setListener(@Nullable AnalysisFragmentListener listener) {
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
        observerViewTree(view);
        return view;
    }

    public void onStart() {
        mStopped = false;
    }

    public void onStop() {
        mStopped = true;
    }

    private void bindViews(@NonNull View view) {
        mLayoutRoot = (RelativeLayout) view.findViewById(R.id.gv_layout_root);
        mImageDocument = (ImageView) view.findViewById(R.id.gv_image_picture);
        mImageScannerLine = (ImageView) view.findViewById(R.id.gv_image_scanner_line);
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
        mStartAnimationDeferred.resolve();
    }

    public void onDestroy() {
        mPhoto = null;
        stopScanAnimation();
    }

    @Override
    public void startScanAnimation() {
        mStartAnimationDeferred.promise().then(new SimplePromise.DoneCallback() {
            @Override
            public void onDone() {
                initScanAnimation();
                startScanAnimationInternal();
            }
        });
    }

    private void initScanAnimation() {
        if (mScanAnimation != null) {
            return;
        }
        mScanAnimation = ObjectAnimator.ofFloat(mImageScannerLine, "translationY", 0, mImageDocument.getHeight() - ((RelativeLayout.LayoutParams) mImageScannerLine.getLayoutParams()).bottomMargin * 2);
        mScanAnimation.setDuration(SCAN_ANIM_DURATION);
        mScanAnimation.setRepeatMode(ObjectAnimator.REVERSE);
    }

    private void startScanAnimationInternal() {
        if (mScanAnimation == null) {
            return;
        }
        mScanAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        if (!mScanAnimation.isRunning()) {
            mScanAnimation.start();
        }
    }

    @Override
    public void stopScanAnimation() {
        stopScanAnimationInternal();
    }

    private void stopScanAnimationInternal() {
        if (mScanAnimation == null) {
            return;
        }
        mScanAnimation.setRepeatCount(0);
        mScanAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mScanAnimation.removeListener(this);
                if (mStopped) {
                    return;
                }
                resetScannerLineWithAnimation();
            }
        });
    }

    private void resetScannerLineWithAnimation() {
        ObjectAnimator resetAnimation = ObjectAnimator.ofFloat(mImageScannerLine, "translationY", 0);
        resetAnimation.setDuration(SCAN_ANIM_DURATION);
        resetAnimation.start();
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
