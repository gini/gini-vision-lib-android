package net.gini.android.vision.scanner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScannerFragmentCompat extends Fragment {

    private final ScannerFragmentImpl mFragmentImpl = new ScannerFragmentImpl();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ScannerFragmentHelper.setListener(mFragmentImpl, context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }
}
