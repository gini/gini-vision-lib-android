package net.gini.android.vision;

import android.app.Application;
import android.support.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 08.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public abstract class GiniVisionBasePresenter<V extends GiniVisionBaseView> {

    private final Application mApp;
    private final V mView;

    protected GiniVisionBasePresenter(@NonNull final Application app, @NonNull final V view) {
        mApp = app;
        mView = view;
    }

    @NonNull
    public Application getApp() {
        return mApp;
    }

    @NonNull
    protected V getView() {
        return mView;
    }

    public abstract void start();

    public abstract void stop();
}
