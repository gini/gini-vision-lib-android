package net.gini.android.vision.review;

import static net.gini.android.vision.internal.network.NetworkRequestsManager.isCancellation;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;
import static net.gini.android.vision.internal.util.FileImportHelper.showAlertIfOpenWithDocumentAndAppIsDefault;
import static net.gini.android.vision.tracking.EventTrackingHelper.trackReviewScreenEvent;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ortiz.touch.TouchImageView;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.cache.PhotoMemoryCache;
import net.gini.android.vision.internal.camera.photo.ParcelableMemoryCache;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoEdit;
import net.gini.android.vision.internal.camera.photo.PhotoFactoryDocumentAsyncTask;
import net.gini.android.vision.internal.network.NetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestsManager;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.FileImportHelper;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.tracking.ReviewScreenEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Internal use only.
 *
 * @suppress
 */
class ReviewFragmentImpl implements ReviewFragmentInterface {

    private static final String PHOTO_KEY = "PHOTO_KEY";
    private static final String DOCUMENT_KEY = "DOCUMENT_KEY";
    private static final String PARCELABLE_MEMORY_CACHE_TAG = "REVIEW_FRAGMENT";
    private static final Logger LOG = LoggerFactory.getLogger(ReviewFragmentImpl.class);

    private static final ReviewFragmentListener NO_OP_LISTENER = new ReviewFragmentListener() {
        @Override
        public void onShouldAnalyzeDocument(@NonNull final Document document) {
        }

        @Override
        public void onProceedToAnalysisScreen(@NonNull final Document document) {
        }

        @Override
        public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {
        }

        @Override
        public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation,
                final int newRotation) {
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

        @Override
        public void onProceedToAnalysisScreen(@NonNull final Document document,
                final String errorMessage) {

        }
    };

    private FrameLayout mLayoutDocumentContainer;
    private TouchImageView mImageDocument;
    @VisibleForTesting
    ImageButton mButtonRotate;
    private ImageButton mButtonNext;
    private ProgressBar mActivityIndicator;

    private final FragmentImplCallback mFragment;
    @VisibleForTesting
    Photo mPhoto;
    private ImageDocument mDocument;
    private ReviewFragmentListener mListener = NO_OP_LISTENER;
    private boolean mDocumentWasUploaded;
    private boolean mDocumentWasModified;
    private int mCurrentRotation;
    private boolean mNextClicked;
    private boolean mStopped;
    private String mDocumentAnalysisErrorMessage;

    ReviewFragmentImpl(@NonNull final FragmentImplCallback fragment,
            @NonNull final Document document) {
        mFragment = fragment;
        if (!document.isReviewable()) {
            throw new IllegalArgumentException(
                    "Non reviewable documents must be passed directly to the Analysis Screen. You"
                            + " can use Document#isReviewable() to check whether you can use it "
                            + "with the Review Screen or have to pass it directly to the Analysis"
                            + " Screen.");
        }
        if (document.getType() != Document.Type.IMAGE) {
            throw new IllegalArgumentException("Only Documents with type IMAGE allowed");
        }
        mDocument = (ImageDocument) document;
        // Tag the documents to be able to clean up the parcelled data
        mDocument.setParcelableMemoryCacheTag(PARCELABLE_MEMORY_CACHE_TAG);
    }

    @VisibleForTesting
    TouchImageView getImageDocument() {
        return mImageDocument;
    }

    @Override
    public void setListener(@NonNull final ReviewFragmentListener listener) {
        mListener = listener;
    }

    @Override
    public void onDocumentAnalyzed() {
        LOG.info("Document was analyzed");
        mDocumentWasUploaded = true;
    }

    @Override
    public void onNoExtractionsFound() {
    }

