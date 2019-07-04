package net.gini.android.vision.internal.storage;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.copyAssetToStorage;
import static net.gini.android.vision.test.Helpers.getTestJpeg;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.robolectric.Shadows.shadowOf;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import android.app.Application;
import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowContentResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;

import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Created by Alpar Szotyori on 04.07.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
@RunWith(AndroidJUnit4.class)
@Config(shadows = {
        ImageDiskStoreTest.ContentResolverShadow.class,
        ImageDiskStoreTest.MimeTypeMapShadow.class
})
public class ImageDiskStoreTest {

    @Test
    public void should_saveByteArray_toOwnFolder_inAppFilesDir() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();

        // When
        imageDiskStore.save(appContext, getTestJpeg());

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()).hasLength(1);
    }

    @Test
    public void should_returnUri_toFile_ofSavedByteArray() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();

        // When
        final Uri uri = imageDiskStore.save(appContext, getTestJpeg());

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()[0].getAbsolutePath()).isEqualTo(uri.getPath());
    }

    @Test
    public void should_notUseExtension_whenSavingByteArray() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();

        // When
        imageDiskStore.save(appContext, getTestJpeg());

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()[0].getName()).doesNotContainMatch("^.+\\..+$");
    }

    @Test
    public void should_saveUri_toOwnFolder_inAppFilesDir() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();

        final Application appContext = getApplicationContext();
        final Uri uri = getUriAndRegisterInputStream(appContext);

        // When
        imageDiskStore.save(appContext, uri);

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()).hasLength(1);
    }

    private Uri getUriAndRegisterInputStream(final Application appContext) throws IOException {
        final File filesDir = appContext.getFilesDir();
        copyAssetToStorage("invoice.jpg", filesDir.getPath());

        final File file = new File(filesDir, "invoice.jpg");
        final Uri uri = Uri.fromFile(file);

        final ShadowContentResolver contentResolver = shadowOf(appContext.getContentResolver());
        contentResolver.registerInputStream(uri, new FileInputStream(file));
        return uri;
    }

    @Test
    public void should_preserveExtension_whenSavingUri() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();

        final Application appContext = getApplicationContext();
        final Uri uri = getUriAndRegisterInputStream(appContext);

        // When
        imageDiskStore.save(appContext, uri);

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()[0].getName()).endsWith(".jpg");
    }

    @Test
    public void should_returnUri_toFile_ofSavedUri() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();

        final Application appContext = getApplicationContext();
        final Uri uri = getUriAndRegisterInputStream(appContext);

        // When
        final Uri resultUri = imageDiskStore.save(appContext, uri);

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()[0].getAbsolutePath()).isEqualTo(resultUri.getPath());
    }

    @Test
    public void should_generateFilenames_basedOnTheCurrentTime_inMilliseconds() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();

        // When
        imageDiskStore.save(appContext, getTestJpeg());

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        final String fileName = storeDir.listFiles()[0].getName();

        final Calendar calendarFileNameDate = Calendar.getInstance();
        calendarFileNameDate.setTimeInMillis(Long.parseLong(fileName));

        final Calendar calendarNow = Calendar.getInstance();
        calendarNow.get(Calendar.YEAR);

        assertThat(calendarFileNameDate.get(Calendar.YEAR)).isEqualTo(
                calendarNow.get(Calendar.YEAR));
        assertThat(calendarFileNameDate.get(Calendar.MONTH)).isEqualTo(
                calendarNow.get(Calendar.MONTH));
        assertThat(calendarFileNameDate.get(Calendar.DAY_OF_MONTH)).isEqualTo(
                calendarNow.get(Calendar.DAY_OF_MONTH));
        assertThat(calendarFileNameDate.get(Calendar.HOUR)).isEqualTo(
                calendarNow.get(Calendar.HOUR));
        assertThat(calendarFileNameDate.get(Calendar.MINUTE)).isEqualTo(
                calendarNow.get(Calendar.MINUTE));
    }

    @Test
    public void should_updateByteArray() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();
        final byte[] bytes = new byte[]{10, 20, 1, 34, 42};

        // When
        final Uri uri = imageDiskStore.save(appContext, getTestJpeg());
        final boolean result = imageDiskStore.update(uri, bytes);

        // Then
        assertThat(result).isTrue();
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()[0].length()).isEqualTo(bytes.length);
    }

    @Test
    public void should_failUpdate_ifUriIsInvalid() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();
        final byte[] bytes = new byte[]{10, 20, 1, 34, 42};

        // When
        imageDiskStore.save(appContext, getTestJpeg());
        final boolean result = imageDiskStore.update(Uri.EMPTY, bytes);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_failUpdate_ifErrorWritingFile_atUri() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = spy(new ImageDiskStore());
        final Application appContext = getApplicationContext();
        final byte[] bytes = new byte[]{10, 20, 1, 34, 42};

        // When
        imageDiskStore.save(appContext, getTestJpeg());
        Mockito.doThrow(new IOException()).when(imageDiskStore).writeToFile(any(File.class),
                any(byte[].class));
        final boolean result = imageDiskStore.update(Uri.EMPTY, bytes);

        // Then
        assertThat(result).isFalse();
    }

    @Test
    public void should_deleteUri() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();
        copyAssetToStorage("invoice.jpg", appContext.getFilesDir().getPath());

        // When
        final Uri uri = imageDiskStore.save(appContext, getTestJpeg());
        imageDiskStore.save(appContext, getTestJpeg());
        imageDiskStore.delete(uri);

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.listFiles()).hasLength(1);
    }

    @Test
    public void should_clearStore_byRemovingOnlyOwnFiles() throws Exception {
        // Given
        final ImageDiskStore imageDiskStore = new ImageDiskStore();
        final Application appContext = getApplicationContext();
        copyAssetToStorage("invoice.jpg", appContext.getFilesDir().getPath());

        // When
        imageDiskStore.save(appContext, getTestJpeg());
        imageDiskStore.save(appContext, getTestJpeg());
        imageDiskStore.save(appContext, getTestJpeg());
        imageDiskStore.save(appContext, getTestJpeg());
        ImageDiskStore.clear(appContext);

        // Then
        final File storeDir = new File(appContext.getFilesDir(), ImageDiskStore.STORE_DIR);
        assertThat(storeDir.exists()).isFalse();

        final File nonStoreFile = new File(appContext.getFilesDir(), "invoice.jpg");
        assertThat(nonStoreFile.exists()).isTrue();
    }

    @Implements(ContentResolver.class)
    public static class ContentResolverShadow extends ShadowContentResolver {

        @Override
        @Implementation
        protected String getType(final Uri uri) {
            final String name = uri.getLastPathSegment();
            return name.substring(name.lastIndexOf(".") + 1);
        }
    }

    @Implements(MimeTypeMap.class)
    public static class MimeTypeMapShadow {

        @Implementation
        public String getExtensionFromMimeType(final String mimeType) {
            return mimeType;
        }
    }
}