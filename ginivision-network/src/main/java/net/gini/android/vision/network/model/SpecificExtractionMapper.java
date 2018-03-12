package net.gini.android.vision.network.model;

import android.support.annotation.NonNull;

import net.gini.android.models.SpecificExtraction;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public final class SpecificExtractionMapper {

    @NonNull
    public static Map<String, GiniVisionSpecificExtraction> mapToGVL(
            @NonNull final Map<String, SpecificExtraction> sourceMap) {
        final Map<String, GiniVisionSpecificExtraction> targetMap = new HashMap<>(sourceMap.size());
        for (final Map.Entry<String, SpecificExtraction> source : sourceMap.entrySet()) {
            targetMap.put(source.getKey(), map(source.getValue()));
        }
        return targetMap;
    }

    @NonNull
    public static GiniVisionSpecificExtraction map(
            @NonNull final SpecificExtraction source) {
        return new GiniVisionSpecificExtraction(source.getName(), source.getValue(),
                source.getEntity(),
                BoxMapper.map(source.getBox()), ExtractionMapper.mapToGVL(source.getCandidate()));
    }

    @NonNull
    public static Map<String, SpecificExtraction> mapToApiSdk(
            @NonNull final Map<String, GiniVisionSpecificExtraction> sourceMap) {
        final Map<String, SpecificExtraction> targetMap = new HashMap<>(sourceMap.size());
        for (final Map.Entry<String, GiniVisionSpecificExtraction> source : sourceMap.entrySet()) {
            targetMap.put(source.getKey(), map(source.getValue()));
        }
        return targetMap;
    }

    @NonNull
    public static SpecificExtraction map(
            @NonNull final GiniVisionSpecificExtraction source) {
        return new SpecificExtraction(source.getName(), source.getValue(), source.getEntity(),
                BoxMapper.map(source.getBox()),
                ExtractionMapper.mapToApiSdk(source.getCandidate()));
    }

    private SpecificExtractionMapper() {
    }
}
