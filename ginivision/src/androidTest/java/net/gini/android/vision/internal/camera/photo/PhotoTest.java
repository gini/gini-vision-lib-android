package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.internal.camera.photo.PhotoSubject.photo;
import static net.gini.android.vision.test.Helpers.doParcelingRoundTrip;
import static net.gini.android.vision.test.Helpers.getTestJpeg;

import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class PhotoTest {

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
    public void should_supportParceling() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        Photo photoFromParcel = doParcelingRoundTrip(photo,Photo.CREATOR);
        // Then
        assertThat(photoFromParcel).isEqualTo(photo);
    }

    @Test
    public void should_generateUUID_whenCreated() {
        // When
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // Then
        assertThat(UUID.fromString(photo.getUUID())).isNotNull();
    }

    @Test
    public void should_addUUID_toExifUserComment() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // Then
        assertAbout(photo()).that(photo).hasUUIDinUserComment(photo.getUUID());
    }
}