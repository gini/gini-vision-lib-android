package net.gini.android.vision.review;

import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ortiz.touch.TouchImageView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoEdit;
import net.gini.android.vision.internal.camera.photo.PhotoFactoryDocumentAsyncTask;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.network.AnalysisResult;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetworkCallback;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

class ReviewFragmentImpl implements ReviewFragmentInterface {

    private static final String PHOTO_KEY = "PHOTO_KEY";
    private static final String DOCUMENT_KEY = "DOCUMENT_KEY";
    private static final Logger LOG = LoggerFactory.getLogger(ReviewFragmentImpl.class);

    private static final int JPEG_COMPRESSION_QUALITY_FOR_UPLOAD = 50;

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
    private ImageButton mButtonRotate;
    private ImageButton mButtonNext;
    private ProgressBar mActivityIndicator;

    private final FragmentImplCallback mFragment;
    private Photo mPhoto;
    private ImageDocument mDocument;
    private ReviewFragmentListener mListener = NO_OP_LISTENER;
    private boolean mDocumentWasAnalyzed;
    private boolean mDocumentWasModified;
    private int mCurrentRotation;
    private boolean mNextClicked;
    private boolean mStopped;
    private AnalysisResult mAnalysisResult;
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
    }

    @VisibleForTesting
    TouchImageView getImageDocument() {
        return mImageDocument;
    }

    public void setListener(@Nullable final ReviewFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    @Override
    public void onDocumentAnalyzed() {
        LOG.info("Document was analyzed");
        mDocumentWasAnalyzed = true;
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
        if (mPhoto == null) {
            final Activity activity = mFragment.getActivity();
            if (activity == null) {
                return;
            }
            showActivityIndicatorAndDisableButtons();
            LOG.debug("Loading document data");
            mDocument.loadData(activity, new AsyncCallback<byte[]>() {
                @Override
                public void onSuccess(final byte[] result) {
                    LOG.debug("Document data loaded");
                    if (mNextClicked || mStopped) {
                        return;
                    }
                    createAndCompressPhoto();
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
        if (GiniVision.hasInstance()) {
            final GiniVisionNetworkService networkService = GiniVision.getInstance()
                    .internal().getGiniVisionNetworkService();
            final Document document = DocumentFactory.newDocumentFromPhotoAndDocument(mPhoto,
                    mDocument);
            networkService.analyze(document,
                    new GiniVisionNetworkCallback<AnalysisResult, Error>() {
                        @Override
                        public void failure(final Error error) {
                            mDocumentAnalysisErrorMessage = error.getMessage();
                        }

                        @Override
                        public void success(final AnalysisResult result) {
                            mDocumentWasAnalyzed = true;
                            mAnalysisResult = result;
                        }

                        @Override
                        public void cancelled() {

                        }
                    });
        } else {
            mListener.onShouldAnalyzeDocument(
                    DocumentFactory.newDocumentFromPhotoAndDocument(mPhoto, mDocument));
        }
    }

    private void createAndCompressPhoto() {
        LOG.debug("Instantiating a Photo from the Document");
        final PhotoFactoryDocumentAsyncTask asyncTask = new PhotoFactoryDocumentAsyncTask(
                new AsyncCallback<Photo>() {
                    @Override
                    public void onSuccess(final Photo result) {
                        LOG.debug("Photo instantiated");
                        if (mNextClicked || mStopped) {
                            return;
                        }
                        mPhoto = result;
                        mCurrentRotation = mPhoto.getRotationForDisplay();
                        applyCompressionToPhoto(new PhotoEdit.PhotoEditCallback() {
                            @Override
                            public void onDone(@NonNull final Photo photo) {
                                LOG.debug("Photo compressed");
                                if (mNextClicked || mStopped) {
                                    return;
                                }
                                hideActivityIndicatorAndEnableButtons();
                                observeViewTree();
                                LOG.info("Should analyze document");
                                shouldAnalyzeDocument();
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
                    }

                    @Override
                    public void onError(final Exception exception) {
                        LOG.error("Failed to instantiate a Photo from the ImageDocument");
                        if (mNextClicked || mStopped) {
                            return;
                        }
                        mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW,
                                "An error occurred while instantiating a Photo from the ImageDocument."));
                    }
                });
        asyncTask.execute(mDocument);
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
        outState.putParcelable(PHOTO_KEY, mPhoto);
        outState.putParcelable(DOCUMENT_KEY, mDocument);
    }

    public void onDestroy() {
        mPhoto = null; // NOPMD
        mDocument = null; // NOPMD
        cancelAnalysis();
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
        if (mPhoto != null) {
            mCurrentRotation = mPhoto.getRotationForDisplay();
        }
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
        if (mPhoto == null) {
            return;
        }
        rotateImageView(mPhoto.getRotationForDisplay(), false);
    }

    private void onRotateClicked() {
        final int oldRotation = mCurrentRotation;
        mCurrentRotation += 90;
        rotateImageView(mCurrentRotation, true);
        mDocumentWasModified = true;
        applyRotationToPhoto(new PhotoEdit.PhotoEditCallback() {
            @Override
            public void onDone(@NonNull final Photo photo) {
                if (mStopped) {
                    return;
                }
                cancelAnalysis();
                final GiniVisionDocument document = DocumentFactory.newDocumentFromPhotoAndDocument(
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

    private void cancelAnalysis() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (GiniVision.hasInstance()) {
            final GiniVisionNetworkService networkService = GiniVision.getInstance()
                    .internal().getGiniVisionNetworkService();
            networkService.cancel();
        }
    }

    private void onNextClicked() {
        mNextClicked = true;
        if (!mDocumentWasModified) {
            LOG.debug("Document wasn't modified");
            if (!mDocumentWasAnalyzed || !TextUtils.isEmpty(mDocumentAnalysisErrorMessage)) {
                LOG.debug("Document wasn't analyzed");
                proceedToAnalysisScreen();
            } else {
                LOG.debug("Document was analyzed");
                LOG.info("Document reviewed and analyzed");
                // Photo was not modified and has been analyzed, client should show extraction
                // results
                documentReviewedAndAnalyzed();
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

    private void documentReviewedAndAnalyzed() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        final GiniVisionDocument document = DocumentFactory.newDocumentFromPhotoAndDocument(mPhoto,
                mDocument);
        if (GiniVision.hasInstance()) {
            final Map<String, GiniVisionSpecificExtraction> extractions =
                    mAnalysisResult.getExtractions();
            if (extractions.isEmpty()) {
                mListener.onProceedToNoExtractionsScreen(document);
            } else {
                mListener.onExtractionsAvailable(extractions);
            }
        } else {
            mListener.onDocumentReviewedAndAnalyzed(document);
        }
    }

    private void proceedToAnalysisScreen() {
        LOG.info("Proceed to Analysis Screen");
        final GiniVisionDocument document = DocumentFactory.newDocumentFromPhotoAndDocument(mPhoto,
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

    private void applyCompressionToPhoto(@NonNull final PhotoEdit.PhotoEditCallback callback) {
        if (mPhoto == null) {
            return;
        }
        LOG.debug("Compressing the Photo to quality {}", JPEG_COMPRESSION_QUALITY_FOR_UPLOAD);
        mPhoto.edit()
                .compressBy(JPEG_COMPRESSION_QUALITY_FOR_UPLOAD)
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
