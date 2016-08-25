package net.gini.android.vision.review;

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

import com.ortiz.touch.TouchImageView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoEdit;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ReviewFragmentImpl implements ReviewFragmentInterface {

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
        public void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation) {
        }

        @Override
        public void onError(@NonNull GiniVisionError error) {
        }
    };

    private FrameLayout mLayoutDocumentContainer;
    private TouchImageView mImageDocument;
    private ImageButton mButtonRotate;
    private ImageButton mButtonNext;

    private final FragmentImplCallback mFragment;
    private Photo mPhoto;
    private ReviewFragmentListener mListener = NO_OP_LISTENER;
    private boolean mDocumentWasAnalyzed = false;
    private boolean mDocumentWasModified = false;
    private int mCurrentRotation = 0;
    private boolean mNextClicked = false;
    private boolean mStopped = false;

    public ReviewFragmentImpl(@NonNull FragmentImplCallback fragment, @NonNull Document document) {
        mFragment = fragment;
        mPhoto = Photo.fromDocument(document);
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
        applyCompressionToJpeg(new PhotoEdit.PhotoEditCallback() {
            @Override
            public void onDone(@NonNull Photo photo) {
                if (mNextClicked || mDocumentWasModified || mStopped) {
                    return;
                }
                LOG.info("Should analyze document");
                mListener.onShouldAnalyzeDocument(Document.fromPhoto(mPhoto));
            }

            @Override
            public void onFailed() {
                if (mNextClicked || mStopped) {
                    return;
                }
                LOG.error("Failed to compress the jpeg");
                mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW, "An error occurred while compressing the jpeg."));
            }
        });
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_review, container, false);
        bindViews(view);
        setInputHandlers();
        observeViewTree(view);
        return view;
    }

    public void onStart() {
        showDocument();
        mNextClicked = false;
        mStopped = false;
    }

    private void showDocument() {
        mImageDocument.setImageBitmap(mPhoto.getBitmapPreview());
    }

    public void onStop() {
        mStopped = true;
    }

    public void onDestroy() {
        mPhoto = null;
    }

    private void bindViews(@NonNull View view) {
        mLayoutDocumentContainer = (FrameLayout) view.findViewById(R.id.gv_layout_document_container);
        mImageDocument = (TouchImageView) view.findViewById(R.id.gv_image_document);
        mButtonRotate = (ImageButton) view.findViewById(R.id.gv_button_rotate);
        mButtonNext = (ImageButton) view.findViewById(R.id.gv_button_next);
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

    private void observeViewTree(@NonNull final View view) {
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onViewLayoutFinished();
                view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void onViewLayoutFinished() {
        rotateDocumentForDisplay();
    }

    private void rotateDocumentForDisplay() {
        mCurrentRotation = mPhoto.getRotationForDisplay();
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
                mListener.onDocumentWasRotated(Document.fromPhoto(photo), oldRotation, mCurrentRotation);
            }

            @Override
            public void onFailed() {
                if (mStopped) {
                    return;
                }
                LOG.error("Failed to rotate the jpeg");
                mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW, "An error occurred while applying rotation to the jpeg."));
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
                // Photo was not modified and has been analyzed, client should show extraction results
                mListener.onDocumentReviewedAndAnalyzed(Document.fromPhoto(mPhoto));
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
                    mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.REVIEW, "An error occurred while applying rotation to the jpeg."));
                }
            });
        }
    }

    private void proceedToAnalysisScreen() {
        LOG.info("Proceed to Analysis Screen");
        mListener.onProceedToAnalysisScreen(Document.fromPhoto(mPhoto));
    }

    private void applyRotationToJpeg(@NonNull PhotoEdit.PhotoEditCallback callback) {
        LOG.info("Rotating the jpeg {} degrees", mCurrentRotation);
        mPhoto.edit()
                .rotate(mCurrentRotation)
                .applyAsync(callback);
    }

    private void applyCompressionToJpeg(@NonNull PhotoEdit.PhotoEditCallback callback) {
        LOG.info("Compressing the jpeg to quality {}", JPEG_COMPRESSION_QUALITY_FOR_UPLOAD);
        mPhoto.edit()
                .compress(JPEG_COMPRESSION_QUALITY_FOR_UPLOAD)
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
            widthAnimation = ValueAnimator.ofInt(mImageDocument.getWidth(), mLayoutDocumentContainer.getHeight());
            heightAnimation = ValueAnimator.ofInt(mImageDocument.getHeight(), mLayoutDocumentContainer.getWidth());
        } else {
            LOG.debug("ImageView width needs to fit container width");
            LOG.debug("ImageView height needs to fit container height");
            widthAnimation = ValueAnimator.ofInt(mImageDocument.getWidth(), mLayoutDocumentContainer.getWidth());
            heightAnimation = ValueAnimator.ofInt(mImageDocument.getHeight(), mLayoutDocumentContainer.getHeight());
        }

        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int width = (int) valueAnimator.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
                layoutParams.width = width;
                mImageDocument.requestLayout();
            }
        });
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int height = (int) valueAnimator.getAnimatedValue();
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mImageDocument.getLayoutParams();
                layoutParams.height = height;
                mImageDocument.requestLayout();
            }
        });

        ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(mImageDocument, "rotation", degrees);

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
