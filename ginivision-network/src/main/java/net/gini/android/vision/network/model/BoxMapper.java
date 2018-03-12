package net.gini.android.vision.network.model;

import android.support.annotation.Nullable;

import net.gini.android.models.Box;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public final class BoxMapper {

    @Nullable
    public static GiniVisionBox map(@Nullable final Box source) {
        if (source == null) {
            return null;
        }
        return new GiniVisionBox(source.getPageNumber(), source.getLeft(), source.getTop(),
                source.getWidth(), source.getHeight());
    }

    @Nullable
    public static Box map(@Nullable final GiniVisionBox source) {
        if (source == null) {
            return null;
        }
        return new Box(source.getPageNumber(), source.getLeft(), source.getTop(), source.getWidth(),
                source.getHeight());
    }

    private BoxMapper() {
    }
}
