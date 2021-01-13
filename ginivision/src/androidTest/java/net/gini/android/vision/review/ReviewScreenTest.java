package net.gini.android.vision.review;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.DocumentSubject.document;
import static net.gini.android.vision.test.Helpers.createDocument;
import static net.gini.android.vision.test.Helpers.getTestJpeg;
import static net.gini.android.vision.test.Helpers.isTablet;
import static net.gini.android.vision.test.Helpers.prepareLooper;
import static net.gini.android.vision.test.Helpers.resetDeviceOrientation;
import static net.gini.android.vision.test.Helpers.waitForWindowUpdate;

import static org.junit.Assume.assumeTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.content.Intent;
import android.view.Surface;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivityTestSpy;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.test.CurrentActivityTestRule;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.InstrumentationRegistry;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

@RunWith(AndroidJUnit4.class)
public class ReviewScreenTest {

    private static final int PAUSE_DURATION = 500;
    private static final int PAUSE_DURATION_LONG = 2_000;

    @Rule
    public CurrentActivityTestRule<ReviewActivityTestSpy> mActivityTestRule =
            new CurrentActivityTestRule<>(ReviewActivityTestSpy.class, true, false);
    @Rule
    public ActivityTestRule<ReviewFragmentHostActivityNotListener>
            mReviewFragmentHostActivityNotListenerTR = new ActivityTestRule<>(
            ReviewFragmentHostActivityNotListener.class, true, false);
    @Rule
    public ActivityTestRule<ReviewFragmentHostActivity>
            mReviewFragmentHostActivityTR = new ActivityTestRule<>(
            ReviewFragmentHostActivity.class, true, false);

    private static byte[] TEST_JPEG = null;

    @BeforeClass
    public static void setupClass() throws IOException {
        TEST_JPEG = getTestJpeg();
    }

    @AfterClass
    public static void teardownClass() throws IOException {
        TEST_JPEG = null;
    }

    @After
    public void tearDown() throws Exception {
        resetDeviceOrientation();
        ReviewFragmentHostActivityNotListener.sListener = null;
    }

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenAnalysisActivityClass_wasNotGiven() {
        prepareLooper();
        final ReviewActivityTestSpy reviewActivity = new ReviewActivityTestSpy();

        final Intent intent = new Intent(Intent.ACTION_MAIN);
        reviewActivity.setIntent(intent);

        reviewActivity.readExtras();
    }

