package net.gini.android.vision.camera;

import net.gini.android.vision.test.FragmentHostActivity;


/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class CameraFragmentHostActivityNotListener extends
        FragmentHostActivity<CameraFragmentCompatFake> {

    static CameraFragmentListener sListener;

    @Override
    protected void setListener() {
        if (sListener != null) {
            getFragment().setListener(sListener);
        }
    }

    @Override
    protected CameraFragmentCompatFake createFragment() {
        return CameraFragmentCompatFake.createInstance();
    }

}
