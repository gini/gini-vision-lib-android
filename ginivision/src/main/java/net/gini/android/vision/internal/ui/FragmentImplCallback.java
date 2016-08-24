package net.gini.android.vision.internal.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * @exclude
 */
public interface FragmentImplCallback {

    @Nullable
    Activity getActivity();

    @Nullable
    View getView();

    void startActivity(Intent intent);
}
