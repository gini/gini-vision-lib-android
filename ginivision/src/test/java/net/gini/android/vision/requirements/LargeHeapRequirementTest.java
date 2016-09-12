package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LargeHeapRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifLargeHeap_wasNotEnabled() throws PackageManager.NameNotFoundException {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        ApplicationInfo applicationInfo = mock(ApplicationInfo.class);

        doReturn(packageManager).when(context).getPackageManager();
        //noinspection WrongConstant
        doReturn(applicationInfo).when(packageManager).getApplicationInfo(anyString(), eq(PackageManager.GET_META_DATA));

        LargeHeapRequirement largeHeapRequirement = new LargeHeapRequirement(context);

        assertThat(largeHeapRequirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportUnfulfilled_ifPackageName_wasNotFound() throws PackageManager.NameNotFoundException {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);

        doReturn(packageManager).when(context).getPackageManager();
        //noinspection WrongConstant
        doThrow(PackageManager.NameNotFoundException.class).when(packageManager).getApplicationInfo(anyString(), eq(PackageManager.GET_META_DATA));

        LargeHeapRequirement largeHeapRequirement = new LargeHeapRequirement(context);

        assertThat(largeHeapRequirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportFulfilled_ifLargeHeap_wasEnabled() throws PackageManager.NameNotFoundException {
        Context context = mock(Context.class);
        PackageManager packageManager = mock(PackageManager.class);
        ApplicationInfo applicationInfo = mock(ApplicationInfo.class);

        doReturn(packageManager).when(context).getPackageManager();
        //noinspection WrongConstant
        doReturn(applicationInfo).when(packageManager).getApplicationInfo(anyString(), eq(PackageManager.GET_META_DATA));
        applicationInfo.flags = ApplicationInfo.FLAG_LARGE_HEAP;

        LargeHeapRequirement largeHeapRequirement = new LargeHeapRequirement(context);

        assertThat(largeHeapRequirement.check().isFulfilled()).isTrue();
    }
}
