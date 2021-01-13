package net.gini.android.vision.analysis;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.analysis.BitmapMatcher.withBitmap;
import static net.gini.android.vision.analysis.RotationMatcher.withRotation;
import static net.gini.android.vision.test.BackgroundColorMatcher.hasBackgroundColor;
import static net.gini.android.vision.test.Helpers.getTestJpeg;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.View;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.util.Size;
import net.gini.android.vision.test.FragmentImplFactory;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowViewGroup;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(AndroidJUnit4.class)
@Config(shadows = {
        AnalysisFragmentImplTest.DialogShadow.class,
        AnalysisFragmentImplTest.AnalysisHintsAnimatorShadow.class,
        AnalysisFragmentImplTest.ErrorSnackbarShadow.class
})
public class AnalysisFragmentImplTest {

    @After
    public void tearDown() throws Exception {
        AnalysisFragmentCompatFake.sFragmentImplFactory = null;
        DialogShadow.cleanup();
        AnalysisHintsAnimatorShadow.cleanup();
        ErrorSnackbarShadow.cleanup();
    }

    @Test
    public void should_forwardAnalysisFragmentInterfaceMethods_toPresenter() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = mock(AnalysisScreenPresenter.class);
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                presenter, analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.hideError();
                            analysisFragment.onNoExtractionsFound();
                            analysisFragment.onDocumentAnalyzed();
                            analysisFragment.showError("Message", 1000);
                            analysisFragment.showError("Message", "ButtonTitle",
                                    mock(View.OnClickListener.class));
                            analysisFragment.startScanAnimation();
                            analysisFragment.stopScanAnimation();
                            analysisFragment.setListener(mock(AnalysisFragmentListener.class));
                        }
                    });

            // Then
            verify(presenter).hideError();
            verify(presenter).onNoExtractionsFound();
            verify(presenter).onDocumentAnalyzed();
            verify(presenter).showError(anyString(), anyInt());
            verify(presenter).showError(anyString(), anyString(), any(View.OnClickListener.class));
            verify(presenter).startScanAnimation();
            verify(presenter).stopScanAnimation();
            verify(presenter).setListener(any(AnalysisFragmentListener.class));
        }
    }

    @NonNull
    private ActivityScenario<AnalysisFragmentHostActivity> launchHostActivity(
            @NonNull final AnalysisScreenPresenter presenter,
            @NonNull final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef) {
        AnalysisFragmentCompatFake.sFragmentImplFactory =
                new FragmentImplFactory<AnalysisFragmentImpl, AnalysisFragmentCompat>() {
                    @NonNull
                    @Override
                    public AnalysisFragmentImpl createFragmentImpl(
                            @NonNull final AnalysisFragmentCompat fragment) {
                        final Document document = DocumentFactory.newEmptyImageDocument(
                                Document.Source.newCameraSource(), Document.ImportMethod.NONE);
                        final AnalysisFragmentImpl analysisFragmentImpl = new AnalysisFragmentImpl(
                                fragment,
                                document, null) {

                            @Override
                            void createPresenter(@NonNull final Activity activity,
                                    @NonNull final Document document,
                                    final String documentAnalysisErrorMessage) {
                                setPresenter(presenter);
                            }

                        };
                        analysisFragmentImplRef.set(analysisFragmentImpl);
                        return analysisFragmentImpl;
                    }
                };
        return ActivityScenario.launch(
                AnalysisFragmentHostActivity.class);
    }

    @Test
    public void should_notShowScanAnimation_byDefault() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {
            // Then
            onView(withId(R.id.gv_progress_activity)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
            onView(withId(R.id.gv_analysis_message)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }
    }

    private ActivityScenario<AnalysisFragmentHostActivity> launchHostActivity(
            @NonNull final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef) {
        final AnalysisScreenPresenter presenter = mock(AnalysisScreenPresenter.class);
        return launchHostActivity(presenter, analysisFragmentImplRef);
    }

    @Test
    public void should_showScanAnimation_whenRequested() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showScanAnimation();
                        }
                    });

            // Then
            onView(withId(R.id.gv_progress_activity)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.gv_analysis_message)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        }
    }

    @Test
    public void should_hideScanAnimation_whenRequested() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showScanAnimation();
                            analysisFragment.hideScanAnimation();
                        }
                    });

            // Then
            onView(withId(R.id.gv_progress_activity)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
            onView(withId(R.id.gv_analysis_message)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }
    }

    @Test
    public void should_waitForViewLayout_whenRequested() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        final AtomicReference<CompletableFuture<Void>> futureRef;
        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            futureRef = new AtomicReference<>();

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            final CompletableFuture<Void> future =
                                    analysisFragment.waitForViewLayout();
                            futureRef.set(future);
                        }
                    });
        }

        // Then
        final CompletableFuture<Void> future = futureRef.get();
        assertThat(future).isNotNull();
        assertThat(future.isDone()).isTrue();
    }

    @Test
    public void should_showPdfInfoPanel() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showPdfInfoPanel();
                        }
                    });

            // Then
            onView(withId(R.id.gv_pdf_info)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
            onView(withId(R.id.gv_analysis_overlay)).check(
                    matches(hasBackgroundColor(Color.TRANSPARENT)));
            onView(withId(R.id.gv_analysis_message)).check(matches(withText("")));
        }
    }

    @Test
    public void should_showPdfTitle() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showPdfInfoPanel();
                            analysisFragment.showPdfTitle("PdfTitle");
                        }
                    });

            // Then
            onView(withId(R.id.gv_pdf_filename)).check(matches(
                    withText("PdfTitle")));
        }
    }

    @Test
    public void should_showPdfPageCount() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showPdfInfoPanel();
                            analysisFragment.showPdfPageCount("42");
                        }
                    });

            // Then
            onView(withId(R.id.gv_pdf_page_count)).check(matches(
                    withText("42")));
        }
    }

    @Test
    public void should_hidePdfPageCount() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showPdfInfoPanel();
                            analysisFragment.showPdfPageCount("42");
                            analysisFragment.hidePdfPageCount();
                        }
                    });

            // Then
            onView(withId(R.id.gv_pdf_page_count)).check(matches(
                    withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        }
    }

    @Test
    public void should_getSize_forPdfPreview() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        final AtomicReference<Size> pdfPreviewSize;
        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            pdfPreviewSize = new AtomicReference<>();

            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            pdfPreviewSize.set(analysisFragment.getPdfPreviewSize());
                        }
                    });

            // Then
            assertThat(pdfPreviewSize.get()).isNotNull();
            assertThat(pdfPreviewSize.get().height).isGreaterThan(0);
            assertThat(pdfPreviewSize.get().width).isGreaterThan(0);
        }
    }

    @Test
    public void should_showBitmap() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        final Bitmap bitmap;
        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            final Photo photo = PhotoFactory.newPhotoFromJpeg(getTestJpeg(), 0, "portrait", "phone",
                    ImageDocument.Source.newCameraSource());
            bitmap = photo.getBitmapPreview();

            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showBitmap(bitmap, 0);
                        }
                    });

            // Then
            onView(withId(R.id.gv_image_picture)).check(matches(withBitmap(bitmap)));
        }
    }

    @Test
    public void should_rotateImageView_forBitmap_accordingToRotation() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            final Photo photo = PhotoFactory.newPhotoFromJpeg(getTestJpeg(), 0, "portrait", "phone",
                    ImageDocument.Source.newCameraSource());
            final Bitmap bitmap = photo.getBitmapPreview();

            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showBitmap(bitmap, 180);
                        }
                    });

            // Then
            onView(withId(R.id.gv_image_picture)).check(matches(withRotation(180)));
        }
    }

    @Test
    public void should_showAlertDialog() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showAlertDialog("Message",
                                    "PositiveButtonTitle",
                                    mock(DialogInterface.OnClickListener.class),
                                    "NegativeButtonTitle",
                                    mock(DialogInterface.OnClickListener.class),
                                    mock(DialogInterface.OnCancelListener.class));
                        }
                    });

            // Then
            assertThat(DialogShadow.showCalled).isTrue();
        }
    }

    @Test
    public void should_showErrorSnackbar() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showErrorSnackbar("Message", 1000,
                                    "ButtonTitle", mock(View.OnClickListener.class));
                        }
                    });

            // Then
            onView(withText("Message")).check(matches(isCompletelyDisplayed()));
            onView(withText("ButtonTitle")).check(matches(isCompletelyDisplayed()));
        }
    }

    @Test
    public void should_hideErrorSnackbar() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showErrorSnackbar("Message", 1000,
                                    "ButtonTitle", mock(View.OnClickListener.class));
                            analysisFragment.hideErrorSnackbar();
                        }
                    });

            // Then
            assertThat(ErrorSnackbarShadow.hideCalled).isTrue();
        }
    }

    @Test
    public void should_showHints() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        final List<AnalysisHint> hints;
        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            hints = AnalysisHint.getArray();
            scenario.onActivity(
                    new ActivityScenario.ActivityAction<AnalysisFragmentHostActivity>() {
                        @Override
                        public void perform(final AnalysisFragmentHostActivity activity) {
                            final AnalysisFragmentImpl analysisFragment =
                                    analysisFragmentImplRef.get();
                            analysisFragment.showHints(hints);
                        }
                    });

            // Then
            assertThat(AnalysisHintsAnimatorShadow.startCalled).isTrue();
            assertThat(AnalysisHintsAnimatorShadow.hints).containsExactlyElementsIn(
                    hints);
        }
    }

    @Test
    public void should_startPresenter_onStart() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = mock(AnalysisScreenPresenter.class);
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                presenter, analysisFragmentImplRef)) {

            // Then
            verify(presenter).start();
        }
    }

    @Test
    public void should_stopHintsAnimatior_onStop() throws Exception {
        // Given
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                analysisFragmentImplRef)) {

            // When
            scenario.moveToState(Lifecycle.State.CREATED);

            // Then
            assertThat(AnalysisHintsAnimatorShadow.stopCalled).isTrue();
        }
    }

    @Test
    public void shold_stopPresenter_onDestroy() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = mock(AnalysisScreenPresenter.class);
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                presenter, analysisFragmentImplRef)) {

            // When
            scenario.moveToState(Lifecycle.State.DESTROYED);

            // Then
            verify(presenter).stop();
        }
    }

    @Test
    public void should_finishPresenter_onDestroy_whenActivity_isFinishing() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = mock(AnalysisScreenPresenter.class);
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                presenter, analysisFragmentImplRef)) {

            // When
            scenario.moveToState(Lifecycle.State.DESTROYED);

            // Then
            verify(presenter).finish();
        }
    }

    @Test
    public void should_notFinishPresenter_onDestroy_whenActivity_isNotFinishing() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = mock(AnalysisScreenPresenter.class);
        final AtomicReference<AnalysisFragmentImpl> analysisFragmentImplRef =
                new AtomicReference<>();

        try (final ActivityScenario<AnalysisFragmentHostActivity> scenario = launchHostActivity(
                presenter, analysisFragmentImplRef)) {

            // When
            scenario.recreate();

            // Then
            verify(presenter, never()).finish();
        }
    }

    @Implements(AnalysisHintsAnimator.class)
    public static class AnalysisHintsAnimatorShadow {

        static boolean startCalled;
        static List<AnalysisHint> hints;
        static boolean stopCalled;

        static void cleanup() {
            startCalled = false;
            stopCalled = false;
            hints = null;
        }

        @Implementation
        public void start() {
            startCalled = true;
        }

        public void setHints(final List<AnalysisHint> hints) {
            this.hints = hints;
        }

        public void stop() {
            stopCalled = true;
        }
    }

    @Implements(Dialog.class)
    public static class DialogShadow {

        static boolean showCalled;

        static void cleanup() {
            showCalled = false;
        }

        @Implementation
        public void show() {
            showCalled = true;
        }
    }

    @Implements(ErrorSnackbar.class)
    public static class ErrorSnackbarShadow extends ShadowViewGroup {

        static boolean hideCalled;

        static void cleanup() {
            hideCalled = false;
        }

        @Implementation
        public void hide() {
            hideCalled = true;
        }
    }
}
