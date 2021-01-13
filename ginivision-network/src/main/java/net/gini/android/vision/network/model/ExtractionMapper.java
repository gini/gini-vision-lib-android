package net.gini.android.vision.network.model;

import net.gini.android.models.Extraction;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Helper class to map the {@link Extraction} from the Gini API SDK to the Gini Vision Library's
 * {@link GiniVisionExtraction} and vice versa.
 */
public final class ExtractionMapper {

    /**
     * Map a list of {@link Extraction}s from the Gini API SDK to a list of Gini Vision Library
     * {@link GiniVisionExtraction}s.
     *
     * @param sourceList list of Gini API SDK {@link Extraction}s
     *
     * @return list of Gini Vision Library {@link GiniVisionExtraction}s
     */
    @NonNull
    public static List<GiniVisionExtraction> mapListToGVL(
            @NonNull final List<Extraction> sourceList) {
        final List<GiniVisionExtraction> targetList = new ArrayList<>(sourceList.size());
        for (final net.gini.android.models.Extraction source : sourceList) {
            targetList.add(map(source));
        }
        return targetList;
    }

    /**
     * Map an {@link Extraction} from the Gini API SDK to the Gini Vision Library's {@link
     * GiniVisionExtraction}.
     *
     * @param source Gini API SDK {@link Extraction}
     *
     * @return a Gini Vision Library {@link GiniVisionExtraction}
     */
    @NonNull
    public static GiniVisionExtraction map(
            @NonNull final net.gini.android.models.Extraction source) {
        final GiniVisionExtraction giniVisionExtraction = new GiniVisionExtraction(
                source.getValue(), source.getEntity(),
                BoxMapper.map(source.getBox()));
        giniVisionExtraction.setIsDirty(source.isDirty());
        return giniVisionExtraction;
    }

    /**
     * Map a list of {@link GiniVisionExtraction}s from the Gini Vision Library to a list of Gini
     * API SDK {@link Extraction}s.
     *
     * @param sourceList list of Gini Vision Library {@link GiniVisionExtraction}s
     *
     * @return list of Gini API SDK {@link Extraction}s
     */
    @NonNull
    public static List<Extraction> mapListToApiSdk(
            @NonNull final List<GiniVisionExtraction> sourceList) {
        final List<Extraction> targetList = new ArrayList<>(sourceList.size());
        for (final GiniVisionExtraction source : sourceList) {
            targetList.add(map(source));
        }
        return targetList;
    }

    /**
     * Map a {@link GiniVisionExtraction} from the Gini Vision Library to the Gini API SDK's {@link
     * Extraction}.
     *
     * @param source Gini Vision Library {@link GiniVisionExtraction}
     *
     * @return Gini API SDK {@link Extraction}
     */
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
