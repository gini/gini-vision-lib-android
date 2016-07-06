package net.gini.android.vision.camera;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
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

    private ImageButton mButtonCameraTrigger;

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
        return view;
    }

    private void bindViews(View view) {
        mButtonCameraTrigger = (ImageButton) view.findViewById(R.id.gv_button_camera_trigger);
    }

    private void setInputHandlers() {
        mButtonCameraTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] testDocument = loadTestDocument();
                if (testDocument != null) {
                    mListener.onDocumentAvailable(Document.fromPhoto(Photo.fromJpeg(testDocument, 0)));
                } else {
                    mListener.onError(new GiniVisionError(GiniVisionError.ErrorCode.CAMERA, "Could not load the test document jpeg."));
                }
            }
        });
    }

    @Override
    public void showDocumentCornerGuides() {

    }

    @Override
    public void hideDocumentCornerGuides() {

    }

    @Override
    public void showCameraTriggerButton() {

    }

    @Override
    public void hideCameraTriggerButton() {

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
}
