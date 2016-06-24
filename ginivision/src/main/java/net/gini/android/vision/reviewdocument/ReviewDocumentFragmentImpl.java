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

class ReviewDocumentFragmentImpl implements ReviewDocumentFragmentInterface {

    private static final ReviewDocumentFragmentListener NO_OP_LISTENER = new ReviewDocumentFragmentListener() {
        @Override
        public void onShouldAnalyseDocument(Document document) {

        }

        @Override
        public void onProceedToAnalyseScreen(Document document) {

        }

        @Override
        public void onDocumentReviewedAndAnalysed(Document document) {

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
    private boolean mPhotoWasAnalysed = false;
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

    public void onDocumentAnalysed() {
        mPhotoWasAnalysed = true;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        mListener.onShouldAnalyseDocument(mDocument);
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
            if (!mPhotoWasAnalysed) {
                // TODO: can go on to the analyse screen
                mListener.onProceedToAnalyseScreen(mDocument);
            } else {
                // TODO: photo was not modified and already analysed, client should show extraction results
                mListener.onDocumentReviewedAndAnalysed(mDocument);
            }
        } else {
            // TODO: can go on to the analyse screen
            mListener.onProceedToAnalyseScreen(mDocument);
        }
    }
}
