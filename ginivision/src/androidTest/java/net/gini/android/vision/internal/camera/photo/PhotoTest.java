package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.doParcelingRoundTrip;
import static net.gini.android.vision.test.Helpers.getTestJpeg;
import static net.gini.android.vision.test.PhotoSubject.photo;

import android.support.test.runner.AndroidJUnit4;

import net.gini.android.vision.Document;

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
        Photo photoFromParcel = doParcelingRoundTrip(photo, Photo.CREATOR);
        // Then
        assertThat(photoFromParcel).isEqualTo(photo);
    }

    @Test
    public void should_keepUserComment_whenCreating_fromDocument() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        Photo fromDocument = Photo.fromDocument(Document.fromPhoto(photo));
        // Then
        assertAbout(photo()).that(photo).hasSameUserCommentAs(fromDocument);
    }

    @Test
    public void should_setContentIdFromUserComment_whenCreating_fromDocument() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        Photo fromDocument = Photo.fromDocument(Document.fromPhoto(photo));
        // Then
        assertThat(photo.getContentId()).isEqualTo(fromDocument.getContentId());
    }

    @Test
    public void should_setRotationDeltafromUserComment_whenCreating_fromDocument() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        photo.edit().rotateTo(90).apply();
        Photo fromDocument = Photo.fromDocument(Document.fromPhoto(photo));
        // Then
        assertThat(photo.getRotationDelta()).isEqualTo(fromDocument.getRotationDelta());
    }

    @Test
    public void should_generateUUID_forContentId_whenCreated() {
        // When
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // Then
        assertThat(UUID.fromString(photo.getContentId())).isNotNull();
    }

    @Test
    public void should_generate_uniqueContentIds_forEachInstance() {
        // Given
        Photo photo1 = Photo.fromJpeg(TEST_JPEG, 0);
        Photo photo2 = Photo.fromJpeg(TEST_JPEG, 0);
        // Then
        assertThat(photo1.getContentId()).isNotEqualTo(photo2.getContentId());
    }

    @Test
    public void should_addContentId_toExifUserComment() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // Then
        assertAbout(photo()).that(photo).hasContentIdInUserComment(photo.getContentId());
    }

    @Test
    public void should_keepContentId_afterRotation() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        String contentId = photo.getContentId();
        // When
        photo.edit().rotateTo(90).apply();
        // Then
        assertAbout(photo()).that(photo).hasContentIdInUserComment(contentId);
    }

    @Test
    public void should_keepContentId_afterCompression() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        String contentId = photo.getContentId();
        // When
        photo.edit().compressBy(10).apply();
        // Then
        assertAbout(photo()).that(photo).hasContentIdInUserComment(contentId);
    }

    @Test
    public void should_initRotationDelta_whenCreated() {
        // When
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // Then
        assertThat(photo.getRotationDelta()).isEqualTo(0);
    }

    @Test
    public void should_initRotationDelta_whenCreated_withNonZeroOrientation() {
        // When
        Photo photo = Photo.fromJpeg(TEST_JPEG, 90);
        // Then
        assertThat(photo.getRotationDelta()).isEqualTo(0);
    }

    @Test
    public void should_addRotationDelta_toExifUserComment() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(0);
    }

    @Test
    public void should_updateRotationDelta_afterCWRotation() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        photo.edit().rotateTo(90).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(90);
    }

    @Test
    public void should_updateRotationDelta_afterCCWRotation() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        photo.edit().rotateTo(-90).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(270);
    }

    @Test
    public void should_normalizeRotationDelta_forCWRotation() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        photo.edit().rotateTo(450).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(90);
    }

    @Test
    public void should_normalizeRotationDelta_forCCWRotation() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 0);
        // When
        photo.edit().rotateTo(-270).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(90);
    }

    @Test
    public void should_keepRotationDelta_afterCompression() {
        // Given
        Photo photo = Photo.fromJpeg(TEST_JPEG, 90);
        // When
        photo.edit().compressBy(50).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(0);
    }
}