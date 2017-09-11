package net.gini.android.vision.noresults;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

public class NoResultsFragmentStandard extends Fragment implements FragmentImplCallback {

    private NoResultsFragmentImpl mFragmentImpl;

    public NoResultsFragmentStandard() {
        mFragmentImpl = new NoResultsFragmentImpl(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentImpl.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            final Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    public static NoResultsFragmentStandard createInstance() {
        NoResultsFragmentStandard fragment = new NoResultsFragmentStandard();
        return fragment;
    }

}
