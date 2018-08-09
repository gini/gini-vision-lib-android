package net.gini.android.vision.internal.util;

import static com.google.common.truth.Truth.assertThat;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;

import net.gini.android.vision.test.Helpers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Alpar Szotyori on 09.08.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class FileImportValidatorTest {

    private static final String PDF = "invoice.pdf";
    private static final String PDF_WITH_PASSWORD = "invoice-password.pdf";

    private static Uri sPdfContentUri;
    private static Uri sPdfWithPasswordContentUri;

    @BeforeClass
    public static void setUpClass() throws Exception {
        sPdfContentUri = Helpers.getAssetFileFileContentUri(PDF);
        sPdfWithPasswordContentUri = Helpers.getAssetFileFileContentUri(PDF_WITH_PASSWORD);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Helpers.deleteAssetFileFromContentUri(PDF);
        Helpers.deleteAssetFileFromContentUri(PDF_WITH_PASSWORD);
    }

    @Test
    public void should_acceptPDF_withOnePage_andWithoutPassword() throws Exception {
        // Given
        final FileImportValidator fileImportValidator = new FileImportValidator(
                InstrumentationRegistry.getTargetContext());
        // When
        final boolean result = fileImportValidator.matchesCriteria(sPdfContentUri);
        // Then
        assertThat(result).isTrue();
    }

    @Test
    public void should_NotAcceptPDF_withOnePage_andWithPassword() throws Exception {
        // Given
        final FileImportValidator fileImportValidator = new FileImportValidator(
                InstrumentationRegistry.getTargetContext());
        // When
        final boolean result = fileImportValidator.matchesCriteria(sPdfWithPasswordContentUri);
        // Then
        assertThat(result).isFalse();
        assertThat(fileImportValidator.getError()).isEqualTo(
                FileImportValidator.Error.PASSWORD_PROTECTED_PDF);
    }
}