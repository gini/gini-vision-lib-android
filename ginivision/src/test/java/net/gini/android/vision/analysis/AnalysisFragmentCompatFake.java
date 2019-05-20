package net.gini.android.vision.analysis;

import net.gini.android.vision.test.FragmentImplFactory;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class AnalysisFragmentCompatFake extends AnalysisFragmentCompat {

    public static FragmentImplFactory<AnalysisFragmentImpl, AnalysisFragmentCompat>
            sFragmentImplFactory;

    public AnalysisFragmentCompatFake() {
    }

    @Override
    AnalysisFragmentImpl createFragmentImpl() {
        return sFragmentImplFactory.createFragmentImpl(this);
    }
}
