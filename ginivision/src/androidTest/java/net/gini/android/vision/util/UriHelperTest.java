package net.gini.android.vision.util;

import static com.google.common.truth.Truth.assertThat;

import android.net.Uri;

import net.gini.android.vision.test.Helpers;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Created by Alpar Szotyori on 28.11.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

@RunWith(AndroidJUnit4.class)
public class UriHelperTest {

    private static final String TEST_FILE = "invoice.jpg";
    private static Uri sContentUri;

    @BeforeClass
    public static void setUpClass() throws Exception {
        sContentUri = Helpers.getAssetFileFileContentUri(TEST_FILE);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        Helpers.deleteAssetFileFromContentUri(TEST_FILE);
    }

    @Test
    public void should_getFileSize_forContentUri() {
        // Given
        final int expectedSize = getTestFileSize();
        // When
        final int size = UriHelper.getFileSizeFromUri(sContentUri,
                ApplicationProvider.getApplicationContext());
        // Then
        assertThat(size).isEqualTo(expectedSize);
    }

    private int getTestFileSize() {
        return (int) Helpers.getAssetFileAsFileProviderFile(TEST_FILE).length();
    }

    @Test
    public void should_getFileSize_forFileUri() {
        // Given
        final Uri fileUri = getTestFileFileUri();
        final int expectedSize = getTestFileSize();
        // When
        final int size = UriHelper.getFileSizeFromUri(fileUri,
                ApplicationProvider.getApplicationContext());
        // Then
        assertThat(size).isEqualTo(expectedSize);
    }

    private Uri getTestFileFileUri() {
        final File file = Helpers.getAssetFileAsFileProviderFile(TEST_FILE);
        return Uri.parse(file.getPath());
    }

    @Test
    public void should_getFilename_forContentUri() {
        // When
        final String filename = UriHelper.getFilenameFromUri(sContentUri,
                ApplicationProvider.getApplicationContext());
        // Then
        assertThat(filename).isEqualTo(TEST_FILE);
    }

    @Test
    public void should_getFilename_forFileUri() {
        // Given
        final Uri fileUri = getTestFileFileUri();
        // When
        final String filename = UriHelper.getFilenameFromUri(fileUri,
                ApplicationProvider.getApplicationContext());
        // Then
        assertThat(filename).isEqualTo(TEST_FILE);
    }

}