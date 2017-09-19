package net.gini.android.vision.analysis;

import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
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
import net.gini.android.vision.internal.document.DocumentRenderer;
import net.gini.android.vision.internal.document.DocumentRendererFactory;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.Size;

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
    private int mFragmentHeight;
    private ViewPropertyAnimatorCompat mHintAnimation;
    private View mHintContainer;
    private ImageView mHintImageView;
    private TextView mHintTextView;
    private List<AnalysisHint> mHints;
    private DocumentRenderer mDocumentRenderer;
    private final Document mDocument;
    private final String mDocumentAnalysisErrorMessage;
    private ImageView mImageDocument;
    private RelativeLayout mLayoutRoot;
    private AnalysisFragmentListener mListener = NO_OP_LISTENER;
    private ProgressBar mProgressActivity;
    private Runnable mHintCycleRunnable;

    private static final int HINT_ANIMATION_DURATION = 500;
    private static final int HINT_START_DELAY = 5000;
    private static final int HINT_CYCLE_INTERVAL = 4000;


    public AnalysisFragmentImpl(FragmentImplCallback fragment, Document document, String documentAnalysisErrorMessage) {
        mFragment = fragment;
        mDocument = document;
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
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        forcePortraitOrientationOnPhones(activity);
        mDocumentRenderer = DocumentRendererFactory.fromDocument(mDocument, activity);
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
        mImageDocument = null;
        stopScanAnimation();
    }

    public void onStart() {
        showHints();
    }

    private void showHints() {
        mHints = generateRandomHintsList();

        mHintCycleRunnable = new Runnable() {
            @Override
            public void run() {
                mHintAnimation = getSlideDownAnimation();
                mHintAnimation.start();
            }
        };
        mHandler.postDelayed(mHintCycleRunnable, HINT_START_DELAY);
    }

    @NonNull
    private ViewPropertyAnimatorCompat getSlideDownAnimation() {
        return ViewCompat.animate(mHintContainer)
                .translationY(mFragmentHeight)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {
                    }

                    @Override
                    public void onAnimationEnd(final View view) {
                        setNextHint();
                        mHintAnimation = getSlideUpAnimation();
                        mHintAnimation.start();
                    }

                    @Override
                    public void onAnimationCancel(final View view) {
                    }
                });
    }

    @NonNull
    private ViewPropertyAnimatorCompat getSlideUpAnimation() {
        return ViewCompat.animate(mHintContainer)
                .translationY(0)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {
                    }

                    @Override
                    public void onAnimationEnd(final View view) {
                        mHandler.postDelayed(mHintCycleRunnable, HINT_CYCLE_INTERVAL);
                    }

                    @Override
                    public void onAnimationCancel(final View view) {
                    }
                });
    }

    private void setNextHint() {
        final AnalysisHint nextHint= getNextHint();
        final Context context = mFragment.getActivity();
        if(context != null) {
            mHintImageView.setImageDrawable(
                    ContextCompat.getDrawable(context, nextHint.getDrawableResource()));
        }
        mHintTextView.setText(nextHint.getTextResource());
    }

    private AnalysisHint getNextHint() {
        final AnalysisHint analysisHint = mHints.remove(0);
        mHints.add(analysisHint);
        return analysisHint;
    }

    private List<AnalysisHint> generateRandomHintsList() {
        List<AnalysisHint> list = AnalysisHint.getArray();
        Collections.shuffle(list, new Random());
        return list;
    }

    void onStop() {
        mHandler.removeCallbacks(mHintCycleRunnable);
        if (mHintAnimation != null) {
            mHintAnimation.cancel();
            mHintContainer.clearAnimation();
            mHintAnimation.setListener(null);
        }
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
                            mListener.onAnalyzeDocument(mDocument);
                        }
                    });
        } else {
            mListener.onAnalyzeDocument(mDocument);
        }
    }

    private void bindViews(@NonNull View view) {
        mLayoutRoot = view.findViewById(R.id.gv_layout_root);
        mImageDocument = view.findViewById(R.id.gv_image_picture);
        mProgressActivity = view.findViewById(R.id.gv_progress_activity);
        mHintImageView = view.findViewById(R.id.gv_analyse_hint_image);
        mHintTextView = view.findViewById(R.id.gv_analyse_hint_text);
        mHintContainer = view.findViewById(R.id.gv_analyse_hint_container);
    }

    private void observerViewTree(@NonNull final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onViewLayoutFinished();
                mFragmentHeight = view.getHeight();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void onViewLayoutFinished() {
        rotateDocumentImageView();
        analyzeDocument();
    }

    private void rotateDocumentImageView() {
        final int rotationForDisplay = mDocumentRenderer.getRotationForDisplay();
        int newWidth = mLayoutRoot.getWidth();
        int newHeight = mLayoutRoot.getHeight();
        if (rotationForDisplay == 90 || rotationForDisplay == 270) {
            newWidth = mLayoutRoot.getHeight();
            newHeight = mLayoutRoot.getWidth();
        }

        final FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
        layoutParams.width = newWidth;
        layoutParams.height = newHeight;
        mImageDocument.setLayoutParams(layoutParams);
        mImageDocument.setRotation(rotationForDisplay);
    }

    private void showDocument() {
        final Size previewSize = new Size(mImageDocument.getWidth(), mImageDocument.getHeight());
        mImageDocument.setImageBitmap(mDocumentRenderer.toBitmap(previewSize));
    }
}
