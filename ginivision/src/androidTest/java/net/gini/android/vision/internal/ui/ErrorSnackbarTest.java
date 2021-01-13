package net.gini.android.vision.internal.ui;

import static com.google.common.truth.Truth.assertThat;

import android.view.View;
import android.widget.RelativeLayout;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

@RunWith(AndroidJUnit4.class)
public class ErrorSnackbarTest {

    private static final int TEST_PAUSE_DURATION = 400;
    @Rule
    public ActivityTestRule<ErrorSnackbarTestActivity> mActivityTestRule = new ActivityTestRule<>(
            ErrorSnackbarTestActivity.class);

    @Test
    public void should_showErrorSnackbar() throws InterruptedException {
        showErrorSnackbar("Test message", null, null, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message", null);
    }

    @Test
    public void should_hideErrorSnackbar() throws InterruptedException {
        final ErrorSnackbar errorSnackbar = showErrorSnackbar("Test message", null, null,
                ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message", null);

        hideErrorSnackbar(errorSnackbar);

        assertErrorSnackbarIsHidden("Test message", null);
    }

    @Test
    public void should_hidePreviousErrorSnackbar_whenShowingANewOne_inTheSameLayout()
            throws InterruptedException {
        showErrorSnackbar("Test message 1", null, null, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message 1", null);

        showErrorSnackbar("Test message 2", null, null, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsHidden("Test message 1", null);
        assertErrorSnackbarIsShown("Test message 2", null);
    }

    @Test
    public void should_showMultipleErrorSnackbars_inDifferentLayouts() throws InterruptedException {
        showErrorSnackbar("Test message 1", null, null, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message 1", null);

        final ErrorSnackbarTestActivity activity = mActivityTestRule.getActivity();

        showErrorSnackbar(activity.getSubviewLayout(), "Test message 2", null, null,
                ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message 1", null);
        assertErrorSnackbarIsShown("Test message 2", null);
    }

    @Test
    public void should_hideErrorSnackbar_forAGivenLayout() throws InterruptedException {
        showErrorSnackbar("Test message 1", null, null, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message 1", null);

        final ErrorSnackbarTestActivity activity = mActivityTestRule.getActivity();
        final RelativeLayout rootLayout = activity.getRootLayout();
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                ErrorSnackbar.hideExisting(rootLayout);
            }
        });

        // Wait for the hide animation to finish
        Thread.sleep(ErrorSnackbar.ANIM_DURATION + TEST_PAUSE_DURATION);

        assertErrorSnackbarIsHidden("Test message 1", null);
    }

    @Test
    public void should_notShowButton_ifButtonTitle_wasNotGiven() throws InterruptedException {
        showErrorSnackbar("Test message", null, null, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message", null);

        Espresso.onView(ViewMatchers.withText("Button title"))
                .check(ViewAssertions.doesNotExist());
    }

    @Test
    public void should_showButton_ifButtonTitle_wasGiven() throws InterruptedException {
        showErrorSnackbar("Test message", "Button title", null, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message", "Button title");
    }

    @Test
    public void should_setClickListener_forButton_ifClickListener_wasGiven_andButtonTitle_wasGiven()
            throws InterruptedException {
        final AtomicBoolean buttonClicked = new AtomicBoolean();
        showErrorSnackbar("Test message", "Button title", new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                buttonClicked.set(true);
            }
        }, ErrorSnackbar.LENGTH_INDEFINITE);

        assertErrorSnackbarIsShown("Test message", "Button title");

        Espresso.onView(ViewMatchers.withText("Button title"))
                .perform(ViewActions.click());

        assertThat(buttonClicked.get()).isTrue();
    }

    private ErrorSnackbar showErrorSnackbar(@NonNull final RelativeLayout rootLayout,
            @NonNull final String message, @Nullable final String buttonTitle,
            @Nullable final View.OnClickListener buttonClickListener, final int duration)
            throws InterruptedException {
        final ErrorSnackbarTestActivity activity = mActivityTestRule.getActivity();

        final AtomicReference<ErrorSnackbar> errorSnackbar = new AtomicReference<>();

        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                errorSnackbar.set(ErrorSnackbar.make(activity, rootLayout, message, buttonTitle,
                        buttonClickListener, duration));
                errorSnackbar.get().show();
            }
        });

        // Wait for the show animation to finish
        Thread.sleep(ErrorSnackbar.ANIM_DURATION + TEST_PAUSE_DURATION);

        return errorSnackbar.get();
    }

    private ErrorSnackbar showErrorSnackbar(@NonNull final String message,
            @Nullable final String buttonTitle,
            @Nullable final View.OnClickListener buttonClickListener, final int duration)
            throws InterruptedException {
        final ErrorSnackbarTestActivity activity = mActivityTestRule.getActivity();
        return showErrorSnackbar(activity.getRootLayout(), message, buttonTitle,
                buttonClickListener, duration);
    }

    private void hideErrorSnackbar(final ErrorSnackbar errorSnackbar) throws InterruptedException {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            @Override
            public void run() {
                errorSnackbar.hide();
            }
        });

        // Wait for the hide animation to finish
        Thread.sleep(ErrorSnackbar.ANIM_DURATION + TEST_PAUSE_DURATION);
    }

    private void assertErrorSnackbarIsShown(@NonNull final String message,
            @Nullable final String buttonTitle) {
        Espresso.onView(ViewMatchers.withText(message))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        if (buttonTitle != null) {
            Espresso.onView(ViewMatchers.withText(buttonTitle))
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
        }
    }

    private void assertErrorSnackbarIsHidden(@NonNull final String message,
            @Nullable final String buttonTitle) {
        Espresso.onView(ViewMatchers.withText(message))
                .check(ViewAssertions.doesNotExist());
        if (buttonTitle != null) {
            Espresso.onView(ViewMatchers.withText(buttonTitle))
                    .check(ViewAssertions.doesNotExist());
        }
    }
}
