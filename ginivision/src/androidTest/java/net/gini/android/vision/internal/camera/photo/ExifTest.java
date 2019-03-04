package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertThat;

import net.gini.android.vision.test.Helpers;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ExifTest {

    @Test
    public void should_handleStringTags_containingNullBytes() throws Exception {
        // Given
        // Test jpeg make and model tags contain null bytes
        final byte[] testJpeg = Helpers.loadAsset("exif-string-tag-with-null-bytes.jpeg");
        final Exif.RequiredTags requiredTags = Exif.readRequiredTags(testJpeg);

        // When
        final Exif exif = Exif.builder(testJpeg).setRequiredTags(requiredTags).build();

        // Then
        final byte[] outJpeg = exif.writeToJpeg(testJpeg);
        final Exif.RequiredTags outRequiredTags = Exif.readRequiredTags(outJpeg);

        assertThat((String[]) outRequiredTags.make.getValue()).asList().contains("Lenovo");
        assertThat((String[]) outRequiredTags.model.getValue()).asList().contains(
                "Lenovo TAB 2 A10-70F");
    }
}