package net.gini.android.vision.accounting.network.model;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Helper class to map the {@link SpecificExtraction} from the Gini API SDK to the Gini Vision
 * Library's {@link GiniVisionSpecificExtraction} and vice versa.
 */
public final class SpecificExtractionMapper {

    /**
     * Convert a map of {@link SpecificExtraction}s from the Gini API SDK to a map of Gini Vision
     * Library {@link GiniVisionSpecificExtraction}s.
     *
     * @param sourceMap map of Gini API SDK {@link SpecificExtraction}s
     *
     * @return map of Gini Vision Library {@link GiniVisionSpecificExtraction}s
     */
    @NonNull
    public static Map<String, GiniVisionSpecificExtraction> mapToGVL(
            @NonNull final Map<String, SpecificExtraction> sourceMap) {
        final Map<String, GiniVisionSpecificExtraction> targetMap = new HashMap<>(sourceMap.size());
        for (final Map.Entry<String, SpecificExtraction> source : sourceMap.entrySet()) {
            targetMap.put(source.getKey(), map(source.getValue()));
        }
        return targetMap;
    }

    /**
     * Map a {@link SpecificExtraction} from the Gini API SDK to the Gini Vision Library's {@link
     * GiniVisionSpecificExtraction}.
     *
     * @param source Gini API SDK {@link SpecificExtraction}
     *
     * @return a Gini Vision Library {@link GiniVisionSpecificExtraction}
     */
    @NonNull
    public static GiniVisionSpecificExtraction map(
            @NonNull final SpecificExtraction source) {
        return new GiniVisionSpecificExtraction(source.getName(), source.getValue(),
                source.getEntity(),
                BoxMapper.map(source.getBox()),
                ExtractionMapper.mapListToGVL(source.getCandidate()));
    }

    /**
     * Convert a map of {@link GiniVisionSpecificExtraction}s from the Gini Vision Library to a map
     * of Gini API SDK {@link SpecificExtraction}s.
     *
     * @param sourceMap map of Gini Vision Library {@link GiniVisionSpecificExtraction}s
     *
     * @return map of Gini API SDK {@link SpecificExtraction}s
     */
    @NonNull
    public static Map<String, SpecificExtraction> mapToApiSdk(
            @NonNull final Map<String, GiniVisionSpecificExtraction> sourceMap) {
        final Map<String, SpecificExtraction> targetMap = new HashMap<>(sourceMap.size());
        for (final Map.Entry<String, GiniVisionSpecificExtraction> source : sourceMap.entrySet()) {
            targetMap.put(source.getKey(), map(source.getValue()));
        }
        return targetMap;
    }

    /**
     * Map a {@link GiniVisionSpecificExtraction} from the Gini Vision Library to the Gini API SDK's
     * {@link SpecificExtraction}.
     *
     * @param source Gini Vision Library {@link GiniVisionSpecificExtraction}
     *
     * @return Gini API SDK {@link SpecificExtraction}
     */
    @NonNull
    public static SpecificExtraction map(
            @NonNull final GiniVisionSpecificExtraction source) {
        return new SpecificExtraction(source.getName(), source.getValue(), source.getEntity(),
                BoxMapper.map(source.getBox()),
                ExtractionMapper.mapListToApiSdk(source.getCandidates()));
    }

    private SpecificExtractionMapper() {
    }
}
