package net.gini.android.vision.noresults;


import static android.view.View.GONE;

import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class NoResultsFragmentImpl {

    private static final NoResultsFragmentListener NO_OP_LISTENER =
            new NoResultsFragmentListener() {
                @Override
                public void onBackToCameraPressed() {
                }
            };

    private final FragmentImplCallback mFragment;
    private final Document mDocument;
    private NoResultsFragmentListener mListener;

    NoResultsFragmentImpl(@NonNull final FragmentImplCallback fragment,
            @NonNull final Document document) {
        mFragment = fragment;
        mDocument = document;
    }

    void setListener(@Nullable final NoResultsFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    void onCreate(final Bundle savedInstanceState) {
        forcePortraitOrientationOnPhones(mFragment.getActivity());
    }

    View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_noresults, container, false);
        final View backButton = view.findViewById(R.id.gv_button_no_results_back);
        if (isDocumentFromCameraScreen()) {
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mListener.onBackToCameraPressed();
                }
            });
        } else {
            backButton.setVisibility(GONE);
        }
        return view;
    }

    private boolean isDocumentFromCameraScreen() {
        final Intent intent = mDocument.getIntent();
        return intent == null || intent.getAction() == null;
    }

}