    public void onCreate(@Nullable final Bundle savedInstanceState) {
        forcePortraitOrientationOnPhones(mFragment.getActivity());
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        }
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_review, container, false);
        bindViews(view);
        setInputHandlers();
        return view;
    }

    public void onStart() {
        mNextClicked = false;
        mStopped = false;
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        showAlertIfOpenWithDocumentAndAppIsDefault(activity, mDocument,
                new FileImportHelper.ShowAlertCallback() {
                    @Override
                    public void showAlertDialog(@NonNull final String message,
                            @NonNull final String positiveButtonTitle,
                            @NonNull final DialogInterface.OnClickListener
                                    positiveButtonClickListener,
                            @Nullable final String negativeButtonTitle,
                            @Nullable final DialogInterface.OnClickListener
                                    negativeButtonClickListener,
                            @Nullable final DialogInterface.OnCancelListener cancelListener) {
                        mFragment.showAlertDialog(message, positiveButtonTitle,
                                positiveButtonClickListener, negativeButtonTitle,
                                negativeButtonClickListener, cancelListener);
                    }
                })
                .thenRun(new Runnable() {
                    @Override
                    public void run() {
                        handleOnStart();
                    }
                });
    }

    private void handleOnStart() {
        if (mPhoto == null) {
            final Activity activity = mFragment.getActivity();
            if (activity == null) {
                return;
            }
            showActivityIndicatorAndDisableButtons();
            LOG.debug("Loading document data");
            mDocument.loadData(activity, new AsyncCallback<byte[], Exception>() {
                @Override
                public void onSuccess(final byte[] result) {
                    LOG.debug("Document data loaded");
                    if (mNextClicked || mStopped) {
                        return;
                    }
                    createPhoto();
                }

                @Override
                public void onError(final Exception exception) {
                    LOG.error("Failed to load document data", exception);
                    if (mNextClicked || mStopped) {
                        return;
                    }
                    hideActivityIndicatorAndEnableButtons();
                    mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW,
                            "An error occurred while loading the document."));
                }

                @Override
                public void onCancelled() {
                    // Not used
                }
            });
        } else {
            observeViewTree();
            LOG.info("Should analyze document");
            shouldAnalyzeDocument();
        }
    }

    private void shouldAnalyzeDocument() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        final GiniVisionDocument document = DocumentFactory.newImageDocumentFromPhotoAndDocument(
                mPhoto,
                mDocument);
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                networkRequestsManager.upload(activity, document)
                        .handle(new CompletableFuture.BiFun<NetworkRequestResult<
                                GiniVisionDocument>, Throwable, Void>() {
                            @Override
                            public Void apply(
                                    final NetworkRequestResult<GiniVisionDocument> requestResult,
                                    final Throwable throwable) {
                                if (throwable != null && !isCancellation(throwable)) {
                                    handleAnalysisError(throwable);
                                } else if (requestResult != null) {
                                    mDocumentWasUploaded = true;
                                }
                                return null;
                            }
                        });
            } else {
                mListener.onShouldAnalyzeDocument(document);
            }
        } else {
            mListener.onShouldAnalyzeDocument(document);
        }
    }

    private void handleAnalysisError(@NonNull final Throwable throwable) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().setReviewScreenAnalysisError(throwable);
        }
        mDocumentAnalysisErrorMessage = activity.getString(R.string.gv_document_analysis_error);
    }

    private void createPhoto() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (GiniVision.hasInstance()) {
            LOG.debug("Loading Photo from memory cache");
            final PhotoMemoryCache photoMemoryCache =
                    GiniVision.getInstance().internal().getPhotoMemoryCache();
            photoMemoryCache.get(activity, mDocument, new AsyncCallback<Photo, Exception>() {
                @Override
                public void onSuccess(final Photo result) {
                    LOG.debug("Photo loaded");
                    photoCreated(result);
                }

                @Override
                public void onError(final Exception exception) {
                    LOG.error("Failed to load a Photo for the ImageDocument");
                    photoCreationFailed();
                }

                @Override
                public void onCancelled() {
                    // Not used
                }
            });
        } else {
            LOG.debug("Instantiating a Photo from the Document");
            final PhotoFactoryDocumentAsyncTask asyncTask = new PhotoFactoryDocumentAsyncTask(
                    new AsyncCallback<Photo, Exception>() {
                        @Override
                        public void onSuccess(final Photo result) {
                            LOG.debug("Photo instantiated");
                            photoCreated(result);
                        }

                        @Override
                        public void onError(final Exception exception) {
                            LOG.error("Failed to instantiate a Photo from the ImageDocument");
                            photoCreationFailed();
                        }

                        @Override
                        public void onCancelled() {
                            // Not used
                        }
                    });
            asyncTask.execute(mDocument);
        }
    }

    private void photoCreated(final Photo result) {
        if (mNextClicked || mStopped) {
            return;
        }
        mPhoto = result;
        mPhoto.setParcelableMemoryCacheTag(PARCELABLE_MEMORY_CACHE_TAG);
        mCurrentRotation = mDocument.getRotationForDisplay();
        if (!mDocument.getSource().equals(Document.Source.newCameraSource())) {
            LOG.debug("Compressing Photo");
            applyCompressionToPhoto(new PhotoEdit.PhotoEditCallback() {
                @Override
                public void onDone(@NonNull final Photo photo) {
                    LOG.debug("Photo compressed");
                    photoReady();
                }

                @Override
                public void onFailed() {
                    LOG.error("Failed to compress the Photo");
                    if (mNextClicked || mStopped) {
                        return;
                    }
                    mListener.onError(
                            new GiniVisionError(GiniVisionError.ErrorCode.REVIEW,
                                    "An error occurred while compressing the jpeg."));
                }
            });
        } else {
            photoReady();
        }
    }

    private void applyCompressionToPhoto(@NonNull final PhotoEdit.PhotoEditCallback callback) {
        if (mPhoto == null) {
            return;
        }
        LOG.debug("Compressing the Photo");
        mPhoto.edit()
                .compressByDefault()
                .applyAsync(callback);
    }

    private void photoCreationFailed() {
        if (mNextClicked || mStopped) {
            return;
        }
        mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW,
                "An error occurred while instantiating a Photo from the ImageDocument."));
    }

    private void photoReady() {
        if (mNextClicked || mStopped) {
            return;
        }
        hideActivityIndicatorAndEnableButtons();
        observeViewTree();
        LOG.info("Should analyze document");
        shouldAnalyzeDocument();
    }

    private void showActivityIndicatorAndDisableButtons() {
        if (mActivityIndicator == null) {
            return;
        }
        mActivityIndicator.setVisibility(View.VISIBLE);
        disableNextButton();
        disableRotateButton();
    }

    private void hideActivityIndicatorAndEnableButtons() {
        if (mActivityIndicator == null) {
            return;
        }
        mActivityIndicator.setVisibility(View.GONE);
        enableNextButton();
        enableRotateButton();
    }

    private void disableNextButton() {
        if (mButtonNext == null) {
            return;
        }
        mButtonNext.setEnabled(false);
        mButtonNext.setAlpha(0.5f);
    }

    private void enableNextButton() {
        if (mButtonNext == null) {
            return;
        }
        mButtonNext.setEnabled(true);
        mButtonNext.setAlpha(1f);
    }

    private void disableRotateButton() {
        if (mButtonRotate == null) {
            return;
        }
        mButtonRotate.setEnabled(false);
        mButtonRotate.setAlpha(0.5f);
    }

    private void enableRotateButton() {
        if (mButtonRotate == null) {
            return;
        }
        mButtonRotate.setEnabled(true);
        mButtonRotate.setAlpha(1f);
    }

    private void showDocument() {
        if (mPhoto == null) {
            return;
        }
        mImageDocument.setImageBitmap(mPhoto.getBitmapPreview());
    }

    void onStop() {
        mStopped = true;
    }

    void onSaveInstanceState(final Bundle outState) {
        // Remove previously saved data from the memory cache to keep only the data saved in the
        // current invocation
        clearParcelableMemoryCache();
        outState.putParcelable(PHOTO_KEY, mPhoto);
        outState.putParcelable(DOCUMENT_KEY, mDocument);
    }

    private void clearParcelableMemoryCache() {
        ParcelableMemoryCache.getInstance().removeEntriesWithTag(PARCELABLE_MEMORY_CACHE_TAG);
    }

    public void onDestroy() {
        if (!mNextClicked) {
            deleteUploadedDocument();
        }
        final Activity activity = mFragment.getActivity();
        if (activity != null && activity.isFinishing()) {
            // Remove data from the memory cache. The data had been added in onSaveInstanceState()
            // and also when the document in the arguments was automatically parcelled when the
            // activity was stopped
            clearParcelableMemoryCache();
        }
        mPhoto = null; // NOPMD
        mDocument = null; // NOPMD
    }

    private void bindViews(@NonNull final View view) {
        mLayoutDocumentContainer = view.findViewById(R.id.gv_layout_document_container);
        mImageDocument = view.findViewById(R.id.gv_image_document);
        mButtonRotate = view.findViewById(R.id.gv_button_rotate);
        mButtonNext = view.findViewById(R.id.gv_button_next);
        mActivityIndicator = view.findViewById(R.id.gv_activity_indicator);
    }

    private void restoreSavedState(@Nullable final Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        LOG.debug("Restoring saved state");
        mPhoto = savedInstanceState.getParcelable(PHOTO_KEY);
        mDocument = savedInstanceState.getParcelable(DOCUMENT_KEY);
        if (mDocument == null) {
            throw new IllegalStateException(
                    "Missing required instances for restoring saved instance state.");
        }
        mCurrentRotation = mDocument.getRotationForDisplay();
    }

    private void setInputHandlers() {
        mButtonRotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onRotateClicked();
            }
        });
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onNextClicked();
            }
        });
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
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
        view.requestLayout();
    }

    private void onViewLayoutFinished() {
        LOG.debug("View layout finished");
        rotateDocumentForDisplay();
        showDocument();
    }

    private void rotateDocumentForDisplay() {
        rotateImageView(mDocument.getRotationForDisplay(), false);
    }

    private void onRotateClicked() {
        final int oldRotation = mCurrentRotation;
        mCurrentRotation += 90;
        rotateImageView(mCurrentRotation, true);
        if (GiniVision.hasInstance()
                && GiniVision.getInstance().internal().getNetworkRequestsManager() != null) {
            LOG.debug("Only the preview was rotated");
            mDocument.setRotationForDisplay(mCurrentRotation);
            mDocument.updateRotationDeltaBy(90);
            mPhoto.setRotationForDisplay(mCurrentRotation);
            mPhoto.updateRotationDeltaBy(90);
            return;
        }
        mDocumentWasModified = true;
        applyRotationToPhoto(new PhotoEdit.PhotoEditCallback() {
            @Override
            public void onDone(@NonNull final Photo photo) {
                if (mStopped) {
                    return;
                }
                mDocument.setRotationForDisplay(mCurrentRotation);
                mDocument.updateRotationDeltaBy(90);
                final GiniVisionDocument document =
                        DocumentFactory.newImageDocumentFromPhotoAndDocument(
                                photo, mDocument);
                mListener.onDocumentWasRotated(
                        document,
                        oldRotation, mCurrentRotation);
            }

            @Override
            public void onFailed() {
                if (mStopped) {
                    return;
                }
                LOG.error("Failed to rotate the jpeg");
                mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW,
                        "An error occurred while applying rotation to the jpeg."));
            }
        });
    }

    private void deleteUploadedDocument() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                networkRequestsManager.cancel(mDocument);
                networkRequestsManager.delete(mDocument);
            }
        }
    }

    @VisibleForTesting
    void onNextClicked() {
        trackReviewScreenEvent(ReviewScreenEvent.NEXT);
        mNextClicked = true;
        if (!mDocumentWasModified) {
            LOG.debug("Document wasn't modified");
            if (!mDocumentWasUploaded || !TextUtils.isEmpty(mDocumentAnalysisErrorMessage)) {
                LOG.debug("Document wasn't analyzed");
                proceedToAnalysisScreen();
            } else {
                LOG.debug("Document was analyzed");
                LOG.info("Document reviewed and analyzed");
                // Photo was not modified and has been analyzed, client should show extraction
                // results
                documentReviewedAndUploaded();
            }
        } else {
            LOG.debug("Document was modified");
            applyRotationToPhoto(new PhotoEdit.PhotoEditCallback() {
                @Override
                public void onDone(@NonNull final Photo photo) {
                    if (mStopped) {
                        return;
                    }
                    proceedToAnalysisScreen();
                }

                @Override
                public void onFailed() {
                    if (mStopped) {
                        return;
                    }
                    LOG.error("Failed to rotate the jpeg");
                    mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW,
                            "An error occurred while applying rotation to the jpeg."));
                }
            });
        }
    }

    private void documentReviewedAndUploaded() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        final GiniVisionDocument document = DocumentFactory.newImageDocumentFromPhotoAndDocument(
                mPhoto,
                mDocument);
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager =
                    GiniVision.getInstance().internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                mListener.onProceedToAnalysisScreen(document, mDocumentAnalysisErrorMessage);
            } else {
                mListener.onDocumentReviewedAndAnalyzed(document);
            }
        } else {
            mListener.onDocumentReviewedAndAnalyzed(document);
        }
    }

    private void proceedToAnalysisScreen() {
        LOG.info("Proceed to Analysis Screen");
        final GiniVisionDocument document = DocumentFactory.newImageDocumentFromPhotoAndDocument(
                mPhoto,
                mDocument);
        if (GiniVision.hasInstance()) {
            mListener.onProceedToAnalysisScreen(document, mDocumentAnalysisErrorMessage);
        } else {
            mListener.onProceedToAnalysisScreen(document);
        }
    }

    private void applyRotationToPhoto(@NonNull final PhotoEdit.PhotoEditCallback callback) {
        if (mPhoto == null) {
            return;
        }
        LOG.debug("Rotating the Photo {} degrees", mCurrentRotation);
        mPhoto.edit()
                .rotateTo(mCurrentRotation)
                .applyAsync(callback);
    }

    private void rotateImageView(final int degrees, final boolean animated) {
        LOG.info("Rotate ImageView {} degrees animated {}", degrees, animated);
        if (degrees == 0) {
            return;
        }

        mImageDocument.resetZoom();

        final ValueAnimator widthAnimation;
        final ValueAnimator heightAnimation;
        if (degrees % 360 == 90 || degrees % 360 == 270) {
            LOG.debug("ImageView width needs to fit container height");
            LOG.debug("ImageView height needs fit container width");
            widthAnimation = ValueAnimator.ofInt(mImageDocument.getWidth(),
                    mLayoutDocumentContainer.getHeight());
            heightAnimation = ValueAnimator.ofInt(mImageDocument.getHeight(),
                    mLayoutDocumentContainer.getWidth());
        } else {
            LOG.debug("ImageView width needs to fit container width");
            LOG.debug("ImageView height needs to fit container height");
            widthAnimation = ValueAnimator.ofInt(mImageDocument.getWidth(),
                    mLayoutDocumentContainer.getWidth());
            heightAnimation = ValueAnimator.ofInt(mImageDocument.getHeight(),
                    mLayoutDocumentContainer.getHeight());
        }

        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final int width = (int) valueAnimator.getAnimatedValue();
                final FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
                layoutParams.width = width;
                mImageDocument.requestLayout();
            }
        });
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final int height = (int) valueAnimator.getAnimatedValue();
                final FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
                layoutParams.height = height;
                mImageDocument.requestLayout();
            }
        });

        final ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(mImageDocument, "rotation",
                degrees);

        if (!animated) {
            widthAnimation.setDuration(0);
            heightAnimation.setDuration(0);
            rotateAnimation.setDuration(0);
        }

        widthAnimation.start();
        heightAnimation.start();
        rotateAnimation.start();
    }
}
