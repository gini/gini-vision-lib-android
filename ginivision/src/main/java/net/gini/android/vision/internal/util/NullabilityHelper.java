package net.gini.android.vision.internal.util;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 17.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 *
 * @exclude
 */
public final class NullabilityHelper {

    public static <K, V> Map<K, V> getMapOrEmpty(final Map<K, V> collection) {
        if (collection == null) {
            return Collections.emptyMap();
        }
        return collection;
    }

    private NullabilityHelper() {
    }
}
