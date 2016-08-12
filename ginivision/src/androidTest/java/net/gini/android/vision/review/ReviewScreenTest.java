package net.gini.android.vision.review;

import static com.google.common.truth.Truth.assertThat;
import static net.gini.android.vision.test.Helpers.getTestJpeg;
import static net.gini.android.vision.test.Helpers.prepareLooper;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivityTestStub;
import net.gini.android.vision.camera.photo.Photo;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(AndroidJUnit4.class)
public class ReviewScreenTest {

    private static final int PAUSE_DURATION = 500;
    private static final int PAUSE_DURATION_LONG = 2_000;

    @Rule
    public IntentsTestRule<ReviewActivityTestStub> mActivityTestRule = new IntentsTestRule<>(ReviewActivityTestStub.class, true, false);

    private static byte[] TEST_JPEG = null;

    @BeforeClass
    public static void setupClass() throws IOException {
        TEST_JPEG = getTestJpeg();
    }

    @AfterClass
    public static void teardownClass() throws IOException {
        TEST_JPEG = null;
    }

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenAnalysisActivityClass_wasNotGiven() {
        prepareLooper();
        ReviewActivityTestStub reviewActivity = new ReviewActivityTestStub();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        reviewActivity.setIntent(intent);

        reviewActivity.readExtras();
    }

    @Test
    public void should_rotatePreview_accordingToOrientation() throws IOException, InterruptedException {
        ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 180);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // Give some time for the activity to settle
        Thread.sleep(PAUSE_DURATION);

