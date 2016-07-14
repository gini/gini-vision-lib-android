package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;
import static net.gini.android.vision.test.Helpers.doParcelingRoundTrip;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class OnboardingPageTest {

    @Test
    public void should_beParcelable() {
        //noinspection ResourceType
        OnboardingPage toParcel = new OnboardingPage(314, 42, true);
        OnboardingPage fromParcel = doParcelingRoundTrip(toParcel, OnboardingPage.CREATOR);

        assertThat(toParcel.getTextResId()).isEqualTo(fromParcel.getTextResId());
        assertThat(toParcel.getImageResId()).isEqualTo(fromParcel.getImageResId());
        assertThat(toParcel.isTransparent()).isEqualTo(fromParcel.isTransparent());
    }
}
