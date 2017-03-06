package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.getTestJpeg;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

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
        Photo photoFromParcel = doParcelRoundTrip(photo);
        // Then
        assertThat(photoFromParcel).isEqualTo(photo);
    }

    private Photo doParcelRoundTrip(final Photo photo) {
        Parcel parcel = Parcel.obtain();
        photo.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        Photo fromParcel = Photo.CREATOR.createFromParcel(parcel);
        parcel.recycle();
        return fromParcel;
    }
}