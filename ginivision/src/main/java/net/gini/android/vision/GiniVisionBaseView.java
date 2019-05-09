package net.gini.android.vision;

import android.support.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 08.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public abstract class GiniVisionBaseView<P extends GiniVisionBasePresenter> {

    private P mPresenter;

    public void setPresenter(@NonNull final P presenter) {
        mPresenter = presenter;
    }

    @NonNull
    protected P getPresenter() {
        return mPresenter;
    }
}
