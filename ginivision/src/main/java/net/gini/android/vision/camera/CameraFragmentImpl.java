package net.gini.android.vision.camera;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import static net.gini.android.vision.GiniVisionError.ErrorCode.DOCUMENT_IMPORT;
import static net.gini.android.vision.camera.Util.cameraExceptionToGiniVisionError;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;
import static net.gini.android.vision.internal.util.AndroidHelper.isMarshmallowOrLater;
import static net.gini.android.vision.internal.util.ContextHelper.getClientApplicationId;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.internal.camera.api.CameraController;
import net.gini.android.vision.internal.camera.api.CameraException;
import net.gini.android.vision.internal.camera.api.CameraInterface;
import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.view.CameraPreviewSurface;
import net.gini.android.vision.internal.fileimport.FileChooserActivity;
import net.gini.android.vision.internal.permission.PermissionRequestListener;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.ui.ViewStubSafeInflater;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import jersey.repackaged.jsr166e.CompletableFuture;

class CameraFragmentImpl implements CameraFragmentInterface {

    public static final String GV_SHARED_PREFS = "GV_SHARED_PREFS";
    public static final int DEFAULT_ANIMATION_DURATION = 200;
    private static final Logger LOG = LoggerFactory.getLogger(CameraFragmentImpl.class);

    private static final CameraFragmentListener NO_OP_LISTENER = new CameraFragmentListener() {
        @Override
        public void onDocumentAvailable(@NonNull Document document) {
        }

        @Override
        public void onError(@NonNull GiniVisionError error) {
        }
    };

    private static final int REQ_CODE_CHOOSE_FILE = 1;
    private static final int SHOW_ERROR_DURATION = 4000;
    public static final String SHOW_HINT_POP_UP = "SHOW_HINT_POP_UP";

    private final CameraFragmentImplCallback mFragment;
    private View mImageCorners;
    private CameraFragmentListener mListener = NO_OP_LISTENER;
    private final UIExecutor mUIExecutor = new UIExecutor();
    private CameraController mCameraController;

    private RelativeLayout mLayoutRoot;
    private CameraPreviewSurface mCameraPreview;
    private ImageView mCameraFocusIndicator;
    private ImageButton mButtonCameraTrigger;
    private LinearLayout mLayoutNoPermission;
    private ImageButton mButtonImportDocument;
    private View mUploadHintCloseButton;
    private View mUploadHintContainer;
    private View mUploadHintContainerArrow;

    private ViewStubSafeInflater mViewStubInflater;

    private CompletableFuture<SurfaceHolder> mSurfaceCreatedFuture = new CompletableFuture<>();
    private boolean mIsTakingPicture = false;

    private boolean mImportDocumentButtonEnabled = false;

    CameraFragmentImpl(@NonNull CameraFragmentImplCallback fragment) {
        mFragment = fragment;
    }

    void setListener(CameraFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        forcePortraitOrientationOnPhones(activity);
    }

