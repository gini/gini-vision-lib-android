package net.gini.android.vision.internal.camera.photo;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by aszotyori on 13/03/2017.
 */

@RunWith(AndroidJUnit4.class)
public class UserCommentBuilderTest {

    @Test
    public void should_createUserComment_withPredeterminedOrder_ofKeys() throws Exception {
        // Given
        Exif.UserCommentBuilder builder = Exif.userCommentBuilder();
        // When
        builder.setRotationDelta(90)
                .setUUID("asdasd-assd-ssdsa-sdsdss")
                .setAddMake(true)
                .setAddModel(true);
        String userComment = builder.build();
        // Then
        List<String> keys = getListOfKeys(userComment);
        assertThat(keys).containsExactly(Exif.USER_COMMENT_MAKE, Exif.USER_COMMENT_MODEL,
                Exif.USER_COMMENT_PLATFORM, Exif.USER_COMMENT_OS_VERSION,
                Exif.USER_COMMENT_GINI_VISION_VERSION, Exif.USER_COMMENT_UUID,
                Exif.USER_COMMENT_ROTATION_DELTA).inOrder();
    }

    @NonNull
    private List<String> getListOfKeys(String userComment) {
        List<String> keys = new ArrayList<>();
        String[] keyValuePairs = userComment.split(",");
        for (String keyValuePair : keyValuePairs) {
            String[] keyAndValue = keyValuePair.split("=");
            if (keyAndValue.length > 0) {
                keys.add(keyAndValue[0]);
            }
        }
        return keys;
    }
}
