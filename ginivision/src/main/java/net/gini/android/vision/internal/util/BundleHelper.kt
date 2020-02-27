package net.gini.android.vision.internal.util

import android.os.Bundle
import android.os.Parcelable
import java.util.*

/**
 * Created by Alpar Szotyori on 17.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

fun <V : Parcelable> fromMap(map: Map<String, V>): Bundle = Bundle().apply {
    for ((key, value) in map) {
        putParcelable(key, value)
    }
}

fun <V : Parcelable> fromMapList(mapList: List<Map<String, V>>): List<Bundle> = mapList.map { fromMap(it) }

@JvmOverloads
fun <V : Parcelable> toMap(bundle: Bundle, classLoader: ClassLoader? = null): Map<String, V> = HashMap<String, V>().apply {
    if (classLoader != null) {
        bundle.classLoader = classLoader
    }
    for (key in bundle.keySet()) {
        bundle.getParcelable<V>(key)?.let { parcelable ->
            put(key, parcelable)
        }
    }
}

@JvmOverloads
fun <V : Parcelable> toMapList(bundleList: List<Bundle>,
                               classLoader: ClassLoader? = null): List<Map<String, V>> = bundleList.map { toMap<V>(it, classLoader) }