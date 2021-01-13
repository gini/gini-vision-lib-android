package net.gini.android.vision.camera;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static net.gini.android.vision.camera.Util.cameraExceptionToGiniVisionError;
import static net.gini.android.vision.document.ImageDocument.ImportMethod;
import static net.gini.android.vision.internal.camera.view.FlashButtonHelper.getFlashButtonPosition;
import static net.gini.android.vision.internal.network.NetworkRequestsManager.isCancellation;
import static net.gini.android.vision.internal.qrcode.EPSPaymentParser.EXTRACTION_ENTITY_NAME;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;
import static net.gini.android.vision.internal.util.AndroidHelper.isMarshmallowOrLater;
import static net.gini.android.vision.internal.util.ContextHelper.isTablet;
import static net.gini.android.vision.internal.util.FeatureConfiguration.getDocumentImportEnabledFileTypes;
import static net.gini.android.vision.internal.util.FeatureConfiguration.isMultiPageEnabled;
import static net.gini.android.vision.internal.util.FeatureConfiguration.isQRCodeScanningEnabled;
import static net.gini.android.vision.tracking.EventTrackingHelper.trackCameraScreenEvent;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.ImportedFileValidationException;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.document.QRCodeDocument;
import net.gini.android.vision.internal.camera.api.CameraController;
import net.gini.android.vision.internal.camera.api.CameraException;
import net.gini.android.vision.internal.camera.api.CameraInterface;
import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoEdit;
import net.gini.android.vision.internal.camera.view.CameraPreviewSurface;
import net.gini.android.vision.internal.camera.view.FlashButtonHelper.FlashButtonPosition;
import net.gini.android.vision.internal.fileimport.FileChooserActivity;
import net.gini.android.vision.internal.network.AnalysisNetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestsManager;
import net.gini.android.vision.internal.qrcode.PaymentQRCodeData;
import net.gini.android.vision.internal.qrcode.PaymentQRCodeReader;
import net.gini.android.vision.internal.qrcode.QRCodeDetectorTask;
import net.gini.android.vision.internal.qrcode.QRCodeDetectorTaskGoogleVision;
import net.gini.android.vision.internal.storage.ImageDiskStore;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.ui.ViewStubSafeInflater;
import net.gini.android.vision.internal.util.ApplicationHelper;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.internal.util.Size;
import net.gini.android.vision.network.model.GiniVisionExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.tracking.CameraScreenEvent;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListener;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import jersey.repackaged.jsr166e.CompletableFuture;

class CameraFragmentImpl implements CameraFragmentInterface, PaymentQRCodeReader.Listener {

    private static final String GV_SHARED_PREFS = "GV_SHARED_PREFS";
    @VisibleForTesting
    static final int DEFAULT_ANIMATION_DURATION = 200;
    private static final long HIDE_QRCODE_DETECTED_POPUP_DELAY_MS = 10000;
    private static final long DIFFERENT_QRCODE_DETECTED_POPUP_DELAY_MS = 200;
    private static final Logger LOG = LoggerFactory.getLogger(CameraFragmentImpl.class);

