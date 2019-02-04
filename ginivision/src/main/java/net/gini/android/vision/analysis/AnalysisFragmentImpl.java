package net.gini.android.vision.analysis;

import static net.gini.android.vision.internal.network.NetworkRequestsManager.isCancellation;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;
import static net.gini.android.vision.internal.util.FileImportHelper.showAlertIfOpenWithDocument;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.internal.camera.photo.ParcelableMemoryCache;
import net.gini.android.vision.internal.document.DocumentRenderer;
import net.gini.android.vision.internal.document.DocumentRendererFactory;
import net.gini.android.vision.internal.network.AnalysisNetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestsManager;
import net.gini.android.vision.internal.storage.ImageDiskStore;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.Size;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jersey.repackaged.jsr166e.CompletableFuture;

class AnalysisFragmentImpl implements AnalysisFragmentInterface {

    protected static final Logger LOG = LoggerFactory.getLogger(AnalysisFragmentImpl.class);
    private static final String PARCELABLE_MEMORY_CACHE_TAG = "ANALYSIS_FRAGMENT";

    private static final AnalysisFragmentListener NO_OP_LISTENER = new AnalysisFragmentListener() {
        @Override
        public void onAnalyzeDocument(@NonNull final Document document) {
        }

        @Override
        public void onError(@NonNull final GiniVisionError error) {
        }

        @Override
        public void onExtractionsAvailable(
                @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {

        }

        @Override
        public void onProceedToNoExtractionsScreen(@NonNull final Document document) {

        }
    };

    private final FragmentImplCallback mFragment;
    private int mFragmentHeight;
    private TextView mAnalysisMessageTextView;
    private ViewPropertyAnimatorCompat mHintHeadlineAnimation;
    private ViewPropertyAnimatorCompat mHintAnimation;
    private View mHintContainer;
    private ImageView mHintImageView;
    private TextView mHintTextView;
    private TextView mHintHeadlineTextView;
    private List<AnalysisHint> mHints;
    private DocumentRenderer mDocumentRenderer;
    private final GiniVisionMultiPageDocument<GiniVisionDocument, GiniVisionDocumentError>
            mMultiPageDocument;
    private final String mDocumentAnalysisErrorMessage;
    private ImageView mImageDocument;
    private RelativeLayout mLayoutRoot;
    private AnalysisFragmentListener mListener = NO_OP_LISTENER;
    private ProgressBar mProgressActivity;
    private Runnable mHintStartRunnable;
    private Runnable mHintCycleRunnable;
    private LinearLayout mPdfOverlayLayout;
    private TextView mPdfTitleTextView;
    private TextView mPdfPageCountTextView;
    private LinearLayout mAnalysisOverlay;

    private static final int HINT_ANIMATION_DURATION = 500;
    private static final int HINT_START_DELAY = 5000;
    private static final int HINT_CYCLE_INTERVAL = 4000;
    private boolean mStopped;
    private boolean mAnalysisCompleted;

    AnalysisFragmentImpl(final FragmentImplCallback fragment,
            @NonNull final Document document,
            final String documentAnalysisErrorMessage) {
        mFragment = fragment;
        mMultiPageDocument = asMultiPageDocument(document);
        // Tag the documents to be able to clean up the automatically parcelled data
        if (document instanceof GiniVisionDocument) {
            ((GiniVisionDocument) document).setParcelableMemoryCacheTag(
                    PARCELABLE_MEMORY_CACHE_TAG);
        }
        mMultiPageDocument.setParcelableMemoryCacheTag(PARCELABLE_MEMORY_CACHE_TAG);
        mDocumentAnalysisErrorMessage = documentAnalysisErrorMessage;
    }

    @SuppressWarnings("unchecked")
    private GiniVisionMultiPageDocument<GiniVisionDocument,
            GiniVisionDocumentError> asMultiPageDocument(@NonNull final Document document) {
        if (!(document instanceof GiniVisionMultiPageDocument)) {
            return DocumentFactory.newMultiPageDocument((GiniVisionDocument) document);
        } else {
            return (GiniVisionMultiPageDocument) document;
        }
    }

    @Override
    public void hideError() {
        if (mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.hideExisting(mLayoutRoot);
    }

    @Override
    public void onNoExtractionsFound() {

    }

    @Override
    public void onDocumentAnalyzed() {
        mAnalysisCompleted = true;
        clearSavedImages();
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        if (mFragment.getActivity() == null || mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, null, null,
                duration).show();
    }

    @Override
    public void showError(@NonNull final String message, @NonNull final String buttonTitle,
            @NonNull final View.OnClickListener onClickListener) {
        if (mFragment.getActivity() == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, buttonTitle,
                onClickListener, ErrorSnackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void startScanAnimation() {
        mProgressActivity.setVisibility(View.VISIBLE);
        mAnalysisMessageTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopScanAnimation() {
        mProgressActivity.setVisibility(View.GONE);
        mAnalysisMessageTextView.setVisibility(View.GONE);
    }

    public void onCreate(final Bundle savedInstanceState) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        forcePortraitOrientationOnPhones(activity);
        final GiniVisionDocument documentToRender = getFirstDocument();
        if (documentToRender != null) {
            mDocumentRenderer = DocumentRendererFactory.fromDocument(documentToRender);
        }
        mHints = generateRandomHintsList();
    }

    @NonNull
    private GiniVisionDocument getFirstDocument() {
        return mMultiPageDocument.getDocuments().get(0);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_analysis, container, false);
        bindViews(view);
        return view;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    public void onDestroy() {
        mImageDocument = null; // NOPMD
        stopScanAnimation();
        if (!mAnalysisCompleted) {
            deleteUploadedDocuments();
        } else if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getImageMultiPageDocumentMemoryStore()
                    .clear();
        }
        final Activity activity = mFragment.getActivity();
        if (activity != null && activity.isFinishing()) {
            clearParcelableMemoryCache();
        }
    }

    private void clearParcelableMemoryCache() {
        // Remove data from the memory cache. The data had been added when the document in the
        // arguments was automatically parcelled when the activity was stopped
        ParcelableMemoryCache.getInstance().removeEntriesWithTag(PARCELABLE_MEMORY_CACHE_TAG);
    }

    private void deleteUploadedDocuments() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                networkRequestsManager.cancel(mMultiPageDocument);
                networkRequestsManager.delete(mMultiPageDocument)
                        .handle(new CompletableFuture.BiFun<NetworkRequestResult<
                                GiniVisionDocument>, Throwable, Void>() {
                            @Override
                            public Void apply(
                                    final NetworkRequestResult<GiniVisionDocument> requestResult,
                                    final Throwable throwable) {
                                // Delete PDF partial documents here because the Camera Screen
                                // doesn't keep references to them
                                if (mMultiPageDocument.getType() == Document.Type.PDF_MULTI_PAGE) {
                                    for (final Object document
                                            : mMultiPageDocument.getDocuments()) {
                                        final GiniVisionDocument giniVisionDocument =
                                                (GiniVisionDocument) document;
                                        networkRequestsManager.cancel(giniVisionDocument);
                                        networkRequestsManager.delete(giniVisionDocument);
                                    }
                                }
                                return null;
                            }
                        });
            }
        }
    }

