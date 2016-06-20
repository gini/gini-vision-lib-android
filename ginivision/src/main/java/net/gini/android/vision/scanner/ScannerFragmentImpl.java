package net.gini.android.vision.scanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.gini.android.vision.R;
import net.gini.android.vision.scanner.photo.Photo;

class ScannerFragmentImpl {

    private static final ScannerFragmentListener NO_OP_LISTENER = new ScannerFragmentListener() {
        @Override
        public void onPhotoTaken(Photo photo) {
        }
    };

    private ScannerFragmentListener mListener = NO_OP_LISTENER;

    private ImageButton mButtonCameraTrigger;

    public void setListener(ScannerFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_scanner, container, false);
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
                mListener.onPhotoTaken(Photo.fromJpeg(new byte[]{1, 2}, 0));
            }
        });
    }
}
