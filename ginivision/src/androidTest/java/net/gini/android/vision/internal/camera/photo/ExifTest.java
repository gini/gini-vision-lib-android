package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.getTestJpeg;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ExifTest {

    @Test
    public void should_handleStringTags_containingNullBytes() throws Exception {
        // Given
        // Test jpeg make and model tags contain null bytes
        final byte[] testJpeg = getTestJpeg("exif-string-tag-with-null-bytes.jpeg");
        final Exif.RequiredTags requiredTags = Exif.readRequiredTags(testJpeg);

        // When
        final Exif exif = Exif.builder(testJpeg).setRequiredTags(requiredTags).build();

        // Then
        final byte[] outJpeg = exif.writeToJpeg(testJpeg);
        final Exif.RequiredTags outRequiredTags = Exif.readRequiredTags(outJpeg);

        assertThat(outRequiredTags.make.getStringValue()).isEqualTo("Lenovo");
        assertThat(outRequiredTags.model.getStringValue()).isEqualTo("Lenovo TAB 2 A10-70F");
    }
}