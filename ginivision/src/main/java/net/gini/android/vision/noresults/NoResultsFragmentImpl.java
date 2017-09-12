package net.gini.android.vision.noresults;


import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

class NoResultsFragmentImpl {

    private final FragmentImplCallback mFragment;
    private NoResultsFragmentListener mListener;

    NoResultsFragmentImpl(final FragmentImplCallback fragment) {
        mFragment = fragment;
    }

    public void onCreate(final Bundle savedInstanceState) {
        forcePortraitOrientationOnPhones(mFragment.getActivity());
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