    private static final CameraFragmentListener NO_OP_LISTENER = new CameraFragmentListener() {
        @Override
        public void onDocumentAvailable(@NonNull final Document document) {
        }

        @Override
        public void onProceedToMultiPageReviewScreen(
                @NonNull final GiniVisionMultiPageDocument multiPageDocument) {
        }

        @Override
        public void onQRCodeAvailable(@NonNull final QRCodeDocument qrCodeDocument) {

        }

        @Override
        public void onCheckImportedDocument(@NonNull final Document document,
                @NonNull final DocumentCheckResultCallback callback) {
            callback.documentAccepted();
        }

        @Override
        public void onError(@NonNull final GiniVisionError error) {
        }

        @Override
        public void onExtractionsAvailable(
                @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {

        }
    };

    private static final int REQ_CODE_CHOOSE_FILE = 1;
    private static final String SHOW_HINT_POP_UP = "SHOW_HINT_POP_UP";
    private static final String IN_MULTI_PAGE_STATE_KEY = "IN_MULTI_PAGE_STATE_KEY";
    private static final String IS_FLASH_ENABLED_KEY = "IS_FLASH_ENABLED_KEY";

    private final FragmentImplCallback mFragment;
    private final GiniVisionFeatureConfiguration mGiniVisionFeatureConfiguration;
    private HideQRCodeDetectedRunnable mHideQRCodeDetectedPopupRunnable;

    private View mImageCorners;
    private ImageStack mImageStack;
    private boolean mInterfaceHidden;
    private boolean mInMultiPageState;
    private boolean mIsFlashEnabled = true;
    private CameraFragmentListener mListener = NO_OP_LISTENER;
    private final UIExecutor mUIExecutor = new UIExecutor();
    private CameraInterface mCameraController;
    private ImageMultiPageDocument mMultiPageDocument;
    private PaymentQRCodeReader mPaymentQRCodeReader;

    private RelativeLayout mLayoutRoot;
    private CameraPreviewSurface mCameraPreview;
    private ImageView mCameraFocusIndicator;
    @VisibleForTesting
    ImageButton mButtonCameraTrigger;
    private ImageButton mButtonCameraFlash;
    private LinearLayout mLayoutNoPermission;
    private ImageButton mButtonImportDocument;
    private View mQRCodeDetectedPopupContainer;
    private PaymentQRCodeData mPaymentQRCodeData;
    private View mUploadHintCloseButton;
    private View mUploadHintContainer;
    private View mUploadHintContainerArrow;
    private View mCameraPreviewShade;
    private View mActivityIndicatorBackground;
    private ProgressBar mActivityIndicator;
    private ViewPropertyAnimatorCompat mUploadHintContainerArrowAnimation;
    private ViewPropertyAnimatorCompat mCameraPreviewShadeAnimation;
    private ViewPropertyAnimatorCompat mUploadHintContainerAnimation;
    private ViewPropertyAnimatorCompat mQRCodeDetectedPopupAnimation;

    private ViewStubSafeInflater mViewStubInflater;

    private CompletableFuture<SurfaceHolder> mSurfaceCreatedFuture = new CompletableFuture<>();
    private boolean mIsTakingPicture;

    private boolean mImportDocumentButtonEnabled;
    private ImportImageDocumentUrisAsyncTask mImportUrisAsyncTask;
    private boolean mProceededToMultiPageReview;
    private boolean mQRCodeAnalysisCompleted;
    private QRCodeDocument mQRCodeDocument;
    private LinearLayout mImportButtonContainer;
    private boolean mInstanceStateSaved;

    CameraFragmentImpl(@NonNull final FragmentImplCallback fragment) {
        this(fragment, GiniVisionFeatureConfiguration.buildNewConfiguration().build());
    }

    CameraFragmentImpl(@NonNull final FragmentImplCallback fragment,
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        mFragment = fragment;
        mGiniVisionFeatureConfiguration = giniVisionFeatureConfiguration;
    }

    @Override
    public void onPaymentQRCodeDataAvailable(@NonNull final PaymentQRCodeData paymentQRCodeData) {
        if (mUploadHintContainer.getVisibility() == View.VISIBLE
                || mInterfaceHidden
                || mActivityIndicator.getVisibility() == View.VISIBLE) {
            hideQRCodeDetectedPopup(null);
            mPaymentQRCodeData = null; // NOPMD
            return;
        }

        final View view = mFragment.getView();
        if (view == null) {
            return;
        }

        if (mPaymentQRCodeData == null
                || mQRCodeDetectedPopupContainer.getVisibility() == View.GONE) {
            showQRCodeDetectedPopup(0);
            view.removeCallbacks(mHideQRCodeDetectedPopupRunnable);
            view.postDelayed(mHideQRCodeDetectedPopupRunnable,
                    getHideQRCodeDetectedPopupDelayMs());
        } else {
            if (mPaymentQRCodeData.equals(paymentQRCodeData)) {
                view.removeCallbacks(mHideQRCodeDetectedPopupRunnable);
                view.postDelayed(mHideQRCodeDetectedPopupRunnable,
                        getHideQRCodeDetectedPopupDelayMs());
            } else {
                view.removeCallbacks(mHideQRCodeDetectedPopupRunnable);
                hideQRCodeDetectedPopup(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        showQRCodeDetectedPopup(
                                getDifferentQRCodeDetectedPopupDelayMs());
                    }
                });
            }
        }
        mPaymentQRCodeData = paymentQRCodeData;
    }

    @VisibleForTesting
    long getHideQRCodeDetectedPopupDelayMs() {
        return HIDE_QRCODE_DETECTED_POPUP_DELAY_MS;
    }

    @VisibleForTesting
    long getDifferentQRCodeDetectedPopupDelayMs() {
        return DIFFERENT_QRCODE_DETECTED_POPUP_DELAY_MS;
    }

    private class HideQRCodeDetectedRunnable implements Runnable {

        @Override
        public void run() {
            hideQRCodeDetectedPopup(null);
            mPaymentQRCodeData = null; // NOPMD
        }
    }

    @VisibleForTesting
    void showQRCodeDetectedPopup(final long startDelay) {
        if (mQRCodeDetectedPopupContainer.getAlpha() != 0) {
            return;
        }
        clearQRCodeDetectedPopUpAnimation();
        mQRCodeDetectedPopupContainer.setVisibility(View.VISIBLE);
        mQRCodeDetectedPopupAnimation = ViewCompat.animate(mQRCodeDetectedPopupContainer)
                .alpha(1.0f)
                .setStartDelay(startDelay)
                .setDuration(DEFAULT_ANIMATION_DURATION);
        mQRCodeDetectedPopupAnimation.start();
    }

    private void hideQRCodeDetectedPopup(
            @Nullable final ViewPropertyAnimatorListener animatorListener) {
        if (mQRCodeDetectedPopupContainer.getAlpha() != 1) {
            if (animatorListener != null) {
                animatorListener.onAnimationEnd(mQRCodeDetectedPopupContainer);
            }
            return;
        }
        clearQRCodeDetectedPopUpAnimation();
        mQRCodeDetectedPopupAnimation = ViewCompat.animate(mQRCodeDetectedPopupContainer)
                .alpha(0.0f)
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        mQRCodeDetectedPopupContainer.setVisibility(View.GONE);
                        if (animatorListener != null) {
                            animatorListener.onAnimationEnd(view);
                        }
                    }
                });
        mQRCodeDetectedPopupAnimation.start();
    }

    private void clearQRCodeDetectedPopUpAnimation() {
        if (mQRCodeDetectedPopupAnimation != null) {
            mQRCodeDetectedPopupAnimation.cancel();
            mQRCodeDetectedPopupContainer.clearAnimation();
            mQRCodeDetectedPopupAnimation.setListener(null);
        }
        final View view = mFragment.getView();
        if (view != null) {
            view.removeCallbacks(mHideQRCodeDetectedPopupRunnable);
        }
    }

    @Override
    public void setListener(@NonNull final CameraFragmentListener listener) {
        mListener = listener;
    }

    public void onCreate(final Bundle savedInstanceState) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        forcePortraitOrientationOnPhones(activity);
        initFlashState();
        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState);
        }
    }

    private void initFlashState() {
        if (GiniVision.hasInstance()) {
            mIsFlashEnabled = GiniVision.getInstance().isFlashOnByDefault();
        }
    }

    private void restoreSavedState(@NonNull final Bundle savedInstanceState) {
        mInMultiPageState = savedInstanceState.getBoolean(IN_MULTI_PAGE_STATE_KEY);
        mIsFlashEnabled = savedInstanceState.getBoolean(IS_FLASH_ENABLED_KEY);
    }

    View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_camera, container, false);
        bindViews(view);
        setInputHandlers();
        setSurfaceViewCallback();
        return view;
    }

    public void onStart() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        mInstanceStateSaved = false;
        mProceededToMultiPageReview = false;
        initViews();
        initCameraController(activity);
        if (isQRCodeScanningEnabled(mGiniVisionFeatureConfiguration)) {
            mHideQRCodeDetectedPopupRunnable = new HideQRCodeDetectedRunnable();
            initQRCodeReader(activity);
        }

        if (isCameraPermissionGranted()) {
            final CompletableFuture<Void> openCameraCompletable = openCamera();
            final CompletableFuture<SurfaceHolder> surfaceCreationCompletable =
                    handleSurfaceCreation();

            CompletableFuture.allOf(openCameraCompletable, surfaceCreationCompletable)
                    .handle(new CompletableFuture.BiFun<Void, Throwable, Object>() {
                        @Override
                        public Object apply(final Void aVoid, final Throwable throwable) {
                            if (throwable != null) {
                                // Exceptions were handled before
                                return null;
                            }
                            try {
                                final SurfaceHolder surfaceHolder =
                                        surfaceCreationCompletable.get();
                                if (surfaceHolder != null) {
                                    final Size previewSize =
                                            mCameraController.getPreviewSizeForDisplay();
                                    mCameraPreview.setPreviewSize(previewSize);
                                    startPreview(surfaceHolder);
                                    enableTapToFocus();
                                    showUploadHintPopUpOnFirstExecution();
                                    initFlashButton();
                                } else {
                                    handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW,
                                            "Cannot start preview: no SurfaceHolder received for SurfaceView",
                                            null);
                                }
                            } catch (final InterruptedException | ExecutionException e) {
                                handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW,
                                        "Cannot start preview", e);
                            }
                            return null;
                        }
                    });
        } else {
            showNoPermissionView();
        }
    }

    private boolean isCameraPermissionGranted() {
        final Activity activity = mFragment.getActivity();
        return activity != null && ContextCompat.checkSelfPermission(activity,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void initFlashButton() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (mCameraController.isFlashAvailable()) {
            if (GiniVision.hasInstance() && GiniVision.getInstance().isFlashButtonEnabled()) {
                mButtonCameraFlash.setVisibility(View.VISIBLE);
            }
            updateCameraFlashState();
        }

    }

    public void onResume() {
        initMultiPageDocument();
    }

    private void initMultiPageDocument() {
        if (GiniVision.hasInstance()) {
            final ImageMultiPageDocument multiPageDocument =
                    GiniVision.getInstance().internal()
                            .getImageMultiPageDocumentMemoryStore().getMultiPageDocument();
            if (multiPageDocument != null && multiPageDocument.getDocuments().size() > 0) {
                mMultiPageDocument = multiPageDocument;
                mInMultiPageState = true;
                updateImageStack();
            } else {
                mInMultiPageState = false;
                mMultiPageDocument = null;
                mImageStack.removeImages();
            }
        }
    }

    private void initQRCodeReader(final Activity activity) {
        if (mPaymentQRCodeReader != null) {
            return;
        }
        final QRCodeDetectorTaskGoogleVision qrCodeDetectorTask =
                new QRCodeDetectorTaskGoogleVision(activity);
        qrCodeDetectorTask.checkAvailability(new QRCodeDetectorTask.Callback() {
            @Override
            public void onResult(final boolean isAvailable) {
                if (isAvailable) {
                    mPaymentQRCodeReader = PaymentQRCodeReader.newInstance(qrCodeDetectorTask);
                    mPaymentQRCodeReader.setListener(CameraFragmentImpl.this);
                } else {
                    LOG.warn(
                            "QRCode detector dependencies are not yet available. QRCode detection is disabled.");
                }
            }

            @Override
            public void onInterrupted() {
                LOG.debug(
                        "Checking whether the QRCode detector task is operational was interrupted.");
            }
        });
    }

    @VisibleForTesting
    PaymentQRCodeReader getPaymentQRCodeReader() {
        return mPaymentQRCodeReader;
    }


    private void showUploadHintPopUpOnFirstExecution() {
        if (shouldShowHintPopUp()) {
            showUploadHintPopUp();
        }
    }

    @VisibleForTesting
    void showUploadHintPopUp() {
        disableCameraTriggerButtonAnimated(0.3f);
        mUploadHintContainer.setVisibility(View.VISIBLE);
        mUploadHintContainerArrow.setVisibility(View.VISIBLE);
        mCameraPreviewShade.setVisibility(View.VISIBLE);
        mCameraPreviewShade.setClickable(true);
        clearUploadHintPopUpAnimations();
        mUploadHintContainerAnimation = ViewCompat.animate(
                mUploadHintContainer)
                .alpha(1)
                .setDuration(DEFAULT_ANIMATION_DURATION);
        mUploadHintContainerAnimation.start();
        mUploadHintContainerArrowAnimation = ViewCompat.animate(
                mUploadHintContainerArrow)
                .alpha(1)
                .setDuration(DEFAULT_ANIMATION_DURATION);
        mUploadHintContainerArrowAnimation.start();
        mCameraPreviewShadeAnimation = ViewCompat.animate(
                mCameraPreviewShade)
                .alpha(1)
                .setDuration(DEFAULT_ANIMATION_DURATION);
        mCameraPreviewShadeAnimation.start();
    }

    private void clearUploadHintPopUpAnimations() {
        if (mUploadHintContainerAnimation != null) {
            mUploadHintContainerAnimation.cancel();
            mUploadHintContainer.clearAnimation();
            mUploadHintContainerAnimation.setListener(null);
        }
        if (mUploadHintContainerArrowAnimation != null) {
            mUploadHintContainerArrowAnimation.cancel();
            mUploadHintContainerArrow.clearAnimation();
            mUploadHintContainerArrowAnimation.setListener(null);
        }
        if (mCameraPreviewShadeAnimation != null) {
            mCameraPreviewShadeAnimation.cancel();
            mCameraPreviewShade.clearAnimation();
            mCameraPreviewShadeAnimation.setListener(null);
        }
    }

    private boolean shouldShowHintPopUp() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return false;
        }
        if (!isDocumentImportEnabled(activity) || mInterfaceHidden) {
            return false;
        }
        final Context context = mFragment.getActivity();
        if (context != null) {
            final SharedPreferences gvSharedPrefs = context.getSharedPreferences(GV_SHARED_PREFS,
                    Context.MODE_PRIVATE);
            return gvSharedPrefs.getBoolean(SHOW_HINT_POP_UP, true);
        }
        return false;
    }

    private void startPreview(final SurfaceHolder holder) {
        mCameraController.startPreview(holder)
                .handle(new CompletableFuture.BiFun<Void, Throwable, Void>() {
                    @Override
                    public Void apply(final Void aVoid, final Throwable throwable) {
                        if (throwable != null) {
                            handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW,
                                    "Cannot start preview", throwable);
                        }
                        return null;
                    }
                });
    }

    private void enableTapToFocus() {
        mCameraController.enableTapToFocus(mCameraPreview,
                new CameraInterface.TapToFocusListener() {
                    @Override
                    public void onFocusing(final Point point) {
                        showFocusIndicator(point);
                    }

                    @Override
                    public void onFocused(final boolean success) {
                        hideFocusIndicator();
                    }
                });
    }

    private void showFocusIndicator(final Point point) {
        final int top = Math.round((mLayoutRoot.getHeight() - mCameraPreview.getHeight()) / 2.0f);
        final int left = Math.round((mLayoutRoot.getWidth() - mCameraPreview.getWidth()) / 2.0f);
        final RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) mCameraFocusIndicator.getLayoutParams();
        layoutParams.leftMargin = (int) Math.round(
                left + point.x - (mCameraFocusIndicator.getWidth() / 2.0));
        layoutParams.topMargin = (int) Math.round(
                top + point.y - (mCameraFocusIndicator.getHeight() / 2.0));
        mCameraFocusIndicator.setLayoutParams(layoutParams);
        mCameraFocusIndicator.animate().setDuration(DEFAULT_ANIMATION_DURATION).alpha(1.0f);
    }

    private void hideFocusIndicator() {
        mCameraFocusIndicator.animate().setDuration(DEFAULT_ANIMATION_DURATION).alpha(0.0f);
    }

    private CompletableFuture<Void> openCamera() {
        LOG.info("Opening camera");
        return mCameraController.open()
                .handle(new CompletableFuture.BiFun<Void, Throwable, Void>() {
                    @Override
                    public Void apply(final Void aVoid, final Throwable throwable) {
                        if (throwable != null) {
                            if (throwable instanceof CameraException) {
                                handleError(GiniVisionError.ErrorCode.CAMERA_OPEN_FAILED,
                                        "Failed to open camera", throwable);
                            } else if (throwable instanceof Exception) {
                                handleCameraException((Exception) throwable);
                            } else {
                                handleError(GiniVisionError.ErrorCode.CAMERA_OPEN_FAILED,
                                        "Failed to open camera", throwable);
                            }
                            throw new CameraException(throwable);
                        } else {
                            LOG.info("Camera opened");
                            hideNoPermissionView();
                        }
                        return null;
                    }
                });
    }

    private CompletableFuture<SurfaceHolder> handleSurfaceCreation() {
        return mSurfaceCreatedFuture.handle(
                new CompletableFuture.BiFun<SurfaceHolder, Throwable, SurfaceHolder>() {
                    @Override
                    public SurfaceHolder apply(final SurfaceHolder surfaceHolder,
                            final Throwable throwable) {
                        if (throwable != null) {
                            handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW,
                                    "Cannot start preview", throwable);
                            throw new CameraException(throwable);
                        }
                        return surfaceHolder;
                    }
                });
    }

    private void handleCameraException(@NonNull final Exception e) {
        LOG.error("Failed to open camera", e);
        final GiniVisionError error = cameraExceptionToGiniVisionError(e);
        if (error.getErrorCode() == GiniVisionError.ErrorCode.CAMERA_NO_ACCESS) {
            showNoPermissionView();
        } else {
            mListener.onError(cameraExceptionToGiniVisionError(e));
        }
    }

    void onSaveInstanceState(@NonNull final Bundle outState) {
        mInstanceStateSaved = true;
        outState.putBoolean(IN_MULTI_PAGE_STATE_KEY, mInMultiPageState);
        outState.putBoolean(IS_FLASH_ENABLED_KEY, mIsFlashEnabled);
    }

    void onStop() {
        closeCamera();
        clearUploadHintPopUpAnimations();
        clearQRCodeDetectedPopUpAnimation();
    }

    void onDestroy() {
        if (mImportUrisAsyncTask != null) {
            mImportUrisAsyncTask.cancel(true);
        }

        if (!mInstanceStateSaved) {
            if (!mProceededToMultiPageReview) {
                deleteUploadedMultiPageDocuments();
                clearMultiPageDocument();
            }
            if (!mQRCodeAnalysisCompleted) {
                deleteUploadedQRCodeDocument();
            }
        }
    }

    private void clearMultiPageDocument() {
        if (GiniVision.hasInstance()) {
            mMultiPageDocument = null; // NOPMD
            GiniVision.getInstance().internal()
                    .getImageMultiPageDocumentMemoryStore().clear();
        }
    }

    private void deleteUploadedMultiPageDocuments() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (mMultiPageDocument == null) {
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
                                for (final Object document : mMultiPageDocument.getDocuments()) {
                                    final GiniVisionDocument giniVisionDocument =
                                            (GiniVisionDocument) document;
                                    networkRequestsManager.cancel(giniVisionDocument);
                                    networkRequestsManager.delete(giniVisionDocument);
                                }
                                return null;
                            }
                        });
            }
        }
    }

    private void deleteUploadedQRCodeDocument() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (mQRCodeDocument == null) {
            return;
        }
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                networkRequestsManager.cancel(mQRCodeDocument);
                networkRequestsManager.delete(mQRCodeDocument);
            }
        }
    }

    private void closeCamera() {
        LOG.info("Closing camera");
        if (mPaymentQRCodeReader != null) {
            mPaymentQRCodeReader.release();
            mPaymentQRCodeReader = null; // NOPMD
        }
        mCameraController.disableTapToFocus(mCameraPreview);
        mCameraController.setPreviewCallback(null);
        mCameraController.stopPreview();
        mCameraController.close();
        LOG.info("Camera closed");
    }

    private void bindViews(final View view) {
        mLayoutRoot = view.findViewById(R.id.gv_root);
        mCameraPreview = view.findViewById(R.id.gv_camera_preview);
        mImageCorners = view.findViewById(R.id.gv_image_corners);
        mCameraFocusIndicator = view.findViewById(R.id.gv_camera_focus_indicator);
        mButtonCameraTrigger = view.findViewById(R.id.gv_button_camera_trigger);
        bindFlashButtonView(view);
        final ViewStub stubNoPermission = view.findViewById(R.id.gv_stub_camera_no_permission);
        mViewStubInflater = new ViewStubSafeInflater(stubNoPermission);
        mButtonImportDocument = view.findViewById(R.id.gv_button_import_document);
        mImportButtonContainer = view.findViewById(R.id.gv_document_import_button_container);
        mUploadHintContainer = view.findViewById(R.id.gv_document_import_hint_container);
        mUploadHintContainerArrow = view.findViewById(R.id.gv_document_import_hint_container_arrow);
        mUploadHintCloseButton = view.findViewById(R.id.gv_document_import_hint_close_button);
        mCameraPreviewShade = view.findViewById(R.id.gv_camera_preview_shade);
        mActivityIndicatorBackground =
                view.findViewById(R.id.gv_activity_indicator_background);
        mActivityIndicator = view.findViewById(R.id.gv_activity_indicator);
        mQRCodeDetectedPopupContainer = view.findViewById(

                R.id.gv_qrcode_detected_popup_container);
        mImageStack = view.findViewById(R.id.gv_image_stack);
    }

    private void bindFlashButtonView(final View view) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (isTablet(activity)) {
            mButtonCameraFlash = view.findViewById(R.id.gv_button_camera_flash);
            if (mButtonCameraFlash != null) {
                return;
            }
        }
        final FlashButtonPosition flashButtonPosition = getFlashButtonPosition(
                isDocumentImportEnabled(activity), isMultiPageEnabled());
        switch (flashButtonPosition) {
            case LEFT_OF_CAMERA_TRIGGER:
                mButtonCameraFlash = view.findViewById(R.id.gv_button_camera_flash_left_of_trigger);
                break;
            case BOTTOM_LEFT:
                mButtonCameraFlash = view.findViewById(R.id.gv_button_camera_flash_bottom_left);
                break;
            case BOTTOM_RIGHT:
                mButtonCameraFlash = view.findViewById(R.id.gv_button_camera_flash_bottom_right);
                break;
            default:
                throw new UnsupportedOperationException("Unknown flash button position: "
                        + flashButtonPosition);
        }
    }

    private void initViews() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (!mInterfaceHidden && isDocumentImportEnabled(activity)) {
            mImportDocumentButtonEnabled = true;
            mImportButtonContainer.setVisibility(View.VISIBLE);
            showImportDocumentButtonAnimated();
        }
    }

    private boolean isDocumentImportEnabled(@NonNull final Activity activity) {
        return getDocumentImportEnabledFileTypes(mGiniVisionFeatureConfiguration)
                != DocumentImportEnabledFileTypes.NONE
                && FileChooserActivity.canChooseFiles(activity);
    }

    private void setInputHandlers() {
        mButtonCameraTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onCameraTriggerClicked();
            }
        });
        mButtonCameraFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mIsFlashEnabled = !mCameraController.isFlashEnabled();
                updateCameraFlashState();
            }
        });
        mImportButtonContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                closeUploadHintPopUp();
                showFileChooser();
            }
        });
        mUploadHintCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                closeUploadHintPopUp();
            }
        });
        mQRCodeDetectedPopupContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                hideQRCodeDetectedPopup(null);
                handlePaymentQRCodeData();
            }
        });
        mImageStack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mProceededToMultiPageReview = true;
                mListener.onProceedToMultiPageReviewScreen(mMultiPageDocument);
            }
        });
    }

    @VisibleForTesting
    void onCameraTriggerClicked() {
        LOG.info("Taking picture");
        if (exceedsMultiPageLimit()) {
            showMultiPageLimitError();
            return;
        }
        if (!mCameraController.isPreviewRunning()) {
            LOG.info("Will not take picture: preview must be running");
            return;
        }
        if (mIsTakingPicture) {
            LOG.info("Already taking a picture");
            return;
        }
        mIsTakingPicture = true;
        mCameraController.takePicture()
                .handle(new CompletableFuture.BiFun<Photo, Throwable, Void>() {
                    @Override
                    public Void apply(final Photo photo, final Throwable throwable) {
                        mUIExecutor.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trackCameraScreenEvent(CameraScreenEvent.TAKE_PICTURE);
                                onPictureTaken(photo, throwable);
                            }
                        });
                        return null;
                    }
                });
    }

    private void handlePaymentQRCodeData() {
        if (mPaymentQRCodeData == null) {
            return;
        }
        switch (mPaymentQRCodeData.getFormat()) {
            case EPC069_12:
            case BEZAHL_CODE:
                mQRCodeDocument = QRCodeDocument.fromPaymentQRCodeData(
                        mPaymentQRCodeData);
                analyzeQRCode(mQRCodeDocument);
                break;
            case EPS_PAYMENT:
                handleEPSPaymentQRCode();
                break;
            default:
                LOG.error("Unknown payment QR Code format: {}", mPaymentQRCodeData);
                break;
        }
        mPaymentQRCodeData = null; // NOPMD
    }

    private void handleEPSPaymentQRCode() {
        final GiniVisionExtraction extraction = new GiniVisionExtraction(
                mPaymentQRCodeData.getUnparsedContent(), EXTRACTION_ENTITY_NAME,
                null);
        final GiniVisionSpecificExtraction specificExtraction = new GiniVisionSpecificExtraction(
                EXTRACTION_ENTITY_NAME,
                mPaymentQRCodeData.getUnparsedContent(),
                EXTRACTION_ENTITY_NAME,
                null,
                Collections.singletonList(extraction)
        );
        mListener.onExtractionsAvailable(
                Collections.singletonMap(EXTRACTION_ENTITY_NAME, specificExtraction));
    }

    private void updateCameraFlashState() {
        mCameraController.setFlashEnabled(mIsFlashEnabled);
        updateFlashButtonImage();
    }

    private void updateFlashButtonImage() {
        final int flashIconRes = mIsFlashEnabled ? R.drawable.gv_camera_flash_on
                : R.drawable.gv_camera_flash_off;
        mButtonCameraFlash.setImageResource(flashIconRes);
    }

    @VisibleForTesting
    void analyzeQRCode(final QRCodeDocument qrCodeDocument) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        mQRCodeAnalysisCompleted = false;
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager =
                    GiniVision.getInstance().internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                showActivityIndicatorAndDisableInteraction();
                networkRequestsManager
                        .upload(activity, qrCodeDocument)
                        .handle(new CompletableFuture.BiFun<NetworkRequestResult<
                                GiniVisionDocument>, Throwable,
                                NetworkRequestResult<GiniVisionDocument>>() {
                            @Override
                            public NetworkRequestResult<GiniVisionDocument> apply(
                                    final NetworkRequestResult<GiniVisionDocument> requestResult,
                                    final Throwable throwable) {
                                if (throwable != null) {
                                    hideActivityIndicatorAndEnableInteraction();
                                    if (!isCancellation(throwable)) {
                                        handleAnalysisError();
                                    }
                                }
                                return requestResult;
                            }
                        })
                        .thenCompose(
                                new CompletableFuture.Fun<NetworkRequestResult<GiniVisionDocument>,
                                        CompletableFuture<AnalysisNetworkRequestResult<
                                                GiniVisionMultiPageDocument>>>() {
                                    @Override
                                    public CompletableFuture<AnalysisNetworkRequestResult<
                                            GiniVisionMultiPageDocument>> apply(
                                            final NetworkRequestResult<GiniVisionDocument>
                                                    requestResult) {
                                        if (requestResult != null) {
                                            final GiniVisionMultiPageDocument multiPageDocument =
                                                    DocumentFactory.newMultiPageDocument(
                                                            qrCodeDocument);
                                            return networkRequestsManager.analyze(
                                                    multiPageDocument);
                                        }
                                        return CompletableFuture.completedFuture(null);
                                    }
                                })
                        .handle(new CompletableFuture.BiFun<AnalysisNetworkRequestResult<
                                GiniVisionMultiPageDocument>, Throwable, Void>() {
                            @Override
                            public Void apply(
                                    final AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>
                                            requestResult,
                                    final Throwable throwable) {
                                hideActivityIndicatorAndEnableInteraction();
                                if (throwable != null
                                        && !isCancellation(throwable)) {
                                    handleAnalysisError();
                                } else if (requestResult != null) {
                                    mQRCodeAnalysisCompleted = true;
                                    mListener.onExtractionsAvailable(
                                            requestResult.getAnalysisResult().getExtractions());
                                }
                                return null;
                            }
                        });
            } else {
                mListener.onQRCodeAvailable(qrCodeDocument);
            }
        } else {
            mListener.onQRCodeAvailable(qrCodeDocument);
        }
    }

    private void handleAnalysisError() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        showError(activity.getString(R.string.gv_document_analysis_error), 3000);
    }

    private void closeUploadHintPopUp() {
        hideUploadHintPopUp(new ViewPropertyAnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final View view) {
                final Context context = view.getContext();
                savePopUpShown(context);
            }
        });
    }

    private void hideUploadHintPopUp(@Nullable final ViewPropertyAnimatorListenerAdapter
            animatorListener) {
        if (!mInterfaceHidden) {
            enableCameraTriggerButtonAnimated();
        }
        clearUploadHintPopUpAnimations();
        mUploadHintContainerAnimation = ViewCompat.animate(mUploadHintContainer)
                .alpha(0)
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        mUploadHintContainerArrow.setVisibility(View.GONE);
                        mUploadHintContainer.setVisibility(View.GONE);
                        mCameraPreviewShade.setVisibility(View.GONE);
                        mCameraPreviewShade.setClickable(false);
                        if (animatorListener != null) {
                            animatorListener.onAnimationEnd(view);
                        }
                    }
                });
        mUploadHintContainerAnimation.start();
        mUploadHintContainerArrowAnimation = ViewCompat.animate(mUploadHintContainerArrow)
                .alpha(0)
                .setDuration(DEFAULT_ANIMATION_DURATION);
        mUploadHintContainerArrowAnimation.start();
        mCameraPreviewShadeAnimation = ViewCompat.animate(mCameraPreviewShade)
                .alpha(0)
                .setDuration(DEFAULT_ANIMATION_DURATION);
        mCameraPreviewShadeAnimation.start();
    }

    private void savePopUpShown(final Context context) {
        final SharedPreferences gvSharedPrefs = context.getSharedPreferences(GV_SHARED_PREFS,
                Context.MODE_PRIVATE);
        gvSharedPrefs.edit().putBoolean(SHOW_HINT_POP_UP, false).apply();
    }

    private void showFileChooser() {
        LOG.info("Importing document");
        if (exceedsMultiPageLimit()) {
            showMultiPageLimitError();
            return;
        }
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        final Intent fileChooserIntent = FileChooserActivity.createIntent(activity);
        final DocumentImportEnabledFileTypes enabledFileTypes;
        if (mInMultiPageState) {
            enabledFileTypes = DocumentImportEnabledFileTypes.IMAGES;
        } else {
            enabledFileTypes = getDocumentImportEnabledFileTypes(mGiniVisionFeatureConfiguration);
        }
        fileChooserIntent.putExtra(FileChooserActivity.EXTRA_IN_DOCUMENT_IMPORT_FILE_TYPES,
                enabledFileTypes);
        fileChooserIntent.setExtrasClassLoader(CameraFragmentImpl.class.getClassLoader());
        mFragment.startActivityForResult(fileChooserIntent, REQ_CODE_CHOOSE_FILE);
    }

    boolean onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQ_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK) {
                importDocumentFromIntent(data);
            } else if (resultCode != RESULT_CANCELED) {
                final String message;
                if (resultCode == FileChooserActivity.RESULT_ERROR) {
                    final GiniVisionError error = data.getParcelableExtra(
                            FileChooserActivity.EXTRA_OUT_ERROR);
                    message = "Document import failed: " + error.getMessage();
                } else {
                    message = "Document import failed: unknown result code " + resultCode;
                }
                LOG.error(message);
                showGenericInvalidFileError();
            }
            return true;
        }
        return false;
    }

    private void importDocumentFromIntent(@NonNull final Intent data) {
        final Activity activity = mFragment
                .getActivity();
        if (activity == null) {
            return;
        }
        if (IntentHelper.hasMultipleUris(data)) {
            final List<Uri> uris = IntentHelper.getUris(data);
            if (uris == null) {
                LOG.error("Document import failed: Intent has no Uris");
                showGenericInvalidFileError();
                return;
            }
            handleMultiPageDocumentAndCallListener(activity, data, uris);
        } else {
            final Uri uri = IntentHelper.getUri(data);
            if (uri == null) {
                LOG.error("Document import failed: Intent has no Uri");
                showGenericInvalidFileError();
                return;
            }
            if (!UriHelper.isUriInputStreamAvailable(uri, activity)) {
                LOG.error("Document import failed: InputStream not available for the Uri");
                showGenericInvalidFileError();
                return;
            }

            if (isMultiPageEnabled() && isImage(data, activity)) {
                handleMultiPageDocumentAndCallListener(activity, data,
                        Collections.singletonList(uri));
            } else {
                final FileImportValidator fileImportValidator = new FileImportValidator(activity);
                if (fileImportValidator.matchesCriteria(data, uri)) {
                    createSinglePageDocumentAndCallListener(data, activity);
                } else {
                    final FileImportValidator.Error error = fileImportValidator.getError();
                    if (error != null) {
                        showInvalidFileError(error);
                    } else {
                        showGenericInvalidFileError();
                    }
                }
            }
        }
    }

    private boolean isImage(@NonNull final Intent data, @NonNull final Activity activity) {
        return IntentHelper.hasMimeTypeWithPrefix(data, activity, MimeType.IMAGE_PREFIX.asString());
    }

    private void createSinglePageDocumentAndCallListener(final Intent data,
            final Activity activity) {
        try {
            final GiniVisionDocument document = DocumentFactory.newDocumentFromIntent(data,
                    activity,
                    DeviceHelper.getDeviceOrientation(activity),
                    DeviceHelper.getDeviceType(activity),
                    ImportMethod.PICKER);
            LOG.info("Document imported: {}", document);
            requestClientDocumentCheck(document);
        } catch (final IllegalArgumentException e) {
            LOG.error("Failed to import selected document", e);
            showGenericInvalidFileError();
        }
    }

    private void requestClientDocumentCheck(final GiniVisionDocument document) {
        showActivityIndicatorAndDisableInteraction();
        LOG.debug("Requesting document check from client");
        mListener.onCheckImportedDocument(document,
                new CameraFragmentListener.DocumentCheckResultCallback() {
                    @Override
                    public void documentAccepted() {
                        LOG.debug("Client accepted the document");
                        hideActivityIndicatorAndEnableInteraction();
                        if (document.getType() == Document.Type.IMAGE_MULTI_PAGE) {
                            mProceededToMultiPageReview = true;
                            final ImageMultiPageDocument multiPageDocument =
                                    (ImageMultiPageDocument) document;
                            addToMultiPageDocumentMemoryStore(multiPageDocument);
                            mListener.onProceedToMultiPageReviewScreen(
                                    multiPageDocument);
                        } else {
                            mListener.onDocumentAvailable(document);
                        }
                    }

                    @Override
                    public void documentRejected(@NonNull final String messageForUser) {
                        LOG.debug("Client rejected the document: {}", messageForUser);
                        hideActivityIndicatorAndEnableInteraction();
                        showInvalidFileAlert(messageForUser);
                    }
                });
    }

    private void addToMultiPageDocumentMemoryStore(final ImageMultiPageDocument multiPageDocument) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal()
                    .getImageMultiPageDocumentMemoryStore()
                    .setMultiPageDocument(multiPageDocument);
        }
    }

    private void handleMultiPageDocumentAndCallListener(@NonNull final Context context,
            @NonNull final Intent intent, @NonNull final List<Uri> uris) {
        showActivityIndicatorAndDisableInteraction();
        if (mImportUrisAsyncTask != null) {
            mImportUrisAsyncTask.cancel(true);
        }
        if (!GiniVision.hasInstance()) {
            LOG.error(
                    "Cannot import multi-page document. GiniVision instance not available. Create it with GiniVision.newInstance().");
            return;
        }
        if (exceedsMultiPageLimit()) {
            hideActivityIndicatorAndEnableInteraction();
            showMultiPageLimitError();
            return;
        }
        mImportUrisAsyncTask = new ImportImageDocumentUrisAsyncTask(
                context, intent, GiniVision.getInstance(),
                Document.Source.newExternalSource(), ImportMethod.PICKER,
                new AsyncCallback<ImageMultiPageDocument, ImportedFileValidationException>() {
                    @Override
                    public void onSuccess(final ImageMultiPageDocument multiPageDocument) {
                        if (mMultiPageDocument == null) {
                            mInMultiPageState = true;
                            mMultiPageDocument = multiPageDocument;
                        } else {
                            mMultiPageDocument.addDocuments(multiPageDocument.getDocuments());
                        }
                        if (mMultiPageDocument.getDocuments().isEmpty()) {
                            LOG.error("Document import failed: Intent did not contain images");
                            showGenericInvalidFileError();
                            mMultiPageDocument = null; // NOPMD
                            mInMultiPageState = false;
                            return;
                        }
                        LOG.info("Document imported: {}", mMultiPageDocument);
                        updateImageStack();
                        hideActivityIndicatorAndEnableInteraction();
                        requestClientDocumentCheck(mMultiPageDocument);
                    }

                    @Override
                    public void onError(final ImportedFileValidationException exception) {
                        LOG.error("Document import failed", exception);
                        hideActivityIndicatorAndEnableInteraction();
                        final FileImportValidator.Error error = exception.getValidationError();
                        if (error != null) {
                            showInvalidFileError(error);
                        } else {
                            showGenericInvalidFileError();
                        }
                    }

                    @Override
                    public void onCancelled() {

                    }
                });
        mImportUrisAsyncTask.execute(uris.toArray(new Uri[uris.size()]));
    }

    private boolean exceedsMultiPageLimit() {
        return mInMultiPageState && mMultiPageDocument.getDocuments().size()
                >= FileImportValidator.DOCUMENT_PAGE_LIMIT;
    }

    @Override
    public void showActivityIndicatorAndDisableInteraction() {
        if (mActivityIndicator == null
                || mActivityIndicatorBackground == null) {
            return;
        }
        mActivityIndicatorBackground.setVisibility(View.VISIBLE);
        mActivityIndicatorBackground.setClickable(true);
        mActivityIndicator.setVisibility(View.VISIBLE);
        disableInteraction();
    }

    @Override
    public void hideActivityIndicatorAndEnableInteraction() {
        if (mActivityIndicator == null
                || mActivityIndicatorBackground == null) {
            return;
        }
        mActivityIndicatorBackground.setVisibility(View.INVISIBLE);
        mActivityIndicatorBackground.setClickable(false);
        mActivityIndicator.setVisibility(View.INVISIBLE);
        enableInteraction();
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        if (mFragment.getActivity() == null || mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, null, null,
                duration).show();
    }

    private void updateImageStack() {
        final List<ImageDocument> documents = mMultiPageDocument.getDocuments();
        if (!documents.isEmpty()) {
            mImageStack.removeImages();
        }
        final int size = documents.size();
        if (size >= 3) {
            showImageDocumentsInStack(
                    Arrays.asList(
                            documents.get(size - 1),
                            documents.get(size - 2),
                            documents.get(size - 3)),
                    Arrays.asList(
                            ImageStack.Position.TOP,
                            ImageStack.Position.MIDDLE,
                            ImageStack.Position.BOTTOM));
        } else if (size == 2) {
            showImageDocumentsInStack(
                    Arrays.asList(
                            documents.get(size - 1),
                            documents.get(size - 2)),
                    Arrays.asList(
                            ImageStack.Position.TOP,
                            ImageStack.Position.MIDDLE));
        } else if (size == 1) {
            showImageDocumentsInStack(
                    Collections.singletonList(
                            documents.get(size - 1)),
                    Collections.singletonList(
                            ImageStack.Position.TOP));
        }
    }

    private void showImageDocumentsInStack(@NonNull final List<ImageDocument> documents,
            @NonNull final List<ImageStack.Position> positions) {
        if (!GiniVision.hasInstance()) {
            LOG.error(
                    "Cannot show images in stack. GiniVision instance not available. Create it with GiniVision.newInstance().");
        }
        if (documents.size() != positions.size()) {
            return;
        }
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        final int imagesToLoadCount = documents.size();
        final AtomicInteger imagesLoadedCounter = new AtomicInteger();
        for (int i = 0; i < documents.size(); i++) {
            final ImageDocument document = documents.get(i);
            final ImageStack.Position position = positions.get(i);
            GiniVision.getInstance().internal().getPhotoMemoryCache()
                    .get(activity, document, new AsyncCallback<Photo, Exception>() { // NOPMD
                        @Override
                        public void onSuccess(final Photo result) {
                            mImageStack.setImage(
                                    new ImageStack.StackBitmap(result.getBitmapPreview(),
                                            document.getRotationForDisplay()), position);
                            imagesLoadedCounter.incrementAndGet();
                            if (imagesToLoadCount == imagesLoadedCounter.get()) {
                                mImageStack.setImageCount(mMultiPageDocument.getDocuments().size());
                            }
                        }

                        @Override
                        public void onError(final Exception exception) {
                            mImageStack.setImage(null, position);
                            imagesLoadedCounter.incrementAndGet();
                            if (imagesToLoadCount == imagesLoadedCounter.get()) {
                                mImageStack.setImageCount(mMultiPageDocument.getDocuments().size());
                            }
                        }

                        @Override
                        public void onCancelled() {
                            // Not used
                        }
                    });
        }
    }

    private void enableInteraction() {
        if (mCameraPreview == null
                || mButtonImportDocument == null
                || mButtonCameraTrigger == null) {
            return;
        }
        mCameraPreview.setEnabled(true);
        mButtonImportDocument.setEnabled(true);
        mButtonCameraTrigger.setEnabled(true);
    }

    private void disableInteraction() {
        if (mCameraPreview == null
                || mButtonImportDocument == null
                || mButtonCameraTrigger == null) {
            return;
        }
        mCameraPreview.setEnabled(false);
        mButtonImportDocument.setEnabled(false);
        mButtonCameraTrigger.setEnabled(false);
    }

    private void showInvalidFileError(@NonNull final FileImportValidator.Error error) {
        LOG.error("Invalid document {}", error.toString());
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        showInvalidFileAlert(activity.getString(error.getTextResource()));
    }

    private void showGenericInvalidFileError() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        final String message = activity.getString(R.string.gv_document_import_invalid_document);
        LOG.error("Invalid document {}", message);
        showInvalidFileAlert(message);
    }

    private void showInvalidFileAlert(final String message) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        mFragment.showAlertDialog(message,
                activity.getString(R.string.gv_document_import_pick_another_document),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            final DialogInterface dialogInterface,
                            final int i) {
                        showFileChooser();
                    }
                }, activity.getString(R.string.gv_document_import_close_error), null, null);
    }

    @UiThread
    private void onPictureTaken(final Photo photo, final Throwable throwable) {
        if (throwable != null) {
            handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED, "Failed to take picture",
                    throwable);
            mCameraController.startPreview();
            mIsTakingPicture = false;
        } else {
            if (photo != null) {
                LOG.info("Picture taken");
                showActivityIndicatorAndDisableInteraction();
                photo.edit().compressByDefault().applyAsync(new PhotoEdit.PhotoEditCallback() {
                    @Override
                    public void onDone(@NonNull final Photo result) {
                        hideActivityIndicatorAndEnableInteraction();
                        if (mInMultiPageState) {
                            final ImageDocument document = createSavedDocument(result);
                            if (document == null) {
                                handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED,
                                        "Failed to take picture: could not save picture to disk",
                                        null);
                                mCameraController.startPreview();
                                mIsTakingPicture = false;
                                return;
                            }
                            mMultiPageDocument.addDocument(document);
                            mImageStack.addImage(
                                    new ImageStack.StackBitmap(result.getBitmapPreview(),
                                            document.getRotationForDisplay()),
                                    new TransitionListenerAdapter() {
                                        @Override
                                        public void onTransitionEnd(
                                                @NonNull final Transition transition) {
                                            mIsTakingPicture = false;
                                        }
                                    });
                            mCameraController.startPreview();
                        } else {
                            if (isMultiPageEnabled()) {
                                final ImageDocument document = createSavedDocument(result);
                                if (document == null) {
                                    handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED,
                                            "Failed to take picture: could not save picture to disk",
                                            null);
                                    mCameraController.startPreview();
                                    mIsTakingPicture = false;
                                    return;
                                }
                                mInMultiPageState = true;
                                mMultiPageDocument = new ImageMultiPageDocument(
                                        Document.Source.newCameraSource(), ImportMethod.NONE);
                                GiniVision.getInstance().internal()
                                        .getImageMultiPageDocumentMemoryStore()
                                        .setMultiPageDocument(mMultiPageDocument);
                                mMultiPageDocument.addDocument(document);
                                mImageStack.addImage(
                                        new ImageStack.StackBitmap(result.getBitmapPreview(),
                                                document.getRotationForDisplay()),
                                        new TransitionListenerAdapter() {
                                            @Override
                                            public void onTransitionEnd(
                                                    @NonNull final Transition transition) {
                                                mListener.onProceedToMultiPageReviewScreen(
                                                        mMultiPageDocument);
                                                mIsTakingPicture = false;
                                            }
                                        });
                            } else {
                                final ImageDocument document =
                                        DocumentFactory.newImageDocumentFromPhoto(
                                                result);
                                mListener.onDocumentAvailable(document);
                                mIsTakingPicture = false;
                            }
                            mCameraController.startPreview();
                        }
                    }

                    @Override
                    public void onFailed() {
                        hideActivityIndicatorAndEnableInteraction();
                        handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED,
                                "Failed to take picture: picture compression failed", null);
                        mCameraController.startPreview();
                        mIsTakingPicture = false;
                    }
                });
            } else {
                handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED,
                        "Failed to take picture: no picture from the camera", null);
                mCameraController.startPreview();
                mIsTakingPicture = false;
            }
        }
    }

    private void showMultiPageLimitError() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        mFragment.showAlertDialog(activity.getString(R.string.gv_document_error_too_many_pages),
                activity.getString(R.string.gv_document_error_multi_page_limit_review_pages_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            final DialogInterface dialogInterface,
                            final int i) {
                        mProceededToMultiPageReview = true;
                        mListener.onProceedToMultiPageReviewScreen(mMultiPageDocument);
                    }
                }, activity.getString(R.string.gv_document_error_multi_page_limit_cancel_button),
                null, null);
    }

    @Nullable
    private ImageDocument createSavedDocument(@NonNull final Photo photo) {
        if (!GiniVision.hasInstance()) {
            LOG.error(
                    "Cannot save document. GiniVision instance not available. Create it with GiniVision.newInstance().");
        }
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return null;
        }
        final ImageDiskStore imageDiskStore =
                GiniVision.getInstance().internal().getImageDiskStore();
        final Uri savedAtUri = imageDiskStore.save(activity, photo.getData());
        return DocumentFactory.newImageDocumentFromPhoto(photo, savedAtUri);
    }

    private void setSurfaceViewCallback() {
        mCameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                LOG.debug("Surface created");
                mSurfaceCreatedFuture.complete(holder);
            }

            @Override
            public void surfaceChanged(final SurfaceHolder holder, final int format,
                    final int width, final int height) {
                LOG.debug("Surface changed");
            }

            @Override
            public void surfaceDestroyed(final SurfaceHolder holder) {
                LOG.debug("Surface destroyed");
                mSurfaceCreatedFuture = new CompletableFuture<>();
            }
        });
    }

    @Deprecated
    @Override
    public void showDocumentCornerGuides() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        showDocumentCornerGuidesAnimated();
    }

    private void showDocumentCornerGuidesAnimated() {
        mImageCorners.animate().alpha(1.0f);
    }

    @Deprecated
    @Override
    public void hideDocumentCornerGuides() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        hideDocumentCornerGuidesAnimated();
    }

    private void hideDocumentCornerGuidesAnimated() {
        mImageCorners.animate().alpha(0.0f);
    }

    @Deprecated
    @Override
    public void showCameraTriggerButton() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        showCameraTriggerButtonAnimated();
    }

    private void showCameraTriggerButtonAnimated() {
        enableCameraTriggerButtonAnimated();
    }

    @Deprecated
    @Override
    public void hideCameraTriggerButton() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        hideCameraTriggerButtonAnimated();
    }

    private void hideCameraTriggerButtonAnimated() {
        disableCameraTriggerButtonAnimated(0.0f);
    }

    private void disableCameraTriggerButtonAnimated(final float alpha) {
        mButtonCameraTrigger.clearAnimation();
        mButtonCameraTrigger.animate().alpha(alpha).start();
        mButtonCameraTrigger.setEnabled(false);
    }

    private void enableCameraTriggerButtonAnimated() {
        mButtonCameraTrigger.clearAnimation();
        mButtonCameraTrigger.animate().alpha(1.0f).start();
        mButtonCameraTrigger.setEnabled(true);
    }

    @Override
    public void showInterface() {
        if (!mInterfaceHidden || isNoPermissionViewVisible()) {
            return;
        }
        mInterfaceHidden = false;
        showInterfaceAnimated();
    }

    private void showInterfaceAnimated() {
        showCameraTriggerButtonAnimated();
        showDocumentCornerGuidesAnimated();
        showImageStackAnimated();
        if (mImportDocumentButtonEnabled) {
            showUploadHintPopUpOnFirstExecution();
            showImportDocumentButtonAnimated();
        }
        showFlashButtonAnimated();
    }

    private void showImageStackAnimated() {
        mImageStack.animate().alpha(1.0f).start();
    }

    private void showImportDocumentButtonAnimated() {
        mImportButtonContainer.animate().alpha(1.0f);
        mButtonImportDocument.setEnabled(true);
    }

    private void showFlashButtonAnimated() {
        mButtonCameraFlash.animate().alpha(1.0f);
        mButtonCameraFlash.setEnabled(true);
    }

    @Override
    public void hideInterface() {
        if (mInterfaceHidden || isNoPermissionViewVisible()) {
            return;
        }
        mInterfaceHidden = true;
        hideInterfaceAnimated();
    }

    private void hideInterfaceAnimated() {
        hideCameraTriggerButtonAnimated();
        hideDocumentCornerGuidesAnimated();
        hideImageStackAnimated();
        if (mImportDocumentButtonEnabled) {
            hideUploadHintPopUp(null);
            hideImportDocumentButtonAnimated();
        }
        hideFlashButtonAnimated();
    }

    private void hideImageStackAnimated() {
        mImageStack.animate().alpha(0.0f).start();
    }

    private void hideImportDocumentButtonAnimated() {
        mImportButtonContainer.animate().alpha(0.0f);
        mButtonImportDocument.setEnabled(false);
    }

    private void showNoPermissionView() {
        hideCameraPreviewAnimated();
        hideInterfaceAnimated();
        inflateNoPermissionStub();
        setUpNoPermissionButton();
        if (mLayoutNoPermission != null) {
            mLayoutNoPermission.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNoPermissionViewVisible() {
        return mLayoutNoPermission != null
                && mLayoutNoPermission.getVisibility() == View.VISIBLE;
    }

    private void inflateNoPermissionStub() {
        if (mLayoutNoPermission == null) {
            LOG.debug("Inflating no permission view");
            mLayoutNoPermission = (LinearLayout) mViewStubInflater.inflate();
        }
    }

    private void hideNoPermissionView() {
        showCameraPreviewAnimated();
        if (!mInterfaceHidden) {
            showInterfaceAnimated();
        }
        if (mLayoutNoPermission != null) {
            mLayoutNoPermission.setVisibility(View.GONE);
        }
    }

    private void setUpNoPermissionButton() {
        if (isMarshmallowOrLater()) {
            handleNoPermissionButtonClick();
        } else {
            hideNoPermissionButton();
        }
    }

    private void hideCameraPreviewAnimated() {
        mCameraPreview.animate().alpha(0.0f);
        mCameraPreview.setEnabled(false);
    }

    private void showCameraPreviewAnimated() {
        mCameraPreview.animate().alpha(1.0f);
        mCameraPreview.setEnabled(true);
    }

    private void handleNoPermissionButtonClick() {
        final View view = mFragment.getView();
        if (view == null) {
            return;
        }
        final Button button = view.findViewById(R.id.gv_button_camera_no_permission);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startApplicationDetailsSettings();
            }
        });
    }

    private void hideNoPermissionButton() {
        final View view = mFragment.getView();
        if (view == null) {
            return;
        }
        final Button button = (Button) view.findViewById(R.id.gv_button_camera_no_permission);
        button.setVisibility(View.GONE);
    }

    private void hideFlashButtonAnimated() {
        mButtonCameraFlash.animate().alpha(0.0f);
        mButtonCameraFlash.setEnabled(false);
    }

    private void startApplicationDetailsSettings() {
        LOG.debug("Starting Application Details");
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        ApplicationHelper.startApplicationDetailsSettings(activity);
    }

    @VisibleForTesting
    void initCameraController(final Activity activity) {
        if (mCameraController == null) {
            LOG.debug("CameraController created");
            mCameraController = createCameraController(activity);
        }
        if (isQRCodeScanningEnabled(mGiniVisionFeatureConfiguration)) {
            final int rotation = mCameraController.getCameraRotation();
            mCameraController.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(final byte[] data, final Camera camera) {
                    if (mPaymentQRCodeReader == null) {
                        return;
                    }
                    mPaymentQRCodeReader.readFromImage(data, mCameraController.getPreviewSize(),
                            rotation);
                }
            });
        }
    }

    @NonNull
    protected CameraInterface createCameraController(final Activity activity) {
        return new CameraController(activity);
    }

    private void handleError(final GiniVisionError.ErrorCode errorCode,
            @NonNull final String message,
            @Nullable final Throwable throwable) {
        String errorMessage = message;
        if (throwable != null) {
            LOG.error(message, throwable);
            // Add error info to the message to help clients, if they don't have logging enabled
            errorMessage = errorMessage + ": " + throwable.getMessage();
        }
        handleError(errorCode, errorMessage);
    }

    private void handleError(final GiniVisionError.ErrorCode errorCode,
            @NonNull final String message) {
        handleError(new GiniVisionError(errorCode, message));
    }

    private void handleError(@NonNull final GiniVisionError error) {
        LOG.error(error.getMessage());
        mListener.onError(error);
    }
}
