package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Matchers;

@RunWith(JUnit4.class)
public class CameraPermissionRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifCameraPermission_wasNotGranted() {
        Context context = getContextForCameraPermission(false);
        CameraPermissionRequirement requirement = new CameraPermissionRequirement(context);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportFulfilled_ifCameraPermission_wasGranted() {
        Context context = getContextForCameraPermission(true);
        CameraPermissionRequirement requirement = new CameraPermissionRequirement(context);

        assertThat(requirement.check().isFulfilled()).isTrue();
    }

    @NonNull
    private Context getContextForCameraPermission(boolean cameraPermissionGranted) {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(packageManager);
        String packageName = "permission.test";
        when(context.getPackageName()).thenReturn(packageName);
        when(packageManager.checkPermission(Matchers.eq(Manifest.permission.CAMERA),
                Matchers.eq(packageName)))
                .thenReturn(cameraPermissionGranted ? PackageManager.PERMISSION_GRANTED
                        : PackageManager.PERMISSION_DENIED);
        return context;
    }
}
