package net.gini.android.vision.internal.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 17.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public final class NullabilityHelper {

    public static <K, V> Map<K, V> getMapOrEmpty(final Map<K, V> collection) {
        if (collection == null) {
            return Collections.emptyMap();
        }
        return collection;
    }

    public static <T> List<T> getListOrEmpty(final List<T> collection) {
        if (collection == null) {
            return Collections.emptyList();
        }
        return collection;
    }

    private NullabilityHelper() {
    }
}
