package net.gini.android.vision.noresults;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

public class NoResultsFragmentCompat extends Fragment implements FragmentImplCallback {

    private NoResultsFragmentImpl mFragmentImpl;

    public NoResultsFragmentCompat() {
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

    public static NoResultsFragmentCompat createInstance() {
        NoResultsFragmentCompat fragment = new NoResultsFragmentCompat();
        return fragment;
    }

}
