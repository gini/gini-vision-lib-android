package net.gini.android.vision.camera;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.photo.Photo;

class CameraFragmentImpl implements CameraFragmentInterface {

    private static final CameraFragmentListener NO_OP_LISTENER = new CameraFragmentListener() {
        @Override
        public void onDocumentAvailable(Document document) {
        }

        @Override
        public void onError(GiniVisionError error) {
        }
    };

    private CameraFragmentListener mListener = NO_OP_LISTENER;

    private ImageButton mButtonCameraTrigger;

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
                // TODO: return real photo when ready
                mListener.onDocumentAvailable(Document.fromPhoto(Photo.fromJpeg(new byte[1024 * 1024 * 10], 0)));
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
}
