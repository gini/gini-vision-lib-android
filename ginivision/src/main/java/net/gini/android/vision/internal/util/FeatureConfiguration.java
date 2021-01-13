package net.gini.android.vision.internal.util;

import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionFeatureConfiguration;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 05.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public final class FeatureConfiguration {

    public static DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return GiniVision.hasInstance()
                ? GiniVision.getInstance().getDocumentImportEnabledFileTypes()
                : giniVisionFeatureConfiguration.getDocumentImportEnabledFileTypes();
    }

    public static boolean isFileImportEnabled(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return GiniVision.hasInstance()
                ? GiniVision.getInstance().isFileImportEnabled()
                : giniVisionFeatureConfiguration.isFileImportEnabled();
    }

    public static boolean isQRCodeScanningEnabled(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return GiniVision.hasInstance()
                ? GiniVision.getInstance().isQRCodeScanningEnabled()
                : giniVisionFeatureConfiguration.isQRCodeScanningEnabled();
    }

    public static boolean shouldShowOnboardingAtFirstRun(
            final boolean showOnboardingAtFirstRun) {
        return GiniVision.hasInstance()
                ? GiniVision.getInstance().shouldShowOnboardingAtFirstRun()
                : showOnboardingAtFirstRun;
    }

    public static boolean shouldShowOnboarding(
            final boolean showOnboarding) {
        return GiniVision.hasInstance()
                ? GiniVision.getInstance().shouldShowOnboarding()
                : showOnboarding;
    }

    public static boolean isMultiPageEnabled() {
        return GiniVision.hasInstance() && GiniVision.getInstance().isMultiPageEnabled();
    }

    private FeatureConfiguration() {
    }
}