    public void onStart() {
        mStopped = false;
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }

        clearParcelableMemoryCache();

        startScanAnimation();
        LOG.debug("Loading document data");
        mMultiPageDocument.loadData(activity,
                new AsyncCallback<byte[], Exception>() {
                    @Override
                    public void onSuccess(final byte[] result) {
                        LOG.debug("Document data loaded");
                        if (mStopped) {
                            return;
                        }
                        observeViewTree();
                    }

                    @Override
                    public void onError(final Exception exception) {
                        LOG.error("Failed to load document data", exception);
                        if (mStopped) {
                            return;
                        }
                        mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.ANALYSIS,
                                "An error occurred while loading the document."));
                    }

                    @Override
                    public void onCancelled() {
                        // Not used
                    }
                });
        if (getFirstDocument().getType() == Document.Type.IMAGE) {
            showHints();
        }
    }

    private void showHints() {
        mHintCycleRunnable = new Runnable() {
            @Override
            public void run() {
                mHintAnimation = getSlideDownAnimation();
                mHintAnimation.start();
            }
        };
        mHintStartRunnable = new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mHintHeadlineTextView.getText())) {
                    mHintHeadlineAnimation = getHintHeadlineSlideDownAnimation();
                    mHintHeadlineAnimation.start();
                }
                mHandler.post(mHintCycleRunnable);
            }
        };
        mHandler.postDelayed(mHintStartRunnable, HINT_START_DELAY);
    }

    private ViewPropertyAnimatorCompat getHintHeadlineSlideDownAnimation() {
        return ViewCompat.animate(mHintHeadlineTextView)
                .translationY(mFragmentHeight)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        showHeadlineText();
                        mHintHeadlineAnimation = getHintHeadlineSlideUpAnimation();
                        mHintHeadlineAnimation.start();
                    }
                });
    }

    private void showHeadlineText() {
        mHintHeadlineTextView.setText(R.string.gv_analysis_hint_headline);
    }

    private ViewPropertyAnimatorCompat getHintHeadlineSlideUpAnimation() {
        return ViewCompat.animate(mHintHeadlineTextView)
                .translationY(0)
                .setDuration(HINT_ANIMATION_DURATION);
    }

    @NonNull
    private ViewPropertyAnimatorCompat getSlideDownAnimation() {
        return ViewCompat.animate(mHintContainer)
                .translationY(mFragmentHeight)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        setNextHint();
                        mHintAnimation = getSlideUpAnimation();
                        mHintAnimation.start();
                    }
                });
    }

    @NonNull
    private ViewPropertyAnimatorCompat getSlideUpAnimation() {
        return ViewCompat.animate(mHintContainer)
                .translationY(0)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        mHandler.postDelayed(mHintCycleRunnable, HINT_CYCLE_INTERVAL);
                    }
                });
    }

    private void setNextHint() {
        final AnalysisHint nextHint = getNextHint();
        final Context context = mFragment.getActivity();
        if (context != null) {
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
        final List<AnalysisHint> list = AnalysisHint.getArray();
        Collections.shuffle(list, new Random());
        return list;
    }

    void onStop() {
        mStopped = true;
        mHandler.removeCallbacks(mHintStartRunnable);
        mHandler.removeCallbacks(mHintCycleRunnable);
        if (mHintAnimation != null) {
            mHintAnimation.cancel();
            mHintContainer.clearAnimation();
            mHintAnimation.setListener(null);
        }
        if (mHintHeadlineAnimation != null) {
            mHintHeadlineAnimation.cancel();
            mHintHeadlineTextView.clearAnimation();
            mHintHeadlineAnimation.setListener(null);
        }
    }

    @Override
    public void setListener(@NonNull final AnalysisFragmentListener listener) {
        mListener = listener;
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
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        showAlertIfOpenWithDocument(activity, mMultiPageDocument, mFragment)
                .thenRun(new Runnable() {
                    @Override
                    public void run() {
                        if (mDocumentAnalysisErrorMessage != null) {
                            showError(mDocumentAnalysisErrorMessage,
                                    activity.getString(
                                            R.string.gv_document_analysis_error_retry),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(final View v) {
                                            doAnalyzeDocument();
                                        }
                                    });
                        } else {
                            doAnalyzeDocument();
                        }
                    }
                });
    }

    private void doAnalyzeDocument() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                startScanAnimation();
                GiniVisionDebug.writeDocumentToFile(activity, mMultiPageDocument, "_for_analysis");
                for (final Object document : mMultiPageDocument.getDocuments()) {
                    final GiniVisionDocument giniVisionDocument = (GiniVisionDocument) document;
                    networkRequestsManager.upload(activity, giniVisionDocument);
                }
                networkRequestsManager.analyze(mMultiPageDocument)
                        .handle(new CompletableFuture.BiFun<AnalysisNetworkRequestResult<
                                GiniVisionMultiPageDocument>, Throwable, Void>() {
                            @Override
                            public Void apply(
                                    final AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>
                                            requestResult,
                                    final Throwable throwable) {
                                stopScanAnimation();
                                if (throwable != null && !isCancellation(throwable)) {
                                    handleAnalysisError();
                                } else if (requestResult != null) {
                                    mAnalysisCompleted = true;
                                    final Map<String, GiniVisionSpecificExtraction> extractions =
                                            requestResult.getAnalysisResult().getExtractions();
                                    if (extractions.isEmpty()) {
                                        mListener.onProceedToNoExtractionsScreen(
                                                mMultiPageDocument);
                                    } else {
                                        mListener.onExtractionsAvailable(extractions);
                                    }
                                    // Remove all stored images
                                    clearSavedImages();
                                }
                                return null;
                            }
                        });
            } else {
                mListener.onAnalyzeDocument(getFirstDocument());
            }
        } else {
            mListener.onAnalyzeDocument(getFirstDocument());
        }
    }

    private void handleAnalysisError() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        showError(activity.getString(R.string.gv_document_analysis_error),
                activity.getString(R.string.gv_document_analysis_error_retry),
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        doAnalyzeDocument();
                    }
                });
    }

    private void clearSavedImages() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        ImageDiskStore.clear(activity);
    }

    private void bindViews(@NonNull final View view) {
        mLayoutRoot = view.findViewById(R.id.gv_layout_root);
        mImageDocument = view.findViewById(R.id.gv_image_picture);
        mProgressActivity = view.findViewById(R.id.gv_progress_activity);
        mHintImageView = view.findViewById(R.id.gv_analysis_hint_image);
        mHintTextView = view.findViewById(R.id.gv_analysis_hint_text);
        mHintContainer = view.findViewById(R.id.gv_analysis_hint_container);
        mAnalysisMessageTextView = view.findViewById(R.id.gv_analysis_message);
        mPdfOverlayLayout = view.findViewById(R.id.gv_pdf_info);
        mPdfTitleTextView = view.findViewById(R.id.gv_pdf_filename);
        mPdfPageCountTextView = view.findViewById(R.id.gv_pdf_page_count);
        mAnalysisOverlay = view.findViewById(R.id.gv_analysis_overlay);
        mHintHeadlineTextView = view.findViewById(R.id.gv_analysis_hint_headline);
    }

    private void observeViewTree() {
        final View view = mFragment.getView();
        if (view == null) {
            return;
        }
        LOG.debug("Observing the view layout");
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onViewLayoutFinished();
                        mFragmentHeight = view.getHeight();
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
        view.requestLayout();
    }

    private void onViewLayoutFinished() {
        LOG.debug("View layout finished");
        showPdfInfoForPdfDocument();
        showDocument();
        analyzeDocument();
    }

    private void rotateDocumentImageView(final int rotationForDisplay) {
        if (rotationForDisplay == 0) {
            return;
        }
        int newWidth = mLayoutRoot.getWidth();
        int newHeight = mLayoutRoot.getHeight();
        if (rotationForDisplay == 90 || rotationForDisplay == 270) {
            newWidth = mLayoutRoot.getHeight();
            newHeight = mLayoutRoot.getWidth();
        }

        final FrameLayout.LayoutParams layoutParams =
                (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
        layoutParams.width = newWidth;
        layoutParams.height = newHeight;
        mImageDocument.setLayoutParams(layoutParams);
        mImageDocument.setRotation(rotationForDisplay);
    }

    private void showDocument() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        LOG.debug("Rendering the document");
        final Size previewSize = new Size(mImageDocument.getWidth(), mImageDocument.getHeight());
        mDocumentRenderer.toBitmap(activity, previewSize, new DocumentRenderer.Callback() {
            @Override
            public void onBitmapReady(@Nullable final Bitmap bitmap, final int rotationForDisplay) {
                LOG.debug("Document rendered");
                if (mStopped) {
                    return;
                }
                rotateDocumentImageView(rotationForDisplay);
                mImageDocument.setImageBitmap(bitmap);
            }
        });
    }

    private void showPdfInfoForPdfDocument() {
        final GiniVisionDocument documentToRender = getFirstDocument();
        if (documentToRender instanceof PdfDocument) {
            final Activity activity = mFragment.getActivity();
            if (activity == null) {
                return;
            }
            mPdfOverlayLayout.setVisibility(View.VISIBLE);
            mAnalysisOverlay.setBackgroundColor(Color.TRANSPARENT);
            mAnalysisMessageTextView.setText("");

            final PdfDocument pdfDocument = (PdfDocument) documentToRender;
            final String filename = getPdfFilename(activity, pdfDocument);
            if (filename != null) {
                mPdfTitleTextView.setText(filename);
            }

            mPdfPageCountTextView.setVisibility(View.VISIBLE);
            mPdfPageCountTextView.setText("");

            mDocumentRenderer.getPageCount(activity, new AsyncCallback<Integer, Exception>() {
                @Override
                public void onSuccess(final Integer result) {
                    if (result > 0) {
                        mPdfPageCountTextView.setVisibility(View.VISIBLE);
                        final String pageCountString = activity.getResources().getQuantityString(
                                R.plurals.gv_analysis_pdf_pages, result, result);
                        mPdfPageCountTextView.setText(pageCountString);
                    } else {
                        mPdfPageCountTextView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onError(final Exception exception) {
                    mPdfPageCountTextView.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled() {
                    // Not used
                }
            });
        }
    }

    @Nullable
    private String getPdfFilename(final Activity activity, final PdfDocument pdfDocument) {
        final Uri uri = pdfDocument.getUri();
        try {
            return UriHelper.getFilenameFromUri(uri, activity);
        } catch (final IllegalStateException e) { // NOPMD
            // Ignore
        }
        return null;
    }
}
