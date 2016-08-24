package net.gini.android.vision.camera;

import static net.gini.android.vision.camera.Util.cameraExceptionToGiniVisionError;
import static net.gini.android.vision.internal.util.AndroidHelper.isMarshmallowOrLater;
import static net.gini.android.vision.internal.util.ContextHelper.getClientApplicationId;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
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
import net.gini.android.vision.internal.camera.api.CameraController;
import net.gini.android.vision.internal.camera.api.CameraException;
import net.gini.android.vision.internal.camera.api.CameraInterface;
import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.view.CameraPreviewSurface;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.ui.ViewStubSafeInflater;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

import jersey.repackaged.jsr166e.CompletableFuture;

class CameraFragmentImpl implements CameraFragmentInterface {

    private static final Logger LOG = LoggerFactory.getLogger(CameraFragmentImpl.class);

    private static final CameraFragmentListener NO_OP_LISTENER = new CameraFragmentListener() {
        @Override
        public void onDocumentAvailable(@NonNull Document document) {
        }

        @Override
        public void onError(@NonNull GiniVisionError error) {
        }
    };

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

    private ViewStubSafeInflater mViewStubInflater;

    private CompletableFuture<SurfaceHolder> mSurfaceCreatedFuture = new CompletableFuture<>();
    private boolean mIsTakingPicture = false;

    CameraFragmentImpl(@NonNull FragmentImplCallback fragment) {
        mFragment = fragment;
    }

    public void setListener(CameraFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_camera, container, false);
        bindViews(view);
        setInputHandlers();
        setSurfaceViewCallback();
        return view;
    }

    public void onStart() {
        if (mFragment.getActivity() == null) {
            return;
        }
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
                                mCameraPreview.setPreviewSize(mCameraController.getPreviewSize());
                                startPreview(surfaceHolder);
                                enableTapToFocus();
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
        mCameraFocusIndicator.animate().setDuration(200).alpha(1.0f);
    }

    private void hideFocusIndicator() {
        mCameraFocusIndicator.animate().setDuration(200).alpha(0.0f);
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

    public void onStop() {
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
        LOG.debug("Showing document corner guides");
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
        LOG.debug("Hiding document corner guides");
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
        LOG.debug("Showing camera trigger button");
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
        LOG.debug("Hiding camera trigger button");
        mButtonCameraTrigger.animate().alpha(0.0f);
        mButtonCameraTrigger.setEnabled(false);
    }

    private void showNoPermissionView() {
        hideCameraPreviewAnimated();
        hideCameraTriggerButtonAnimated();
        hideDocumentCornerGuidesAnimated();
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

    public void hideNoPermissionView() {
        LOG.debug("Hiding no permission view");
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
        LOG.debug("Hiding camera preview");
        mCameraPreview.animate().alpha(0.0f);
        mCameraPreview.setEnabled(false);
    }

    private void showCameraPreviewAnimated() {
        LOG.debug("Showing camera preview");
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
        LOG.debug("Hiding no permission button");
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
