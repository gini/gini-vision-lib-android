package net.gini.android.vision;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 08.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
@SuppressWarnings("PMD.AbstractClassWithoutAbstractMethod")
public interface GiniVisionBaseView<P extends GiniVisionBasePresenter> {

    void setPresenter(@NonNull final P presenter);
}