    @Test
    public void should_rotatePreview_accordingToOrientation()
            throws IOException, InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 180);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // Give some time for the activity to settle
        Thread.sleep(PAUSE_DURATION);

        assertThat(
                activity.getFragment().getFragmentImpl().getImageDocument().getRotation()).isWithin(
                0.0f).of(180);
    }

    private ReviewActivityTestSpy startReviewActivity(final byte[] jpeg, final int orientation) {
        return startReviewActivity(jpeg, orientation, ImageDocument.Source.newCameraSource());
    }

    private ReviewActivityTestSpy startReviewActivity(final byte[] jpeg, final int orientation, @NonNull final ImageDocument.Source source) {
        final Intent intent = getReviewActivityIntent(jpeg, orientation, source);
        return mActivityTestRule.launchActivity(intent);
    }

    private Intent getReviewActivityIntent(final byte[] jpeg, final int orientation) {
        return getReviewActivityIntent(jpeg, orientation, ImageDocument.Source.newCameraSource());
    }

    private Intent getReviewActivityIntent(final byte[] jpeg, final int orientation, @NonNull final ImageDocument.Source source) {
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                ReviewActivityTestSpy.class);
        intent.putExtra(ReviewActivity.EXTRA_IN_DOCUMENT,
                createDocument(jpeg, orientation, "portrait", "phone", source));
        intent.putExtra(ReviewActivity.EXTRA_IN_ANALYSIS_ACTIVITY,
                new Intent(ApplicationProvider.getApplicationContext(),
                        AnalysisActivityTestSpy.class));
        return intent;
    }

    @Test
    public void should_rotatePreview_whenRotateButton_isClicked()
            throws IOException, InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 90);

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        // Give some time for the rotation animation to finish
        Thread.sleep(PAUSE_DURATION);

        assertThat(
                activity.getFragment().getFragmentImpl().getImageDocument().getRotation()).isWithin(
                0.0f).of(180);
    }

    @Test
    public void should_invokeAnalyzeDocument_whenLaunched()
            throws IOException, InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 90);

        final AtomicBoolean analyzeDocumentInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                analyzeDocumentInvoked.set(true);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION_LONG);

        assertThat(analyzeDocumentInvoked.get()).isTrue();
    }

    @Test
    public void should_compressJpeg_beforeAnalyzeDocument_isInvoked_forExternalImages()
            throws IOException, InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 90, ImageDocument.Source.newExternalSource());

        final AtomicReference<Document> documentToAnalyze = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                documentToAnalyze.set(document);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION_LONG);

        assertThat(documentToAnalyze.get()).isNotNull();
        assertThat(documentToAnalyze.get().getJpeg().length).isLessThan(TEST_JPEG.length);
    }

    @Test
    public void should_NotCompressJpeg_beforeAnalyzeDocument_isInvoked_forCameraImages()
            throws IOException, InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 90);

        final AtomicReference<Document> documentToAnalyze = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                documentToAnalyze.set(document);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION_LONG);

        assertThat(documentToAnalyze.get()).isNotNull();
        assertThat(documentToAnalyze.get().getJpeg().length).isEqualTo(TEST_JPEG.length);
    }

    @Test
    public void should_onlyInvokeProceedToAnalysis_whenNextButton_wasClicked_ifDocument_wasModified_andNotAnalyzed()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 0);

        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        final AtomicBoolean proceedToAnalysisInvoked = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onAddDataToResult(@NonNull final Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {
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
    @SdkSuppress(minSdkVersion = 18)
    public void should_onlyInvokeProceedToAnalysis_whenNextButton_wasClicked_ifDocument_wasModified_andAnalyzed()
            throws Exception {
        final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.setOrientationNatural();
        waitForWindowUpdate(uiDevice);

        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 0);

        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        final AtomicBoolean proceedToAnalysisInvoked = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                // Notify that document was analyzed
                activity.onDocumentAnalyzed();
            }

            @Override
            public void onAddDataToResult(@NonNull final Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {
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
    public void should_onlyInvokeProceedToAnalysis_whenNextButton_wasClicked_ifDocument_wasNotModified_andNotAnalyzed()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 0);

        final AtomicBoolean proceedToAnalysisInvoked = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onAddDataToResult(@NonNull final Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {
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
    public void should_invokeDocumentReviewed_andAddDataToResult_whenNextButton_wasClicked_ifDocument_wasNotModified_andWasAnalyzed()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 0);

        final AtomicBoolean documentReviewedCalled = new AtomicBoolean();
        final AtomicBoolean addDataToResultInvoked = new AtomicBoolean();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                // Notify that document was analyzed
                activity.onDocumentAnalyzed();
            }

            @Override
            public void onAddDataToResult(@NonNull final Intent result) {
                addDataToResultInvoked.set(true);
            }

            @Override
            public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {
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
    public void should_notInvokeAnyListenerMethods_whenHomeButton_wasClicked()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 0);

        final ReviewActivityTestSpy.ListenerHook listenerHook = mock(
                ReviewActivityTestSpy.ListenerHook.class);

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
    @SdkSuppress(minSdkVersion = 18)
    public void should_notInvokeAnyListenerMethods_whenBackButton_wasClicked()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 0);

        final ReviewActivityTestSpy.ListenerHook listenerHook = mock(
                ReviewActivityTestSpy.ListenerHook.class);

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
    public void should_invokeDocumentWasRotated_whenRotateButton_wasClicked()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 0);

        final ReviewActivityTestSpy.ListenerHook listenerHook = mock(
                ReviewActivityTestSpy.ListenerHook.class);

        activity.setListenerHook(listenerHook);

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        verify(listenerHook).onDocumentWasRotated(any(Document.class), eq(0), eq(90));
    }

    @Test
    public void should_notFinish_whenReceivingActivityResult_withResultCodeCancelled_fromAnalysisActivity() {
        prepareLooper();

        final ReviewActivity reviewActivitySpy = Mockito.spy(new ReviewActivityTestSpy());

        reviewActivitySpy.onActivityResult(ReviewActivity.ANALYSE_DOCUMENT_REQUEST,
                Activity.RESULT_CANCELED, new Intent());

        verify(reviewActivitySpy, never()).finish();
    }

    @Test
    public void should_finishIfEnabledByClient_whenReceivingActivityResult_withResultCodeCancelled_fromAnalysisActivity() {
        prepareLooper();

        final Intent intentAllowBackButtonToClose = getReviewActivityIntent(TEST_JPEG, 0);
        intentAllowBackButtonToClose.putExtra(
                ReviewActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, true);

        final ReviewActivity reviewActivity = new ReviewActivityTestSpy();
        reviewActivity.setIntent(intentAllowBackButtonToClose);
        reviewActivity.readExtras();

        final ReviewActivity reviewActivitySpy = Mockito.spy(reviewActivity);

        reviewActivitySpy.onActivityResult(ReviewActivity.ANALYSE_DOCUMENT_REQUEST,
                Activity.RESULT_CANCELED, new Intent());

        verify(reviewActivitySpy).finish();
    }

    @Test
    public void should_returnDocuments_withSameContentId_inAnalyzeDocument_andProceedToAnalysis()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 90);

        final AtomicReference<Document> documentToAnalyze = new AtomicReference<>();
        final AtomicReference<Document> documentToProceedWith = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                documentToAnalyze.set(document);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {
                documentToProceedWith.set(document);
            }
        });

        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        assertAbout(document()).that(documentToAnalyze.get()).hasSameContentIdInUserCommentAs(
                documentToProceedWith.get());
    }

    @Test
    public void should_returnDocument_withZeroRotationDelta_inAnalyzeDocument()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 180);

        final AtomicReference<Document> documentToAnalyze = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                documentToAnalyze.set(document);
            }
        });

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        assertAbout(document()).that(documentToAnalyze.get()).hasRotationDeltaInUserComment(0);
    }

    @Test
    public void should_returnDocument_withNonZeroRotationDelta_inProceedToAnalysis_ifDocumentWasRotated()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 270);

        final AtomicReference<Document> documentToProceedWith = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {
                documentToProceedWith.set(document);
            }
        });

        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        assertAbout(document()).that(documentToProceedWith.get()).hasRotationDeltaInUserComment(90);
    }

    @Test
    public void should_returnDocument_withCumulatedRotationDelta_inProceedToAnalysis_ifDocumentWasRotatedMultipleTimes()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 180);

        final AtomicReference<Document> documentToProceedWith = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {
                documentToProceedWith.set(document);
            }
        });

        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());
        // Modify the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click());

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        assertAbout(document()).that(documentToProceedWith.get()).hasRotationDeltaInUserComment(
                180);
    }

    @Test
    public void should_returnDocument_withNormalizedRotationDelta_inProceedToAnalysis_ifDocumentWasRotatedBeyond360Degrees()
            throws InterruptedException {
        final ReviewActivityTestSpy activity = startReviewActivity(TEST_JPEG, 90);

        final AtomicReference<Document> documentToProceedWith = new AtomicReference<>();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {
                documentToProceedWith.set(document);
            }
        });

        // Rotate the document 5 times
        for (int i = 0; i < 5; i++) {
            Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                    .perform(ViewActions.click());
        }

        // Click next
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Allow the activity to run a little for listeners to be invoked
        Thread.sleep(PAUSE_DURATION);

        assertAbout(document()).that(documentToProceedWith.get()).hasRotationDeltaInUserComment(90);
    }

    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void should_keepAppliedRotation_betweenOrientationChange() throws Exception {
        // Given
        assumeTrue(isTablet());

        final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.setOrientationNatural();
        waitForWindowUpdate(uiDevice);

        startReviewActivity(TEST_JPEG, 90);

        // Rotate the document
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_rotate))
                .perform(ViewActions.click())
                .perform(ViewActions.click());

        // Give some time for the rotation animation to finish
        Thread.sleep(PAUSE_DURATION);

        // When
        uiDevice.setOrientationRight();
        waitForWindowUpdate(uiDevice);


        // Then
        final AtomicReference<Document> documentToAnalyzeAfterOrientationChange =
                new AtomicReference<>();

        final ReviewActivityTestSpy activity = mActivityTestRule.getCurrentActivity();

        activity.setListenerHook(new ReviewActivityTestSpy.ListenerHook() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                documentToAnalyzeAfterOrientationChange.set(document);
            }
        });

        assertAbout(document()).that(
                documentToAnalyzeAfterOrientationChange.get()).hasRotationDeltaInUserComment(180);
        assertThat(documentToAnalyzeAfterOrientationChange.get().getRotationForDisplay()).isEqualTo(
                270);
        assertThat(
                activity.getFragment().getFragmentImpl().getImageDocument().getRotation()).isWithin(
                0.0f).of(270);
    }

    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void should_forcePortraitOrientation_onPhones() throws Exception {
        // Given
        assumeTrue(!isTablet());

        final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.setOrientationLeft();
        waitForWindowUpdate(uiDevice);

        final ReviewActivity reviewActivity = startReviewActivity(TEST_JPEG, 90);
        waitForWindowUpdate(uiDevice);

        // Then
        final int rotation = reviewActivity.getWindowManager().getDefaultDisplay().getRotation();
        assertThat(rotation)
                .isEqualTo(Surface.ROTATION_0);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenListener_wasNotSet() throws Exception {
        final ReviewFragmentCompat fragment = ReviewFragmentCompat.createInstance(
                createDocument(getTestJpeg(), 0, "portrait", "phone", ImageDocument.Source.newCameraSource()));
        fragment.onCreate(null);
    }

    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void should_useExplicitListener_whenActivity_isNotListener() throws Exception {
        // Given
        final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.setOrientationNatural();
        waitForWindowUpdate(uiDevice);

        final AtomicBoolean shouldAnalyzeDoc = new AtomicBoolean();
        ReviewFragmentHostActivityNotListener.sListener = new ReviewFragmentListener() {
            @Override
            public void onShouldAnalyzeDocument(@NonNull final Document document) {
                shouldAnalyzeDoc.set(true);
            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document) {

            }

            @Override
            public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {

            }

            @Override
            public void onDocumentWasRotated(@NonNull final Document document,
                    final int oldRotation, final int newRotation) {
            }

            @Override
            public void onError(@NonNull final GiniVisionError error) {

            }

            @Override
            public void onExtractionsAvailable(
                    @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {

            }

            @Override
            public void onProceedToNoExtractionsScreen(@NonNull final Document document) {

            }

            @Override
            public void onProceedToAnalysisScreen(@NonNull final Document document,
                    @Nullable final String errorMessage) {

            }
        };
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                ReviewFragmentHostActivityNotListener.class);
                mReviewFragmentHostActivityNotListenerTR.launchActivity(intent);
        // Wait for the activity to start
        Thread.sleep(PAUSE_DURATION);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        // Then
        assertThat(shouldAnalyzeDoc.get()).isTrue();
    }

    @Test
    public void should_useActivity_asListener_whenAvailable() throws Exception {
        // Given
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                ReviewFragmentHostActivity.class);
        final ReviewFragmentHostActivity activity =
                mReviewFragmentHostActivityTR.launchActivity(intent);
        // When
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getFragment().mFragmentImpl.mButtonRotate.performClick();
            }
        });
        // Give some time for the rotation animation to finish
        Thread.sleep(PAUSE_DURATION);
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        // Then
        assertThat(activity.shouldAnalyzeDocument()).isTrue();
    }
}
