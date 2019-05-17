package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;

import androidx.annotation.NonNull;

@RunWith(JUnit4.class)
public class CameraPermissionRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifCameraPermission_wasNotGranted() {
        final Context context = getContextForCameraPermission(false);
        final CameraPermissionRequirement requirement = new CameraPermissionRequirement(context);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportFulfilled_ifCameraPermission_wasGranted() {
        final Context context = getContextForCameraPermission(true);
        final CameraPermissionRequirement requirement = new CameraPermissionRequirement(context);

        assertThat(requirement.check().isFulfilled()).isTrue();
    }

    @NonNull
    private Context getContextForCameraPermission(final boolean cameraPermissionGranted) {
        final Context context = mock(Context.class);
        final PackageManager packageManager = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(packageManager);
        final String packageName = "permission.test";
        when(context.getPackageName()).thenReturn(packageName);
        when(packageManager.checkPermission(Matchers.eq(Manifest.permission.CAMERA),
                Matchers.eq(packageName)))
                .thenReturn(cameraPermissionGranted ? PackageManager.PERMISSION_GRANTED
                        : PackageManager.PERMISSION_DENIED);
        return context;
    }
}
