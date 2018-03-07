package net.gini.android.vision.network.model;

import android.support.annotation.NonNull;

import net.gini.android.models.Extraction;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public final class ExtractionMapper {

    @NonNull
    public static List<GiniVisionExtraction> mapToGVL(@NonNull final List<Extraction> sourceList) {
        final List<GiniVisionExtraction> targetList = new ArrayList<>(sourceList.size());
        for (final net.gini.android.models.Extraction source : sourceList) {
            targetList.add(map(source));
        }
        return targetList;
    }

    @NonNull
    public static GiniVisionExtraction map(@NonNull final net.gini.android.models.Extraction source) {
        final GiniVisionExtraction giniVisionExtraction = new GiniVisionExtraction(
                source.getValue(), source.getEntity(),
                BoxMapper.map(source.getBox()));
        giniVisionExtraction.setIsDirty(source.isDirty());
        return giniVisionExtraction;
    }

    @NonNull
    public static List<Extraction> mapToApiSdk(@NonNull final List<GiniVisionExtraction> sourceList) {
        final List<Extraction> targetList = new ArrayList<>(sourceList.size());
        for (final GiniVisionExtraction source : sourceList) {
            targetList.add(map(source));
        }
        return targetList;
    }

    @NonNull
    public static Extraction map(@NonNull final GiniVisionExtraction source) {
        final Extraction extraction = new Extraction(source.getValue(), source.getEntity(),
                BoxMapper.map(source.getBox()));
        extraction.setIsDirty(source.isDirty());
        return extraction;
    }

    private ExtractionMapper() {
    }
}
