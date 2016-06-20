package net.gini.android.vision.reviewphoto;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.gini.android.vision.R;
import net.gini.android.vision.scanner.photo.Photo;

public class ReviewPhotoFragmentImpl {

    private static final ReviewPhotoFragmentListener NO_OP_LISTENER = new ReviewPhotoFragmentListener() {
        @Override
        public void onShouldAnalyzePhoto(Photo photo) {
        }

        @Override
        public void onProceedToAnalyzePhotoScreen(Photo photo) {
        }

        @Override
        public void onPhotoReviewedAndAnalyzed(Photo photo) {
        }
    };

    private ImageButton mButtonRotate;
    private ImageButton mButtonNext;

    private ReviewPhotoFragmentListener mListener = NO_OP_LISTENER;
    private boolean mPhotoWasAnalyzed = false;
    private boolean mPhotoWasModified = false;

    public void setListener(ReviewPhotoFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public void setPhotoWasAnalyzed(boolean photoWasAnalyzed) {
        mPhotoWasAnalyzed = photoWasAnalyzed;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        mListener.onShouldAnalyzePhoto(Photo.fromJpeg(new byte[]{}, 0));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_review_photo, container, false);
        bindViews(view);
        setInputHandlers();
        return view;
    }

    private void bindViews(View view) {
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

    private void onRotateClicked() {
        mPhotoWasModified = true;
    }

    private void onNextClicked() {
        if (!mPhotoWasModified) {
            if (!mPhotoWasAnalyzed) {
                // TODO: can go on to the analyze screen
                mListener.onProceedToAnalyzePhotoScreen(Photo.fromJpeg(new byte[]{}, 0));
            } else {
                // TODO: photo was not modified and already analyzed, client should show extraction results
                mListener.onPhotoReviewedAndAnalyzed(Photo.fromJpeg(new byte[]{}, 0));
            }
        } else {
            // TODO: can go on to the analyze screen
            mListener.onProceedToAnalyzePhotoScreen(Photo.fromJpeg(new byte[]{}, 0));
        }
    }
}
