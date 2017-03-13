package net.gini.android.vision.internal.camera.photo;

import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;
import static net.gini.android.vision.test.Helpers.getTestJpeg;

@RunWith(AndroidJUnit4.class)
public class ExifReaderTest {

    private static byte[] TEST_JPEG = null;

    @BeforeClass
    public static void setupClass() throws IOException {
        TEST_JPEG = getTestJpeg();
    }

    @AfterClass
    public static void teardownClass() throws IOException {
        TEST_JPEG = null;
    }

    @Test
    public void should_returnNull_ifKey_wasNotFound() throws Exception {
        // Given
        ExifReader exifReader = new ExifReader(TEST_JPEG);
        // When
        String value = exifReader.getValueForKeyFromUserComment("unknownKey", "no such key here");
        // Then
        assertThat(value).isNull();
    }
}