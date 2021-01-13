package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Created by aszotyori on 13/03/2017.
 */

@RunWith(AndroidJUnit4.class)
public class UserCommentBuilderTest {

    @Test
    public void should_createUserComment_withPredeterminedOrder_ofKeys() throws Exception {
        // Given
        final Exif.UserCommentBuilder builder = Exif.userCommentBuilder();
        // When
        builder.setRotationDelta(90)
                .setContentId("asdasd-assd-ssdsa-sdsdss")
                .setAddMake(true)
                .setAddModel(true)
                .setDeviceOrientation("landscape")
                .setDeviceType("tablet")
                .setSource("picker");
        final String userComment = builder.build();
        // Then
        final List<String> keys = getListOfKeys(userComment);
        assertThat(keys).containsExactly(Exif.USER_COMMENT_MAKE, Exif.USER_COMMENT_MODEL,
                Exif.USER_COMMENT_PLATFORM, Exif.USER_COMMENT_OS_VERSION,
                Exif.USER_COMMENT_GINI_VISION_VERSION, Exif.USER_COMMENT_CONTENT_ID,
                Exif.USER_COMMENT_ROTATION_DELTA, Exif.USER_COMMENT_DEVICE_ORIENTATION,
                Exif.USER_COMMENT_DEVICE_TYPE, Exif.USER_COMMENT_SOURCE).inOrder();
    }

    @Test
    public void should_addImportMethod_ifSet() throws Exception {
        // Given
        final Exif.UserCommentBuilder builder = Exif.userCommentBuilder();
        // When
        builder.setRotationDelta(90)
                .setContentId("asdasd-assd-ssdsa-sdsdss")
                .setAddMake(true)
                .setAddModel(true)
                .setDeviceOrientation("landscape")
                .setDeviceType("tablet")
                .setSource("external")
                .setImportMethod("picker");
        final String userComment = builder.build();
        // Then
        final List<String> keys = getListOfKeys(userComment);
        assertThat(keys).containsExactly(Exif.USER_COMMENT_MAKE, Exif.USER_COMMENT_MODEL,
                Exif.USER_COMMENT_PLATFORM, Exif.USER_COMMENT_OS_VERSION,
                Exif.USER_COMMENT_GINI_VISION_VERSION, Exif.USER_COMMENT_CONTENT_ID,
                Exif.USER_COMMENT_ROTATION_DELTA, Exif.USER_COMMENT_DEVICE_ORIENTATION,
                Exif.USER_COMMENT_DEVICE_TYPE, Exif.USER_COMMENT_SOURCE,
                Exif.USER_COMMENT_IMPORT_METHOD).inOrder();
    }

    @NonNull
    private List<String> getListOfKeys(final String userComment) {
        final List<String> keys = new ArrayList<>();
        final String[] keyValuePairs = userComment.split(",");
        for (final String keyValuePair : keyValuePairs) {
            final String[] keyAndValue = keyValuePair.split("=");
            if (keyAndValue.length > 0) {
                keys.add(keyAndValue[0]);
            }
        }
        return keys;
    }
}
