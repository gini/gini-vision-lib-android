package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;
import static net.gini.android.vision.onboarding.PageIndicatorsHelper.isPageActive;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;

@Ignore
@RunWith(AndroidJUnit4.class)
public class PageChangeListenerTest {

    @Test
    public void should_updatePageIndicators_onPageChange() {
        OnboardingFragmentImpl.PageIndicators pageIndicators = createPageIndicatorsInstance(2);
        OnboardingFragmentImpl.PageChangeListener.Callback callback = new OnboardingFragmentImpl.PageChangeListener.Callback() {
            @Override
            public void onLastPage() {
            }
        };

        OnboardingFragmentImpl.PageChangeListener pageChangeListener =
                new OnboardingFragmentImpl.PageChangeListener(pageIndicators, 0, 2, callback);
        pageChangeListener.init();

        pageChangeListener.onPageSelected(1);

        isPageActive(pageIndicators, 1);
    }

    @Test
    public void should_setPageIndicator_toInitialCurrentPage() {
        OnboardingFragmentImpl.PageIndicators pageIndicators = createPageIndicatorsInstance(2);
        OnboardingFragmentImpl.PageChangeListener.Callback callback = new OnboardingFragmentImpl.PageChangeListener.Callback() {
            @Override
            public void onLastPage() {
            }
        };

        OnboardingFragmentImpl.PageChangeListener pageChangeListener =
                new OnboardingFragmentImpl.PageChangeListener(pageIndicators, 1, 2, callback);
        pageChangeListener.init();

        isPageActive(pageIndicators, 1);
    }

    @Test
    public void should_invokeCallback_whenLastPage_wasReached() {
        OnboardingFragmentImpl.PageIndicators pageIndicators = createPageIndicatorsInstance(4);

        final AtomicBoolean lastPageCalled = new AtomicBoolean();
        OnboardingFragmentImpl.PageChangeListener.Callback callback = new OnboardingFragmentImpl.PageChangeListener.Callback() {
            @Override
            public void onLastPage() {
                lastPageCalled.set(true);
            }
        };

        OnboardingFragmentImpl.PageChangeListener pageChangeListener =
                new OnboardingFragmentImpl.PageChangeListener(pageIndicators, 1, 4, callback);
        pageChangeListener.init();

        pageChangeListener.onPageSelected(3);

        assertThat(lastPageCalled.get()).isTrue();
    }

    @NonNull
    private OnboardingFragmentImpl.PageIndicators createPageIndicatorsInstance(int nrOfPages) {
        LinearLayout linearLayout = new LinearLayout(InstrumentationRegistry.getTargetContext());
        OnboardingFragmentImpl.PageIndicators pageIndicators =
                new OnboardingFragmentImpl.PageIndicators(InstrumentationRegistry.getTargetContext(), nrOfPages, linearLayout);
        pageIndicators.create();
        return pageIndicators;
    }
}
