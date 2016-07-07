package net.gini.android.vision.camera;

import static net.gini.android.vision.camera.Util.cameraExceptionToGiniVisionError;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.api.CameraController;
import net.gini.android.vision.camera.api.CameraInterface;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.ui.FragmentImplCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class CameraFragmentImpl implements CameraFragmentInterface {

    private static final CameraFragmentListener NO_OP_LISTENER = new CameraFragmentListener() {
        @Override
        public void onDocumentAvailable(Document document) {
        }

        @Override
        public void onError(GiniVisionError error) {
        }
    };

    private final FragmentImplCallback mFragment;
    private CameraFragmentListener mListener = NO_OP_LISTENER;

    private CameraController mCameraController;
    private CameraListener mCameraListener;

    private ImageView mCameraPreview;
    private ImageView mImageCorners;
    private ImageButton mButtonCameraTrigger;
    private ViewStub mStubNoPermission;

    CameraFragmentImpl(@NonNull FragmentImplCallback fragment) {
        mFragment = fragment;
    }

    public void setListener(CameraFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
        if (mCameraListener != null) {
            mCameraListener.setFragmentListener(mListener);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_camera, container, false);
        bindViews(view);
        setInputHandlers();
        return view;
    }

    public void onStart() {
        mCameraController = new CameraController();
        createCameraListener();
        mCameraController.setListener(mCameraListener);
        mCameraController.open();
    }

    private void createCameraListener() {
        mCameraListener = new CameraListener(mListener, new CameraListener.Callback() {
            @Override
            public void onCameraNoPermissionError() {
                showNoPermissionView();
            }

            @Override
            public void onCameraError() {
                // TODO: this hack is only for the stub library
                mCameraPreview.setImageDrawable(null);
            }
        });
    }

    public void onStop() {

    }

    private void bindViews(View view) {
        mCameraPreview = (ImageView) view.findViewById(R.id.gv_camera_preview);
        mImageCorners = (ImageView) view.findViewById(R.id.gv_image_corners);
        mButtonCameraTrigger = (ImageButton) view.findViewById(R.id.gv_button_camera_trigger);
        mStubNoPermission = (ViewStub) view.findViewById(R.id.gv_stub_camera_no_permission);
    }

    private void setInputHandlers() {
        mButtonCameraTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] testDocument = loadTestDocument();
                // TODO: this is only for the stub library
                if (testDocument != null) {
                    mListener.onDocumentAvailable(Document.fromPhoto(Photo.fromJpeg(testDocument, 0)));
                } else {
                    mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.CAMERA_UNKNOWN, "Could not load the test document jpeg."));
                }
            }
        });
    }

    @Override
    public void showDocumentCornerGuides() {
        mImageCorners.animate().alpha(1.0f);
    }

    @Override
    public void hideDocumentCornerGuides() {
        mImageCorners.animate().alpha(0.0f);
    }

    @Override
    public void showCameraTriggerButton() {
        mButtonCameraTrigger.animate().alpha(1.0f);
        mButtonCameraTrigger.setEnabled(true);
    }

    @Override
    public void hideCameraTriggerButton() {
        mButtonCameraTrigger.animate().alpha(0.0f);
        mButtonCameraTrigger.setEnabled(false);
    }

    private void showNoPermissionView() {
        hideCameraPreview();
        hideCameraTriggerButton();
        hideDocumentCornerGuides();
        mStubNoPermission.inflate();
        handleNoPermissionButtonClick();
    }

    private void hideCameraPreview() {
        mCameraPreview.animate().alpha(0.0f);
        mCameraPreview.setEnabled(false);
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
                // TODO: application id from apk manifest
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", "TODO: application id from apk manifest", null);
                intent.setData(uri);
                mFragment.startActivity(intent);
            }
        });
    }

    private byte[] loadTestDocument() {
        if (mFragment.getActivity() == null) {
            return null;
        }
        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = mFragment.getActivity().getAssets().open("gv_test_document.jpg");
            outputStream = new ByteArrayOutputStream();
            copyStream(inputStream, outputStream);
        } catch (IOException e) {
            // Ignore
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
        if (outputStream == null) {
            return null;
        }
        return outputStream.toByteArray();
    }

    private OutputStream copyStream(InputStream is, OutputStream os) throws IOException {
        int bufferLength = 8192;
        byte[] buffer = new byte[bufferLength];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        return os;
    }

    @VisibleForTesting
    static class CameraListener implements CameraInterface.Listener {

        public interface Callback {
            void onCameraNoPermissionError();
            void onCameraError();
        }

        private CameraFragmentListener mFragmentListener;
        private final Callback mCallback;

        CameraListener(CameraFragmentListener fragmentListener, Callback callback) {
            mFragmentListener = fragmentListener;
            mCallback = callback;
        }

        public void setFragmentListener(CameraFragmentListener fragmentListener) {
            mFragmentListener = fragmentListener;
        }

        @Override
        public void onCameraOpened() {
        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onCameraFocusFinished(boolean success) {

        }

        @Override
        public void onPhotoTaken(Photo photo) {

        }

        @Override
        public void onCameraError(RuntimeException e) {
            handleCameraException(e);
            mCallback.onCameraError();
        }

        private void handleCameraException(RuntimeException e) {
            GiniVisionError error = cameraExceptionToGiniVisionError(e);
            if (error.getErrorCode() == GiniVisionError.ErrorCode.CAMERA_NO_ACCESS) {
                mCallback.onCameraNoPermissionError();
            } else {
                mFragmentListener.onError(cameraExceptionToGiniVisionError(e));
            }
        }
    }
}
