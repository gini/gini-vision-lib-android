package net.gini.android.vision.analysis;

import android.support.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class AnalysisFragmentCompatFake extends AnalysisFragmentCompat {

    public static FragmentImplFactory sFragmentImplFactory;

    public AnalysisFragmentCompatFake() {
    }

    @Override
    AnalysisFragmentImpl createFragmentImpl() {
        return sFragmentImplFactory.createFragmentImpl(this);
    }

    public interface FragmentImplFactory {
        @NonNull
        AnalysisFragmentImpl createFragmentImpl(@NonNull final AnalysisFragmentCompat fragment);
    }
}
