package net.gini.android.vision.network.model;

/**
 * Created by Alpar Szotyori on 14.09.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

import net.gini.android.models.ReturnReason;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Helper class to map the {@link net.gini.android.models.ReturnReason} from the Gini API SDK to the Gini Vision
 * Library's {@link GiniVisionReturnReason} and vice versa.
 */
public class ReturnReasonsMapper {

    public static List<ReturnReason> mapToApiSdk(@NonNull final List<GiniVisionReturnReason> sourceList) {
        final List<ReturnReason> targetList = new ArrayList<>(sourceList.size());
        for (final GiniVisionReturnReason source : sourceList) {
            targetList.add(new ReturnReason(source.getId(), source.getLocalizedLabels()));
        }
        return targetList;
    }

    public static List<GiniVisionReturnReason> mapToGVL(@NonNull final List<ReturnReason> sourceList) {
        final List<GiniVisionReturnReason> targetList = new ArrayList<>(sourceList.size());
        for (final ReturnReason source : sourceList) {
            targetList.add(new GiniVisionReturnReason(source.getId(), source.getLocalizedLabels()));
        }
        return targetList;
    }
}
