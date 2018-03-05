package net.gini.android.vision.internal.util;

import android.support.annotation.NonNull;

import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionFeatureConfiguration;

/**
 * Created by Alpar Szotyori on 05.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * @exclude
 */
public final class FeatureConfiguration {

    public static DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return GiniVision.hasInstance() ?
                GiniVision.getInstance().getDocumentImportEnabledFileTypes() :
                giniVisionFeatureConfiguration.getDocumentImportEnabledFileTypes();
    }

    public static boolean isFileImportEnabled(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return GiniVision.hasInstance() ?
                GiniVision.getInstance().isFileImportEnabled() :
                giniVisionFeatureConfiguration.isFileImportEnabled();
    }

    public static boolean isQRCodeScanningEnabled(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return GiniVision.hasInstance() ?
                GiniVision.getInstance().isQRCodeScanningEnabled() :
                giniVisionFeatureConfiguration.isQRCodeScanningEnabled();
    }

    private FeatureConfiguration() {
    }
}
