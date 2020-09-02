package net.gini.android.vision.network.model;

import net.gini.android.models.CompoundExtraction;
import net.gini.android.models.SpecificExtraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 17.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */
public final class CompoundExtractionsMapper {

    @NonNull
    public static Map<String, GiniVisionCompoundExtraction> mapToGVL(@NonNull final Map<String, CompoundExtraction> sourceMap) {
        final Map<String, GiniVisionCompoundExtraction> targetMap = new HashMap<>(sourceMap.size());
        for (final Map.Entry<String, CompoundExtraction> source : sourceMap.entrySet()) {
            targetMap.put(source.getKey(), toGVL(source.getValue()));
        }
        return targetMap;
    }

    private static GiniVisionCompoundExtraction toGVL(@NonNull final CompoundExtraction source) {
        return new GiniVisionCompoundExtraction(source.getName(), mapListToGVL(source.getSpecificExtractionMaps()));
    }

    @NonNull
    private static List<Map<String, GiniVisionSpecificExtraction>> mapListToGVL(final List<Map<String, SpecificExtraction>> sourceList) {
        final List<Map<String, GiniVisionSpecificExtraction>> targetList = new ArrayList<>(sourceList.size());
        for (final Map<String, SpecificExtraction> sourceMap : sourceList) {
            targetList.add(SpecificExtractionMapper.mapToGVL(sourceMap));
        }
        return targetList;
    }

    @NonNull
    public static Map<String, CompoundExtraction> mapToApiSdk(@NonNull final Map<String, GiniVisionCompoundExtraction> sourceMap) {
        final Map<String, CompoundExtraction> targetMap = new HashMap<>(sourceMap.size());
        for (final Map.Entry<String, GiniVisionCompoundExtraction> source : sourceMap.entrySet()) {
            targetMap.put(source.getKey(), toApiSdk(source.getValue()));
        }
        return targetMap;
    }

    private static CompoundExtraction toApiSdk(@NonNull final GiniVisionCompoundExtraction source) {
        return new CompoundExtraction(source.getName(), mapListToApiSdk(source.getSpecificExtractionMaps()));
    }

    @NonNull
    private static List<Map<String, SpecificExtraction>> mapListToApiSdk(final List<Map<String, GiniVisionSpecificExtraction>> sourceList) {
        final List<Map<String, SpecificExtraction>> targetList = new ArrayList<>(sourceList.size());
        for (final Map<String, GiniVisionSpecificExtraction> sourceMap : sourceList) {
            targetList.add(SpecificExtractionMapper.mapToApiSdk(sourceMap));
        }
        return targetList;
    }

    private CompoundExtractionsMapper() {
    }
}
