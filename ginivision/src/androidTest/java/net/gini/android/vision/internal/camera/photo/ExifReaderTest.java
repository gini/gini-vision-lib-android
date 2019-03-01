package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.getTestJpeg;

import net.gini.android.vision.test.Helpers;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ExifReaderTest {

    @Test
    public void should_readUserComment() throws Exception {
        final byte[] testJpeg = Helpers.loadAsset("remslip-valid-user-comment.jpeg");
        final ExifReader exifReader = ExifReader.forJpeg(testJpeg);
        assertThat(exifReader.getUserComment()).isEqualTo("This is valid");
    }

    @Test
    public void should_readUserComment_ifLength_isTooShort() throws Exception {
        // Minimum length is 8 bytes (character code length), following image has 5 bytes
        final byte[] testJpeg = Helpers.loadAsset("remslip-malformed-user-comment.jpeg");
        final ExifReader exifReader = ExifReader.forJpeg(testJpeg);
        assertThat(exifReader.getUserComment()).isEqualTo("short");
    }

    @Test
    public void should_throwException_ifMetadata_wasMissing() throws Exception {
        final byte[] testJpeg = Helpers.loadAsset("remslip-no-metadata.jpeg");
        ExifReaderException exception = null;
        try {
            ExifReader.forJpeg(testJpeg);
        } catch (final ExifReaderException e) {
            exception = e;
        }
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("No jpeg metadata found");
    }

    @Test
    public void should_throwException_ifUserComment_wasMissing() throws Exception {
        // Given
        final byte[] testJpeg = Helpers.loadAsset("remslip-no-user-comment.jpeg");
        final ExifReader exifReader = ExifReader.forJpeg(testJpeg);
        ExifReaderException exception = null;
        try {
            exifReader.getUserComment();
        } catch (final ExifReaderException e) {
            exception = e;
        }
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("No User Comment found");
    }

    @Test
    public void should_throwException_ifByteArray_wasNotJpeg() throws Exception {
        // Given
        ExifReaderException exception = null;
        try {
            final byte[] notJpeg = new byte[]{0, 2, 3, 1};
            ExifReader.forJpeg(notJpeg);
        } catch (final ExifReaderException e) {
            exception = e;
        }
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).startsWith("Could not read jpeg metadata: ");
    }

    @Test
    public void should_returnValue_forKey_inUserCommentCSV() throws Exception {
        // Given
        final byte[] jpeg = getTestJpeg();
        final ExifReader exifReader = ExifReader.forJpeg(jpeg);
        // When
        final String value = exifReader.getValueForKeyFromUserComment("OSVer",
                "Platform=Android,OSVer=7.0,GiniVisionVer=2.1.0(SNAPSHOT),"
                        + "ContentId=21e5bc66-ee46-4ec4-93db-16bd553561bf,RotDeltaDeg=0");
        // Then
        assertThat(value).isEqualTo("7.0");
    }

    @Test
    public void should_returnNull_ifKey_wasNotFound_inUserCommentCSV() throws Exception {
        // Given
        final byte[] jpeg = getTestJpeg();
        final ExifReader exifReader = ExifReader.forJpeg(jpeg);
        // When
        final String value = exifReader.getValueForKeyFromUserComment("unknownKey",
                "no such key here");
        // Then
        assertThat(value).isNull();
    }

    @Test
    public void should_returnNull_ifUserCommentCSV_isEmpty() throws Exception {
        // Given
        final byte[] jpeg = getTestJpeg();
        final ExifReader exifReader = ExifReader.forJpeg(jpeg);
        // When
        final String value = exifReader.getValueForKeyFromUserComment("", "");
        // Then
        assertThat(value).isNull();
    }

    @Test
    public void should_returnNull_ifUserCommentCSV_isMalformed() throws Exception {
        // Given
        final byte[] jpeg = getTestJpeg();
        final ExifReader exifReader = ExifReader.forJpeg(jpeg);
        // When
        final String value = exifReader.getValueForKeyFromUserComment("OSVer", ",Key1=OSVer=,");
        // Then
        assertThat(value).isNull();
    }

}