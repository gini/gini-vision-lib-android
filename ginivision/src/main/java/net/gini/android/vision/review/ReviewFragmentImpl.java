package net.gini.android.vision.review;

import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ortiz.touch.TouchImageView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoEdit;
import net.gini.android.vision.internal.camera.photo.PhotoFactoryDocumentAsyncTask;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReviewFragmentImpl implements ReviewFragmentInterface {

    private static final String PHOTO_KEY = "PHOTO_KEY";
    private static final String DOCUMENT_KEY = "DOCUMENT_KEY";
    private static final Logger LOG = LoggerFactory.getLogger(ReviewFragmentImpl.class);

    private static final int JPEG_COMPRESSION_QUALITY_FOR_UPLOAD = 50;

    private static final ReviewFragmentListener NO_OP_LISTENER = new ReviewFragmentListener() {
        @Override
        public void onShouldAnalyzeDocument(@NonNull Document document) {
        }

        @Override
        public void onProceedToAnalysisScreen(@NonNull Document document) {
        }

        @Override
        public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        }

        @Override
        public void onDocumentWasRotated(@NonNull Document document, int oldRotation,
                int newRotation) {
        }

        @Override
        public void onError(@NonNull GiniVisionError error) {
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
    private boolean mDocumentWasAnalyzed = false;
    private boolean mDocumentWasModified = false;
    private int mCurrentRotation = 0;
    private boolean mNextClicked = false;
    private boolean mStopped = false;

    ReviewFragmentImpl(@NonNull FragmentImplCallback fragment, @NonNull Document document) {
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

    public void setListener(@Nullable ReviewFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public void onDocumentAnalyzed() {
        LOG.info("Document was analyzed");
        mDocumentWasAnalyzed = true;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        forcePortraitOrientationOnPhones(mFragment.getActivity());
        if (mDocument.getType() == Document.Type.PDF) {
            mListener.onProceedToAnalysisScreen(mDocument);
            return;
        }
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_review, container, false);
        bindViews(view);
        setInputHandlers();
        return view;
    }

    public void onStart() {
        mNextClicked = false;
        mStopped = false;
        if (mPhoto == null) {
            createAndCompressPhoto();
        } else {
            observeViewTree();
            LOG.info("Should analyze document");
            mListener.onShouldAnalyzeDocument(ImageDocument.fromPhoto(mPhoto));
        }
    }

    private void createAndCompressPhoto() {
        showActivityIndicator();
        PhotoFactoryDocumentAsyncTask asyncTask = new PhotoFactoryDocumentAsyncTask(
                new PhotoFactoryDocumentAsyncTask.Listener() {
                    @Override
                    public void onPhotoCreated(@NonNull final Photo photo) {
                        if (mNextClicked || mStopped) {
                            return;
                        }
                        mPhoto = photo;
                        mCurrentRotation = mPhoto.getRotationForDisplay();
                        applyCompressionToJpeg(new PhotoEdit.PhotoEditCallback() {
                            @Override
                            public void onDone(@NonNull Photo photo) {
                                if (mNextClicked || mStopped) {
                                    return;
                                }
                                hideActivityIndicator();
                                observeViewTree();
                                LOG.info("Should analyze document");
                                mListener.onShouldAnalyzeDocument(ImageDocument.fromPhoto(photo));
                            }

                            @Override
                            public void onFailed() {
                                if (mNextClicked || mStopped) {
                                    return;
                                }
                                LOG.error("Failed to compress the jpeg");
                                mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW,
                                        "An error occurred while compressing the jpeg."));
                            }
                        });
                    }
                });
        asyncTask.execute(mDocument);
    }

    private void showActivityIndicator() {
        if (mActivityIndicator == null) {
            return;
        }
        mActivityIndicator.setVisibility(View.VISIBLE);
        disableNextButton();
        disableRotateButton();
    }

    private void hideActivityIndicator() {
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
        mPhoto = null;
        mDocument = null;
    }

    private void bindViews(@NonNull View view) {
        mLayoutDocumentContainer = view.findViewById(
                R.id.gv_layout_document_container);
        mImageDocument = view.findViewById(R.id.gv_image_document);
        mButtonRotate = view.findViewById(R.id.gv_button_rotate);
        mButtonNext = view.findViewById(R.id.gv_button_next);
        mActivityIndicator = view.findViewById(R.id.gv_activity_indicator);
    }

    private void restoreSavedState(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
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
            public void onClick(View v) {
                onRotateClicked();
            }
        });
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNextClicked();
            }
        });
    }

    private void observeViewTree() {
        final View view = mFragment.getView();
        if (view == null) {
            return;
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        onViewLayoutFinished();
                        view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
    }

    private void onViewLayoutFinished() {
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
        applyRotationToJpeg(new PhotoEdit.PhotoEditCallback() {
            @Override
            public void onDone(@NonNull Photo photo) {
                if (mStopped) {
                    return;
                }
                mListener.onDocumentWasRotated(ImageDocument.fromPhoto(photo), oldRotation,
                        mCurrentRotation);
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

    private void onNextClicked() {
        mNextClicked = true;
        if (!mDocumentWasModified) {
            LOG.debug("Document wasn't modified");
            if (!mDocumentWasAnalyzed) {
                LOG.debug("Document wasn't analyzed");
                proceedToAnalysisScreen();
            } else {
                LOG.debug("Document was analyzed");
                LOG.info("Document reviewed and analyzed");
                // Photo was not modified and has been analyzed, client should show extraction
                // results
                mListener.onDocumentReviewedAndAnalyzed(ImageDocument.fromPhoto(mPhoto));
            }
        } else {
            LOG.debug("Document was modified");
            applyRotationToJpeg(new PhotoEdit.PhotoEditCallback() {
                @Override
                public void onDone(@NonNull Photo photo) {
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

    private void proceedToAnalysisScreen() {
        LOG.info("Proceed to Analysis Screen");
        mListener.onProceedToAnalysisScreen(ImageDocument.fromPhoto(mPhoto));
    }

    private void applyRotationToJpeg(@NonNull PhotoEdit.PhotoEditCallback callback) {
        if (mPhoto == null) {
            return;
        }
        LOG.info("Rotating the jpeg {} degrees", mCurrentRotation);
        mPhoto.edit()
                .rotateTo(mCurrentRotation)
                .applyAsync(callback);
    }

    private void applyCompressionToJpeg(@NonNull PhotoEdit.PhotoEditCallback callback) {
        if (mPhoto == null) {
            return;
        }
        LOG.info("Compressing the jpeg to quality {}", JPEG_COMPRESSION_QUALITY_FOR_UPLOAD);
        mPhoto.edit()
                .compressBy(JPEG_COMPRESSION_QUALITY_FOR_UPLOAD)
                .applyAsync(callback);
    }

    private void rotateImageView(int degrees, boolean animated) {
        LOG.info("Rotate ImageView {} degrees animated {}", degrees, animated);
        if (degrees == 0) {
            return;
        }

        mImageDocument.resetZoom();

        ValueAnimator widthAnimation;
        ValueAnimator heightAnimation;
        if (degrees % 360 == 90 ||
                degrees % 360 == 270) {
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
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int width = (int) valueAnimator.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
                layoutParams.width = width;
                mImageDocument.requestLayout();
            }
        });
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (int) valueAnimator.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams =
                        (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
                layoutParams.height = height;
                mImageDocument.requestLayout();
            }
        });

        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(mImageDocument, "rotation",
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