    View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_camera, container, false);
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
        initViews();
        initCameraController(activity);

        final CompletableFuture<Void> openCameraCompletable = openCamera();
        final CompletableFuture<SurfaceHolder> surfaceCreationCompletable = handleSurfaceCreation();

        CompletableFuture.allOf(openCameraCompletable, surfaceCreationCompletable)
                .handle(new CompletableFuture.BiFun<Void, Throwable, Object>() {
                    @Override
                    public Object apply(Void aVoid, Throwable throwable) {
                        if (throwable != null) {
                            // Exceptions were handled before
                            return null;
                        }
                        try {
                            SurfaceHolder surfaceHolder = surfaceCreationCompletable.get();
                            if (surfaceHolder != null) {
                                final Size previewSize =
                                        mCameraController.getPreviewSizeForDisplay();
                                mCameraPreview.setPreviewSize(previewSize);
                                startPreview(surfaceHolder);
                                enableTapToFocus();
                                showUploadHintPopUpOnFirstExecution();
                            } else {
                                handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW,
                                        "Cannot start preview: no SurfaceHolder received for SurfaceView", null);
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW, "Cannot start preview", e);
                        }
                        return null;
                    }
                });
    }

    private void showUploadHintPopUpOnFirstExecution() {
        if(shouldShowHintPopUp()) {
            ViewCompat.animate(mUploadHintContainer)
                    .alpha(1)
                    .setDuration(DEFAULT_ANIMATION_DURATION)
                    .start();
            ViewCompat.animate(mUploadHintContainerArrow)
                    .alpha(1)
                    .setDuration(DEFAULT_ANIMATION_DURATION)
                    .start();
        }
    }

    private boolean shouldShowHintPopUp() {
        Context context = mFragment.getActivity();
        if(context != null) {
            SharedPreferences gvSharedPrefs = context.getSharedPreferences(GV_SHARED_PREFS, Context.MODE_PRIVATE);
            return gvSharedPrefs.getBoolean(SHOW_HINT_POP_UP, true);
        }
        return false;
    }

    private void startPreview(SurfaceHolder holder) {
        mCameraController.startPreview(holder)
                .handle(new CompletableFuture.BiFun<Void, Throwable, Void>() {
                    @Override
                    public Void apply(final Void aVoid, final Throwable throwable) {
                        if (throwable != null) {
                            handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW, "Cannot start preview", throwable);
                        }
                        return null;
                    }
                });
    }

    private void enableTapToFocus() {
        mCameraController.enableTapToFocus(mCameraPreview, new CameraInterface.TapToFocusListener() {
            @Override
            public void onFocusing(Point point) {
                showFocusIndicator(point);
            }

            @Override
            public void onFocused(boolean success) {
                hideFocusIndicator();
            }
        });
    }

    private void showFocusIndicator(Point point) {
        int top = Math.round((mLayoutRoot.getHeight() - mCameraPreview.getHeight()) / 2.0f);
        int left = Math.round((mLayoutRoot.getWidth() - mCameraPreview.getWidth()) / 2.0f);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mCameraFocusIndicator.getLayoutParams();
        layoutParams.leftMargin = (int) Math.round(left + point.x - (mCameraFocusIndicator.getWidth() / 2.0));
        layoutParams.topMargin = (int) Math.round(top + point.y - (mCameraFocusIndicator.getHeight() / 2.0));
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
                                handleError(GiniVisionError.ErrorCode.CAMERA_OPEN_FAILED, "Failed to open camera", throwable);
                            } else if (throwable instanceof Exception) {
                                handleCameraException((Exception) throwable);
                            } else {
                                handleError(GiniVisionError.ErrorCode.CAMERA_OPEN_FAILED, "Failed to open camera", throwable);
                            }
                            throw new RuntimeException(throwable);
                        } else {
                            LOG.info("Camera opened");
                            hideNoPermissionView();
                        }
                        return null;
                    }
                });
    }

    private CompletableFuture<SurfaceHolder> handleSurfaceCreation() {
        return mSurfaceCreatedFuture.handle(new CompletableFuture.BiFun<SurfaceHolder, Throwable, SurfaceHolder>() {
            @Override
            public SurfaceHolder apply(SurfaceHolder surfaceHolder, Throwable throwable) {
                if (throwable != null) {
                    handleError(GiniVisionError.ErrorCode.CAMERA_NO_PREVIEW, "Cannot start preview", throwable);
                    throw new RuntimeException(throwable);
                }
                return surfaceHolder;
            }
        });
    }

    private void handleCameraException(@NonNull Exception e) {
        LOG.error("Failed to open camera", e);
        GiniVisionError error = cameraExceptionToGiniVisionError(e);
        if (error.getErrorCode() == GiniVisionError.ErrorCode.CAMERA_NO_ACCESS) {
            showNoPermissionView();
        } else {
            mListener.onError(cameraExceptionToGiniVisionError(e));
        }
    }

    void onStop() {
        closeCamera();
    }

    private void closeCamera() {
        LOG.info("Closing camera");
        mCameraController.disableTapToFocus(mCameraPreview);
        mCameraController.stopPreview();
        mCameraController.close();
        LOG.info("Camera closed");
    }

    private void bindViews(View view) {
        mLayoutRoot = view.findViewById(R.id.gv_root);
        mCameraPreview = view.findViewById(R.id.gv_camera_preview);
        mImageCorners = view.findViewById(R.id.gv_image_corners);
        mCameraFocusIndicator = view.findViewById(R.id.gv_camera_focus_indicator);
        mButtonCameraTrigger = view.findViewById(R.id.gv_button_camera_trigger);
        ViewStub stubNoPermission = view.findViewById(R.id.gv_stub_camera_no_permission);
        mViewStubInflater = new ViewStubSafeInflater(stubNoPermission);
        mButtonImportDocument = view.findViewById(R.id.gv_button_import_document);
        mUploadHintContainer = view.findViewById(R.id.gv_upload_hint_container);
        mUploadHintContainerArrow = view.findViewById(R.id.gv_upload_hint_container2);
        mUploadHintCloseButton = view.findViewById(R.id.gv_upload_hint_button);
    }

    private void initViews() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        if (FileChooserActivity.canChooseFiles(activity)) {
            mImportDocumentButtonEnabled = true;
            mButtonImportDocument.setVisibility(View.VISIBLE);
            showImportDocumentButtonAnimated();
        }
    }

    private void setInputHandlers() {
        mButtonCameraTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOG.info("Taking picture");
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
                                        mIsTakingPicture = false;
                                        callListener(photo, throwable);
                                    }
                                });
                                return null;
                            }
                        });
            }
        });
        mButtonImportDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                LOG.info("Requesting read storage permission");
                requestStoragePermission(new PermissionRequestListener() {
                    @Override
                    public void permissionGranted() {
                        LOG.info("Read storage permission granted");
                        showFileChooser();
                    }

                    @Override
                    public void permissionDenied() {
                        LOG.info("Read storage permission denied");
                        showStoragePermissionDeniedDialog();
                    }

                    @Override
                    public void shouldShowRequestPermissionRationale(
                            @NonNull final RationaleResponse response) {
                        LOG.info("Show read storage permission rationale");
                        showStoragePermissionRationale(response);
                    }
                });
            }
        });
        mUploadHintCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                closeUploadHintPopUp();
            }
        });
    }

    private void closeUploadHintPopUp() {
        ViewCompat.animate(mUploadHintContainerArrow)
                .alpha(0)
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .start();
        ViewCompat.animate(mUploadHintContainer)
                .alpha(0)
                .setDuration(DEFAULT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListener() {
                    @Override
                    public void onAnimationStart(final View view) {
                    }

                    @Override
                    public void onAnimationEnd(final View view) {
                        mUploadHintContainerArrow.setVisibility(View.GONE);
                        mUploadHintContainer.setVisibility(View.GONE);
                        Context context = view.getContext();
                        savePopUpShown(context);
                    }

                    @Override
                    public void onAnimationCancel(final View view) {
                    }
                })
                .start();
    }

    private void savePopUpShown(final Context context) {
        SharedPreferences gvSharedPrefs = context.getSharedPreferences(GV_SHARED_PREFS, Context.MODE_PRIVATE);
        gvSharedPrefs.edit().putBoolean(SHOW_HINT_POP_UP, false).apply();
    }

    private void showStoragePermissionRationale(
            @NonNull final PermissionRequestListener.RationaleResponse response) {
        mFragment.showAlertDialog(R.string.gv_storage_permission_rationale,
                R.string.gv_storage_permission_rationale_positive_button,
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface,
                    final int i) {
                LOG.info("Requesting storage permission from rationale");
                response.requestPermission();
            }
        });
    }

    private void showStoragePermissionDeniedDialog() {
        mFragment.showAlertDialog(R.string.gv_storage_permission_denied,
                R.string.gv_storage_permission_denied_positive_button,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            final DialogInterface dialogInterface,
                            final int i) {
                        LOG.info("Open app details in Settings app");
                        showAppDetailsSettingsScreen();
                    }
                }, R.string.gv_storage_permission_denied_negative_button);
    }

    private void showFileChooser() {
        LOG.info("Importing document");
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        Intent fileChooserIntent = FileChooserActivity.createIntent(activity);
        mFragment.startActivityForResult(fileChooserIntent, REQ_CODE_CHOOSE_FILE);
    }

    private void showAppDetailsSettingsScreen() {
        final Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        final Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        final Uri uri = Uri.fromParts("package",
                activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    private void requestStoragePermission(@NonNull final PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFragment.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE, listener);
        } else {
            listener.permissionGranted();
        }
    }

    boolean onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQ_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK) {
                importDocumentFromIntent(data);
            } else if (resultCode != RESULT_CANCELED) {
                final GiniVisionError error;
                if (resultCode == FileChooserActivity.RESULT_ERROR) {
                    error = data.getParcelableExtra(
                            FileChooserActivity.EXTRA_OUT_ERROR);
                } else {
                    error = new GiniVisionError(DOCUMENT_IMPORT,
                            "Document import finished with unknown result code: "
                                    + resultCode);
                }
                handleError(error);
            }
            return true;
        }
        return false;
    }

    private void importDocumentFromIntent(final Intent data) {
        final Activity activity = mFragment
                .getActivity();
        if (activity == null) {
            return;
        }
        final Uri uri = data.getData();
        if (uri == null) {
            handleError(DOCUMENT_IMPORT, "Failed to import selected document");
            return;
        }
        final FileImportValidator fileImportValidator = new FileImportValidator(activity);
        if (fileImportValidator.matchesCriteria(uri)) {
            createDocumentAndCallListener(data, activity);
        } else {
            showInvalidFileError(fileImportValidator.getError());
        }
    }

    private void createDocumentAndCallListener(final Intent data, final Activity activity) {
        try {
            final Document document = DocumentFactory.newDocumentFromIntent(data,
                    activity,
                    DeviceHelper.getDeviceOrientation(activity),
                    DeviceHelper.getDeviceType(activity),
                    "picker");
            LOG.info("Document imported: {}", document);
            mListener.onDocumentAvailable(document);
        } catch (IllegalArgumentException e) {
            handleError(DOCUMENT_IMPORT,
                    "Failed to import selected document", e);
        }
    }

    private void showInvalidFileError(@Nullable final FileImportValidator.Error error) {
        LOG.error("Invalid document {}", error != null ? error.toString() : "");
        int messageRes = R.string.gv_document_import_invalid_document;
        if (error != null) {
            messageRes = error.getTextResource();
        }
        mFragment.showAlertDialog(messageRes,
                R.string.gv_document_import_pick_another_document,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(
                            final DialogInterface dialogInterface,
                            final int i) {
                        showFileChooser();
                    }
                }, R.string.gv_document_import_close_error);
    }

    @UiThread
    private void callListener(final Photo photo, final Throwable throwable) {
        if (throwable != null) {
            handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED, "Failed to take picture", throwable);
            mCameraController.startPreview();
        } else {
            if (photo != null) {
                LOG.info("Picture taken");
                mListener.onDocumentAvailable(DocumentFactory.newDocumentFromPhoto(photo));
            } else {
                handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED,
                        "Failed to take picture: no picture from the camera", null);
                mCameraController.startPreview();
            }
        }
    }

    private void setSurfaceViewCallback() {
        mCameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                LOG.debug("Surface created");
                mSurfaceCreatedFuture.complete(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                LOG.debug("Surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                LOG.debug("Surface destroyed");
                mSurfaceCreatedFuture = new CompletableFuture<>();
            }
        });
    }

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

    @Override
    public void showCameraTriggerButton() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        showCameraTriggerButtonAnimated();
    }

    private void showCameraTriggerButtonAnimated() {
        mButtonCameraTrigger.animate().alpha(1.0f);
        mButtonCameraTrigger.setEnabled(true);
    }

    @Override
    public void hideCameraTriggerButton() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        hideCameraTriggerButtonAnimated();
    }

    private void hideCameraTriggerButtonAnimated() {
        mButtonCameraTrigger.animate().alpha(0.0f);
        mButtonCameraTrigger.setEnabled(false);
    }

    @Override
    public void showInterface() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        showInterfaceAnimated();
    }

    private void showInterfaceAnimated() {
        showCameraTriggerButtonAnimated();
        showDocumentCornerGuidesAnimated();
        if (mImportDocumentButtonEnabled) {
            showImportDocumentButtonAnimated();
        }
    }

    private void showImportDocumentButtonAnimated() {
        mButtonImportDocument.animate().alpha(1.0f);
        mButtonImportDocument.setEnabled(true);
    }

    @Override
    public void hideInterface() {
        if (isNoPermissionViewVisible()) {
            return;
        }
        hideInterfaceAnimated();
    }

    @Override
    public void showErrorInSnackbar(@NonNull String message, int duration) {
        if (mFragment.getActivity() == null || mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, null, null,
                duration).show();
    }

    @Override
    public void showErrorInSnackbar(@NonNull String message, @NonNull String buttonTitle,
            @NonNull View.OnClickListener onClickListener) {
        if (mFragment.getActivity() == null || mLayoutRoot == null) {
            return;
        }
        ErrorSnackbar.make(mFragment.getActivity(), mLayoutRoot, message, buttonTitle,
                onClickListener, ErrorSnackbar.LENGTH_INDEFINITE).show();
    }

    private void hideInterfaceAnimated() {
        hideCameraTriggerButtonAnimated();
        hideDocumentCornerGuidesAnimated();
        if (mImportDocumentButtonEnabled) {
            hideImportDocumentButtonAnimated();
        }
    }

    private void hideImportDocumentButtonAnimated() {
        mButtonImportDocument.animate().alpha(0.0f);
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
        return mLayoutNoPermission != null &&
                mLayoutNoPermission.getVisibility() == View.VISIBLE;
    }

    private void inflateNoPermissionStub() {
        if (mLayoutNoPermission == null) {
            LOG.debug("Inflating no permission view");
            mLayoutNoPermission = (LinearLayout) mViewStubInflater.inflate();
        }
    }

    private void hideNoPermissionView() {
        showCameraPreviewAnimated();
        showInterfaceAnimated();
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
        View view = mFragment.getView();
        if (view == null) {
            return;
        }
        Button button = view.findViewById(R.id.gv_button_camera_no_permission);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startApplicationDetailsSettings();
            }
        });
    }

    private void hideNoPermissionButton() {
        View view = mFragment.getView();
        if (view == null) {
            return;
        }
        Button button = (Button) view.findViewById(R.id.gv_button_camera_no_permission);
        button.setVisibility(View.GONE);
    }

    private void startApplicationDetailsSettings() {
        Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }
        LOG.debug("Starting Application Details");
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getClientApplicationId(activity), null);
        intent.setData(uri);
        mFragment.startActivity(intent);
    }

    private CameraController initCameraController(Activity activity) {
        if (mCameraController == null) {
            LOG.debug("CameraController created");
            mCameraController = new CameraController(activity);
        }
        return mCameraController;
    }

    private void handleError(GiniVisionError.ErrorCode errorCode, @NonNull String message, @Nullable Throwable throwable) {
        if (throwable != null) {
            LOG.error(message, throwable);
            // Add error info to the message to help clients, if they don't have logging enabled
            message += ": " + throwable.getMessage();
        }
        handleError(errorCode, message);
    }

    private void handleError(GiniVisionError.ErrorCode errorCode, @NonNull String message) {
        handleError(new GiniVisionError(errorCode, message));
    }

    private void handleError(@NonNull final GiniVisionError error) {
        LOG.error(error.getMessage());
        if (error.getErrorCode() == DOCUMENT_IMPORT) {
            final Activity activity = mFragment.getActivity();
            if (activity == null) {
                return;
            }
            showErrorInSnackbar(activity.getString(R.string.gv_document_import_error), SHOW_ERROR_DURATION);
        } else {
            mListener.onError(error);
        }
    }
}
