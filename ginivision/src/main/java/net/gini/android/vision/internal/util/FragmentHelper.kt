package net.gini.android.vision.internal.util

import androidx.fragment.app.Fragment

/**
 * Created by Alpar Szotyori on 14.10.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

internal fun Fragment.parentFragmentManagerOrNull() = if (isAdded) { parentFragmentManager } else { null }