package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.doParcelingRoundTrip;
import static net.gini.android.vision.test.Helpers.getTestJpeg;
import static net.gini.android.vision.test.PhotoSubject.photo;

import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.ImageDocument;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.UUID;

import androidx.test.ext.junit.runners.AndroidJUnit4;

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
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        final MutablePhoto photoFromParcel = doParcelingRoundTrip(photo, MutablePhoto.CREATOR);
        // Then
        assertThat(photoFromParcel).isEqualTo(photo);
    }

    @Test
    public void should_keepUserComment_whenCreating_fromDocument() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        final MutablePhoto fromDocument =
                (MutablePhoto) PhotoFactory.newPhotoFromDocument(
                        (ImageDocument) DocumentFactory.newImageDocumentFromPhoto(photo));
        // Then
        assertAbout(photo()).that(photo).hasSameUserCommentAs(fromDocument);
    }

    @Test
    public void should_setContentIdFromUserComment_whenCreating_fromDocument() {
        // Given
        final MutablePhoto photo = (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait",
                "photo", ImageDocument.Source.newCameraSource());
        // When
        final MutablePhoto fromDocument =
                (MutablePhoto) PhotoFactory.newPhotoFromDocument(
                        (ImageDocument) DocumentFactory.newImageDocumentFromPhoto(photo));
        // Then
        assertThat(photo.getContentId()).isEqualTo(fromDocument.getContentId());
    }

    @Test
    public void should_setRotationDeltafromUserComment_whenCreating_fromDocument() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        photo.edit().rotateTo(90).apply();
        final MutablePhoto fromDocument =
                (MutablePhoto) PhotoFactory.newPhotoFromDocument(
                        (ImageDocument) DocumentFactory.newImageDocumentFromPhoto(photo));
        // Then
        assertThat(photo.getRotationDelta()).isEqualTo(fromDocument.getRotationDelta());
    }

    @Test
    public void should_generateUUID_forContentId_whenCreated() {
        // When
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // Then
        assertThat(UUID.fromString(photo.getContentId())).isNotNull();
    }

    @Test
    public void should_generate_uniqueContentIds_forEachInstance() {
        // Given
        final MutablePhoto photo1 =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        final MutablePhoto photo2 =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // Then
        assertThat(photo1.getContentId()).isNotEqualTo(photo2.getContentId());
    }

    @Test
    public void should_addContentId_toExifUserComment() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // Then
        assertAbout(photo()).that(photo).hasContentIdInUserComment(photo.getContentId());
    }

    @Test
    public void should_keepContentId_afterRotation() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        final String contentId = photo.getContentId();
        // When
        photo.edit().rotateTo(90).apply();
        // Then
        assertAbout(photo()).that(photo).hasContentIdInUserComment(contentId);
    }

    @Test
    public void should_keepContentId_afterCompression() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        final String contentId = photo.getContentId();
        // When
        photo.edit().compressBy(10).apply();
        // Then
        assertAbout(photo()).that(photo).hasContentIdInUserComment(contentId);
    }

    @Test
    public void should_initRotationDelta_whenCreated() {
        // When
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // Then
        assertThat(photo.getRotationDelta()).isEqualTo(0);
    }

    @Test
    public void should_initRotationDelta_whenCreated_withNonZeroOrientation() {
        // When
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 90, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // Then
        assertThat(photo.getRotationDelta()).isEqualTo(0);
    }

    @Test
    public void should_addRotationDelta_toExifUserComment() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(0);
    }

    @Test
    public void should_updateRotationDelta_afterCWRotation() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        photo.edit().rotateTo(90).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(90);
    }

    @Test
    public void should_updateRotationDelta_afterCCWRotation() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        photo.edit().rotateTo(-90).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(270);
    }

    @Test
    public void should_normalizeRotationDelta_forCWRotation() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        photo.edit().rotateTo(450).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(90);
    }

    @Test
    public void should_normalizeRotationDelta_forCCWRotation() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 0, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        photo.edit().rotateTo(-270).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(90);
    }

    @Test
    public void should_keepRotationDelta_afterCompression() {
        // Given
        final MutablePhoto photo =
                (MutablePhoto) PhotoFactory.newPhotoFromJpeg(TEST_JPEG, 90, "portrait", "photo",
                        ImageDocument.Source.newCameraSource());
        // When
        photo.edit().compressBy(50).apply();
        // Then
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(0);
    }
}