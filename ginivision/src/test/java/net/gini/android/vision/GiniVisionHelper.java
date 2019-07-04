package net.gini.android.vision;

import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 13.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class GiniVisionHelper {

    public static void setGiniVisionInstance(@Nullable final GiniVision giniVision) {
        GiniVision.setInstance(giniVision);
    }

}