        assertThat(activity.getFragment().getFragmentImpl().getImageDocument().getRotation()).isWithin(0.0f).of(180);
    }

    @Test
    public void should_rotatePreview_whenRotateButton_isClicked() throws IOException, InterruptedException {
        ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 90);

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        // Give some time for the rotation animation to finish
        Thread.sleep(PAUSE_DURATION);

        assertThat(activity.getFragment().getFragmentImpl().getImageDocument().getRotation()).isWithin(0.0f).of(180);
    }

    @Test
    public void should_invokeAnalyzeDocument_whenLaunched() throws IOException, InterruptedException {
        ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 90);

        final AtomicBoolean analyzeDocumentInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestStub.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull Document document) {
                analyzeDocumentInvoked.set(true);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION_LONG);

        assertThat(analyzeDocumentInvoked.get()).isTrue();
    }

    @Test
    public void should_compressJpeg_beforeAnalyzeDocument_isInvoked() throws IOException, InterruptedException {
        ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 90);

        final AtomicReference<Document> documentToAnalyze = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestStub.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull Document document) {
                documentToAnalyze.set(document);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION_LONG);

        assertThat(documentToAnalyze.get()).isNotNull();
        assertThat(documentToAnalyze.get().getJpeg().length).isLessThan(TEST_JPEG.length);
    }

    @Test
    public void should_onlyInvokeProceedToAnalysis_whenNextButton_wasClicked_ifDocument_wasModified_andNotAnalyzed() throws InterruptedException {
        ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 0);

        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        final AtomicBoolean proceedToAnalysisInvoked = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestStub.ListenerHook() {
            @Override
            public void onAddDataToResult(@NonNull Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull Document document) {
                proceedToAnalysisInvoked.set(true);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        assertThat(proceedToAnalysisInvoked.get()).isTrue();
        assertThat(addDataToResultInvoked.get()).isFalse();
    }

    @Test
    public void should_onlyInvokeProceedToAnalysis_whenNextButton_wasClicked_ifDocument_wasModified_andAnalyzed() throws InterruptedException {
        final ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 0);

        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        final AtomicBoolean proceedToAnalysisInvoked = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestStub.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull Document document) {
                // Notify that document was analyzed
                activity.onDocumentAnalyzed();
            }

            @Override
            public void onAddDataToResult(@NonNull Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull Document document) {
                proceedToAnalysisInvoked.set(true);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        assertThat(proceedToAnalysisInvoked.get()).isTrue();
        assertThat(addDataToResultInvoked.get()).isFalse();
    }

    @Test
    public void should_onlyInvokeProceedToAnalysis_whenNextButton_wasClicked_ifDocument_wasNotModified_andNotAnalyzed() throws InterruptedException {
        ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 0);

        final AtomicBoolean proceedToAnalysisInvoked = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestStub.ListenerHook() {
            @Override
            public void onAddDataToResult(@NonNull Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull Document document) {
                proceedToAnalysisInvoked.set(true);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        assertThat(proceedToAnalysisInvoked.get()).isTrue();
        assertThat(addDataToResultInvoked.get()).isFalse();
    }

    @Test
    public void should_invokeDocumentReviewed_andAddDataToResult_whenNextButton_wasClicked_ifDocument_wasNotModified_andWasAnalyzed() throws InterruptedException {
        final ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 0);

        final AtomicBoolean documentReviewedCalled = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestStub.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull Document document) {
                // Notify that document was analyzed
                activity.onDocumentAnalyzed();
            }

            @Override
            public void onAddDataToResult(@NonNull Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
                documentReviewedCalled.set(true);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        assertThat(documentReviewedCalled.get()).isTrue();
        assertThat(addDataToResultInvoked.get()).isTrue();
    }

    @Test
    public void should_notInvokeAnyListenerMethods_whenHomeButton_wasClicked() throws InterruptedException {
        final ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 0);

        ReviewActivityTestStub.ListenerHook listenerHook = mock(ReviewActivityTestStub.ListenerHook.class);

        activity.setListenerHook(listenerHook);

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        // Click home (back)
        Espresso.onView(ViewMatchers.withContentDescription("Navigate up"))
                .perform(ViewActions.click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        verify(listenerHook, never()).onDocumentReviewedAndAnalyzed(any(Document.class));
        verify(listenerHook, never()).onAddDataToResult(any(Intent.class));
        verify(listenerHook, never()).onProceedToAnalysisScreen(any(Document.class));
    }

    @Test
    public void should_notInvokeAnyListenerMethods_whenBackButton_wasClicked() throws InterruptedException {
        final ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 0);

        ReviewActivityTestStub.ListenerHook listenerHook = mock(ReviewActivityTestStub.ListenerHook.class);

        activity.setListenerHook(listenerHook);

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        // Click back
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).pressBack();

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        verify(listenerHook, never()).onDocumentReviewedAndAnalyzed(any(Document.class));
        verify(listenerHook, never()).onAddDataToResult(any(Intent.class));
        verify(listenerHook, never()).onProceedToAnalysisScreen(any(Document.class));
    }

    @Test
    public void should_invokeDocumentWasRotated_whenRotateButton_wasClicked() throws InterruptedException {
        final ReviewActivityTestStub activity = startReviewActivity(TEST_JPEG, 0);

        ReviewActivityTestStub.ListenerHook listenerHook = mock(ReviewActivityTestStub.ListenerHook.class);

        activity.setListenerHook(listenerHook);

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        verify(listenerHook).onDocumentWasRotated(any(Document.class), eq(0), eq(90));
    }

    private ReviewActivityTestStub startReviewActivity(byte[] jpeg, int orientation) {
        Intent intent = getReviewActivityIntent();
        intent.putExtra(ReviewActivity.EXTRA_IN_DOCUMENT, Document.fromPhoto(Photo.fromJpeg(jpeg, orientation)));
        intent.putExtra(ReviewActivity.EXTRA_IN_ANALYSIS_ACTIVITY, new Intent(InstrumentationRegistry.getTargetContext(), AnalysisActivityTestStub.class));
        return mActivityTestRule.launchActivity(intent);
    }

    private Intent getReviewActivityIntent() {
        return new Intent(InstrumentationRegistry.getTargetContext(), ReviewActivityTestStub.class);
    }
}
