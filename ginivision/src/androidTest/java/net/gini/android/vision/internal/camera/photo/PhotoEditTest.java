package net.gini.android.vision.internal.camera.photo;

import static com.google.common.truth.Truth.assertAbout;

import static net.gini.android.vision.test.Helpers.getTestJpeg;
import static net.gini.android.vision.test.PhotoSubject.photo;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class PhotoEditTest {

    @Test
    public void should_allowAddingModifications_afterAsyncApply() throws Exception {
        // Given
        final CountDownLatch latch = new CountDownLatch(1);
        final Photo photo = getPhoto();
        final PhotoEdit photoEdit = new PhotoEdit(photo);

        // When
        // Start async rotations to 90 degrees
        for (int i = 0; i < 20; i++) {
            photoEdit.rotateTo(90);
        }
        photoEdit.applyAsync(new PhotoEdit.PhotoEditCallback() {
            @Override
            public void onDone(@NonNull final Photo photo) {
                latch.countDown();
            }

            @Override
            public void onFailed() {
            }
        });
        // Add rotations until the async apply completes
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                photoEdit.rotateTo(180);
                if (latch.getCount() == 1) {
                    handler.post(this);
                }
            }
        });

        // Then
        latch.await(500, TimeUnit.MILLISECONDS);
        // Rotation should be at 90 degrees
        assertAbout(photo()).that(photo).hasRotationDeltaInUserComment(90);
    }

    private Photo getPhoto() throws IOException {
        final byte[] jpeg = getTestJpeg();
        return Photo.fromJpeg(jpeg, 0);
    }

}