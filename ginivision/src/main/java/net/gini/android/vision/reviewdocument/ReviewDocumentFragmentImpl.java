package net.gini.android.vision.reviewdocument;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

public class ReviewDocumentFragmentImpl implements ReviewDocumentFragmentInterface {

    private static final ReviewDocumentFragmentListener NO_OP_LISTENER = new ReviewDocumentFragmentListener() {
        @Override
        public void onShouldAnalyzeDocument(Document document) {

        }

        @Override
        public void onProceedToAnalyzeScreen(Document document) {

        }

        @Override
        public void onDocumentReviewedAndAnalyzed(Document document) {

        }

        @Override
        public void onError(GiniVisionError error) {
        }
    };

    private ImageButton mButtonRotate;
    private ImageButton mButtonNext;

    private final FragmentImplCallback mFragment;
    private Document mDocument;
    private ReviewDocumentFragmentListener mListener = NO_OP_LISTENER;
    private boolean mPhotoWasAnalyzed = false;
    private boolean mPhotoWasModified = false;

    public ReviewDocumentFragmentImpl(FragmentImplCallback fragment, Document document) {
        mFragment = fragment;
        mDocument = document;
    }

    public void setListener(ReviewDocumentFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public void onDocumentAnalyzed() {
        mPhotoWasAnalyzed = true;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        mListener.onShouldAnalyzeDocument(mDocument);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_review_document, container, false);
        bindViews(view);
        setInputHandlers();
        return view;
    }

    public void onDestroy() {
        mDocument = null;
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
                mListener.onProceedToAnalyzeScreen(mDocument);
            } else {
                // TODO: photo was not modified and already analyzed, client should show extraction results
                mListener.onDocumentReviewedAndAnalyzed(mDocument);
            }
        } else {
            // TODO: can go on to the analyze screen
            mListener.onProceedToAnalyzeScreen(mDocument);
        }
    }
}
