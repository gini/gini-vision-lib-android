package net.gini.android.vision.analysis;

import net.gini.android.vision.test.FragmentHostActivity;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class AnalysisFragmentHostActivity extends FragmentHostActivity<AnalysisFragmentCompatFake> {
    @Override
    protected void setListener() {

    }

    @Override
    protected AnalysisFragmentCompatFake createFragment() {
        return new AnalysisFragmentCompatFake();
    }
}
