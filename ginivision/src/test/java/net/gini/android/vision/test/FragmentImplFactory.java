package net.gini.android.vision.test;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public interface FragmentImplFactory<I, F> {
    @NonNull
    I createFragmentImpl(@NonNull final F fragment);
}
