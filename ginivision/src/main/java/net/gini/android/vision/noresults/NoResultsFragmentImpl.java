package net.gini.android.vision.noresults;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.R;

class NoResultsFragmentImpl {

    private NoResultsFragmentListener mListener;

    public void onCreate(final Bundle savedInstanceState) {

    }

    void onAttach(Context context) {
        try {
            mListener = (NoResultsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    context.toString() + " must implement NoResultsFragmentListener");
        }
    }

    View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_noresults, container, false);
        View backButton = view.findViewById(R.id.gv_button_no_results_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mListener.onBackToCameraPressed();
            }
        });
        return view;
    }

}
