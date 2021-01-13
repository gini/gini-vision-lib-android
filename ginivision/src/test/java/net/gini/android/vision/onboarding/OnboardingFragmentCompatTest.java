package net.gini.android.vision.onboarding;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.app.Application;
import android.content.res.Resources;

import net.gini.android.vision.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import androidx.fragment.app.FragmentActivity;
import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Created by Alpar Szotyori on 21.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
@RunWith(AndroidJUnit4.class)
public class OnboardingFragmentCompatTest {

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenListener_wasNotSet() {
        // Given
        final ArrayList<OnboardingPage> pages = new ArrayList<>();
        pages.add(new OnboardingPage(R.string.gv_onboarding_flat,
                R.drawable.gv_onboarding_flat));

        final OnboardingFragmentCompat fragment = spy(
                OnboardingFragmentCompat.createInstance(pages));

        final Application application = mock(Application.class);
        when(application.getResources()).thenReturn(mock(Resources.class));

        final FragmentActivity activity = mock(FragmentActivity.class);
        when(activity.getApplication()).thenReturn(application);
        when(activity.getResources()).thenReturn(mock(Resources.class));

        when(fragment.getActivity()).thenReturn(activity);

        // When
        fragment.onCreate(null);
    }

}
