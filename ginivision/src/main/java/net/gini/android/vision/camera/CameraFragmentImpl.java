package net.gini.android.vision.camera;

import static net.gini.android.vision.camera.Util.cameraExceptionToGiniVisionError;
import static net.gini.android.vision.internal.fileimport.FileChooserActivity.EXTRA_OUT_ERROR;
import static net.gini.android.vision.internal.fileimport.FileChooserActivity.RESULT_ERROR;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;
import static net.gini.android.vision.internal.util.AndroidHelper.isMarshmallowOrLater;
import static net.gini.android.vision.internal.util.ContextHelper.getClientApplicationId;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.internal.camera.api.CameraController;
import net.gini.android.vision.internal.camera.api.CameraException;
import net.gini.android.vision.internal.camera.api.CameraInterface;
import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.Size;
import net.gini.android.vision.internal.camera.view.CameraPreviewSurface;
import net.gini.android.vision.internal.fileimport.FileChooserActivity;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.ui.ViewStubSafeInflater;

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
    public static final String SHOW_HINT_POP_UP = "SHOW_HINT_POP_UP";

    private final FragmentImplCallback mFragment;
    private CameraFragmentListener mListener = NO_OP_LISTENER;
    private final UIExecutor mUIExecutor = new UIExecutor();
    private CameraController mCameraController;

    private RelativeLayout mLayoutRoot;
    private CameraPreviewSurface mCameraPreview;
    private ImageView mCameraFocusIndicator;
    private ImageView mImageCorners;
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

    CameraFragmentImpl(@NonNull FragmentImplCallback fragment) {
        mFragment = fragment;
    }

    void setListener(CameraFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    void onCreate(Bundle savedInstanceState) {
        forcePortraitOrientationOnPhones(mFragment.getActivity());
    }

    View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_camera, container, false);
        bindViews(view);
        setInputHandlers();
        setSurfaceViewCallback();
        return view;
    }

    void onStart() {
        if (mFragment.getActivity() == null) {
            return;
        }
        initViews();
        initCameraController(mFragment.getActivity());

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
                                setPreviewCornersImage(previewSize.width, previewSize.height);
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

    private void setPreviewCornersImage(final int width, final int height) {
        final Activity activity = mFragment.getActivity();
        if (activity == null){
            return;
        }
        final Drawable corners;
        if (width <= height) {
            corners = activity.getResources().getDrawable(R.drawable.gv_camera_preview_corners);
        } else {
            corners = activity.getResources().getDrawable(
                    R.drawable.gv_camera_preview_corners_land);
        }
        mImageCorners.setImageDrawable(corners);
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
        mLayoutRoot = (RelativeLayout) view.findViewById(R.id.gv_root);
        mCameraPreview = (CameraPreviewSurface) view.findViewById(R.id.gv_camera_preview);
        mCameraFocusIndicator = (ImageView) view.findViewById(R.id.gv_camera_focus_indicator);
        mImageCorners = (ImageView) view.findViewById(R.id.gv_image_corners);
        mButtonCameraTrigger = (ImageButton) view.findViewById(R.id.gv_button_camera_trigger);
        ViewStub stubNoPermission = (ViewStub) view.findViewById(R.id.gv_stub_camera_no_permission);
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
                LOG.info("Importing document");
                Intent fileChooserIntent = FileChooserActivity.createIntent(mFragment.getActivity());
                mFragment.startActivityForResult(fileChooserIntent, REQ_CODE_CHOOSE_FILE);
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

    boolean onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQ_CODE_CHOOSE_FILE) {
            if(resultCode != RESULT_ERROR) {
                LOG.info("Document file received");
                Toast.makeText(mFragment.getActivity(), "File received", Toast.LENGTH_LONG).show();
            } else {
                LOG.info("Document file opening gone wrong");
                GiniVisionError error = data.getParcelableExtra(EXTRA_OUT_ERROR);
                Toast.makeText(mFragment.getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return false;
    }

    @UiThread
    private void callListener(final Photo photo, final Throwable throwable) {
        if (throwable != null) {
            handleError(GiniVisionError.ErrorCode.CAMERA_SHOT_FAILED, "Failed to take picture", throwable);
            mCameraController.startPreview();
        } else {
            if (photo != null) {
                LOG.info("Picture taken");
                mListener.onDocumentAvailable(Document.fromPhoto(photo));
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
                setPreviewCornersImage(width, height);
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
        LOG.info("Showing document corner guides");
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
        LOG.info("Hiding document corner guides");
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
        LOG.info("Showing camera trigger button");
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
        LOG.info("Hiding camera trigger button");
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
        LOG.info("Showing document import button");
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

    private void hideInterfaceAnimated() {
        hideCameraTriggerButtonAnimated();
        hideDocumentCornerGuidesAnimated();
        if (mImportDocumentButtonEnabled) {
            hideImportDocumentButtonAnimated();
        }
    }

    private void hideImportDocumentButtonAnimated() {
        LOG.info("Hiding document import button");
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
        LOG.info("Hiding no permission view");
        showCameraPreviewAnimated();
        showCameraTriggerButtonAnimated();
        showDocumentCornerGuidesAnimated();
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
        LOG.info("Hiding camera preview");
        mCameraPreview.animate().alpha(0.0f);
        mCameraPreview.setEnabled(false);
    }

    private void showCameraPreviewAnimated() {
        LOG.info("Showing camera preview");
        mCameraPreview.animate().alpha(1.0f);
        mCameraPreview.setEnabled(true);
    }

    private void handleNoPermissionButtonClick() {
        View view = mFragment.getView();
        if (view == null) {
            return;
        }
        Button button = (Button) view.findViewById(R.id.gv_button_camera_no_permission);
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
        LOG.info("Hiding no permission button");
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
        } else {
            LOG.error(message);
        }
        mListener.onError(new GiniVisionError(errorCode, message));
    }
}
