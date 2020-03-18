package net.gini.android.vision.accounting.network.model;

import net.gini.android.models.Box;
import net.gini.android.vision.network.model.GiniVisionBox;

import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Helper class to map the {@link Box} from the Gini API SDK to the Gini Vision Library's {@link
 * GiniVisionBox} and vice versa.
 */
public final class BoxMapper {

    /**
     * Map a {@link Box} from the Gini API SDK to the Gini Vision Library's {@link GiniVisionBox}.
     *
     * @param source Gini API SDK {@link Box}
     *
     * @return a Gini Vision Library {@link GiniVisionBox}
     */
    @Nullable
    public static GiniVisionBox map(@Nullable final Box source) {
        if (source == null) {
            return null;
        }
        return new GiniVisionBox(source.getPageNumber(), source.getLeft(), source.getTop(),
                source.getWidth(), source.getHeight());
    }

    /**
     * Map a {@link GiniVisionBox} from the Gini Vision Library to the Gini API SDK's {@link Box}.
     *
     * @param source Gini Vision Library {@link GiniVisionBox}
     *
     * @return Gini API SDK {@link Box}
     */
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
