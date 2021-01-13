package net.gini.android.vision.analysis;

import static net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp;
import static net.gini.android.vision.tracking.EventTrackingHelper.trackAnalysisScreenEvent;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.network.GiniVisionNetworkApi;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.noresults.NoResultsActivity;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.review.ReviewActivity;
import net.gini.android.vision.tracking.AnalysisScreenEvent;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

/**
 * <h3>Screen API</h3>
 *
 * <p> When you use the Screen API, the {@code AnalysisActivity} displays the captured or imported
 * document and an activity indicator while the document is being analyzed by the Gini API.
 *
 * <p> <b>Note:</b> The title from the ActionBar was removed. Use the activity indicator message
 * instead by overriding the string resource named {@code gv_analysis_activity_indicator_message}. The message is displayed for images
 * only.
 *
 * <p> For PDF documents the first page is shown (only on Android 5.0 Lollipop and newer) along with
 * the PDF's filename and number of pages above the page. On Android KitKat and older only the PDF's filename is shown with the preview area
 * left empty.
 *
 * <p> Extending the {@code AnalysisActivity} in your application has been deprecated. The preferred
 * way of adding network calls to the Gini Vision Library is by creating a {@link GiniVision} instance with a {@link
 * GiniVisionNetworkService} and a {@link GiniVisionNetworkApi} implementation.
 *
 * <p> <b>Note:</b> When declaring your {@code AnalysisActivity} subclass in the {@code
 * AndroidManifest.xml} you should set the theme to the {@code GiniVisionTheme}. If you would like to use your own theme please consider
 * that {@code AnalysisActivity} extends {@link AppCompatActivity} and requires an AppCompat Theme.
 *
 * <p> The {@code AnalysisActivity} is started by the {@link CameraActivity} after the user has
 * reviewed the document and either made no changes to the document and it hasn't been analyzed before tapping the Next button, or the user
 * has modified the document, e.g. by rotating it.
 *
 * <p> For imported documents that cannot be reviewed, like PDFs, the {@link CameraActivity} starts
 * the {@code AnalysisActivity} directly.
 *
 * <p> If you didn't create {@link GiniVision} instance you have to implement the following methods
 * in your {@code AnalysisActivity} subclass:
 *
 * <ul>
 *
 * <li> {@link AnalysisActivity#onAnalyzeDocument(Document)} - start analyzing the document by
 * sending it to the Gini API.<br/><b>Note:</b> Call {@link AnalysisActivity#onDocumentAnalyzed()} when the analysis is done and the
 * Activity hasn't been stopped.<br/><b>Note:</b> If an analysis error message was set in the Review Screen with {@link
 * ReviewActivity#onDocumentAnalysisError(String)} this method won't be called until the user clicks the retry button next to the error
 * message.
 *
 * <li>{@link AnalysisActivity#onAddDataToResult(Intent)} - you should add the results of the
 * analysis to the Intent as extras and retrieve them once the {@link CameraActivity} returns.<br/>This is called only if you called {@link
 * AnalysisActivity#onDocumentAnalyzed()} before.<br/>When this is called, control is returned to your Activity which started the {@link
 * CameraActivity} and you can extract the results of the analysis.
 *
 * </ul>
 *
 * <p> You can also override the following method:
 *
 * <ul>
 *
 * <li>{@link AnalysisActivity#onBackPressed()} - called when the back or the up button was
 * clicked.
 *
 * </ul>
 *
 * <h3>Customizing the Analysis Screen</h3>
 *
 * Customizing the look of the Analysis Screen is done via overriding of app resources.
 *
 * <p> The following items are customizable:
 *
 * <ul>
 *
 * <li> <b>Activity indicator color:</b> via the color resource named {@code
 * gv_analysis_activity_indicator}
 *
 * <li> <b>Activity indicator message:</b> via the string resource named {@code
 * gv_analysis_activity_indicator_message}
 *
 * <li> <b>Activity indicator message text style:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.AnalysingMessage.TextStyle}
 *
 * <li> <b>Activity indicator message font:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.AnalysingMessage.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in
 * your {@code assets} folder
 *
 * <li> <b>PDF info panel background:</b> via the color resource named {@code
 * gv_analysis_pdf_info_background}
 *
 * <li> <b>PDF filename text style:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.PdfFilename.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or
 * {@code italic}
 *
 * <li> <b>PDF filename text size:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.PdfFilename.TextStyle} and setting an item named {@code autoSizeMaxTextSize} and {@code autoSizeMinTextSize} to
 * the desired maximum and minimum {@code sp} sizes
 *
 * <li> <b>PDF filename text color:</b> via the color resource named {@code
 * gv_analysis_pdf_info_text}
 *
 * <li> <b>PDF filename font:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.PdfFilename.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in your
 * {@code assets} folder
 *
 * <li> <b>PDF page count text style:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.PdfPageCount.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or
 * {@code italic}
 *
 * <li> <b>PDF page count text size:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.PdfPageCount.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *
 * <li> <b>PDF page count text color:</b> via the color resource named {@code
 * gv_analysis_pdf_info_text}
 *
 * <li> <b>PDF page count font:</b> via overriding the style named {@code
 * GiniVisionTheme.Analysis.PdfPageCount.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in your
 * {@code assets} folder
 *
 * <li> <b>Background color:</b> via the color resource named {@code gv_background}. <b>Note:</b>
 * this color resource is global to all Activities ({@link CameraActivity}, {@link OnboardingActivity}, {@link ReviewActivity}, {@link
 * AnalysisActivity})
 *
 * <li> <b>Error message text color:</b> via the color resource named {@code
 * gv_snackbar_error_text}
 *
 * <li> <b>Error message font:</b> via overriding the style named {@code
 * GiniVisionTheme.Snackbar.Error.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in your {@code
 * assets} folder
 *
 * <li> <b>Error message text style:</b> via overriding the style named {@code
 * GiniVisionTheme.Snackbar.Error.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or {@code
 * italic}
 *
 * <li> <b>Error message text size:</b> via overriding the style named {@code
 * GiniVisionTheme.Snackbar.Error.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *
 * <li> <b>Error message button text color:</b> via the color resource named {@code
 * gv_snackbar_error_button_title} and {@code gv_snackbar_error_button_title_pressed}
 *
 * <li> <b>Error message button font:</b> via overriding the style named {@code
 * GiniVisionTheme.Snackbar.Error.Button.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in your
 * {@code assets} folder
 *
 * <li> <b>Error message button text style:</b> via overriding the style named {@code
 * GiniVisionTheme.Snackbar.Error.Button.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or
 * {@code italic}
 *
 * <li> <b>Error message button text size:</b> via overriding the style named {@code
 * GiniVisionTheme.Snackbar.Error.Button.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *
 * <li> <b>Error message background color:</b> via the color resource named {@code
 * gv_snackbar_error_background}
 *
 * <li> <b>Document analysis error message retry button text:</b> via the string resource named
 * {@code gv_document_analysis_error_retry}
 *
 * </ul>
 *
 * <p> <b>Important:</b> All overriden styles must have their respective {@code Root.} prefixed
 * style as their parent. Ex.: the parent of {@code GiniVisionTheme.Snackbar.Error.TextStyle} must be {@code
 * Root.GiniVisionTheme.Snackbar.Error.TextStyle}.
 *
 * <h3>Customizing the Action Bar</h3>
 *
 * <p> Customizing the Action Bar is also done via overriding of app resources and each one - except
 * the title string resource - is global to all Activities ({@link CameraActivity}, {@link OnboardingActivity}, {@link ReviewActivity},
 * {@link net.gini.android.vision.review.multipage.MultiPageReviewActivity}, {@link AnalysisActivity}).
 *
 * <p> The following items are customizable:
 *
 * <ul>
 *
 * <li> <b>Background color:</b> via the color resource named {@code gv_action_bar} (highly
 * recommended for Android 5+: customize the status bar color via {@code gv_status_bar})
 *
 * <li> <b>Back button (only for {@link ReviewActivity} and {@link AnalysisActivity}):</b> via
 * images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_action_bar_back}
 *
 * </ul>
 */
public class AnalysisActivity extends AppCompatActivity implements
        AnalysisFragmentListener, AnalysisFragmentInterface {

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final String EXTRA_IN_DOCUMENT = "GV_EXTRA_IN_DOCUMENT";
    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final String EXTRA_IN_DOCUMENT_ANALYSIS_ERROR_MESSAGE =
            "GV_EXTRA_IN_DOCUMENT_ANALYSIS_ERROR_MESSAGE";
    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final int RESULT_NO_EXTRACTIONS = RESULT_FIRST_USER + 2;

    private static final String ANALYSIS_FRAGMENT = "ANALYSIS_FRAGMENT";

    private String mAnalysisErrorMessage;
    private Document mDocument;
    private AnalysisFragmentCompat mFragment;

    @Override
    public void hideError() {
        mFragment.hideError();
    }

    /**
     * <p> You should call this method after you've received the analysis results from the Gini API
     * without the required extractions. </p> <p> It will launch the {@link NoResultsActivity}, if the {@link Document}'s type is {@link
     * Document.Type#IMAGE}. For other types it will just finish the {@code AnalysisActivity} with {@code RESULT_OK}. </p>
     *
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed internally by using the configured {@link
     * GiniVisionNetworkService} implementation.
     */
    @Deprecated
    @Override
    public void onNoExtractionsFound() {
        if (GiniVisionCoordinator.shouldShowGiniVisionNoResultsScreen(mDocument)) {
            final Intent noResultsActivity = new Intent(this, NoResultsActivity.class);
            noResultsActivity.putExtra(NoResultsActivity.EXTRA_IN_DOCUMENT, mDocument);
            noResultsActivity.setExtrasClassLoader(AnalysisActivity.class.getClassLoader());
            startActivity(noResultsActivity);
            setResult(RESULT_NO_EXTRACTIONS);
        } else {
            final Intent result = new Intent();
            setResult(RESULT_OK, result);
        }
        finish();
    }

    /**
     * <p> <b>Screen API:</b> If an analysis error message was set in the Review Screen with {@link
     * ReviewActivity#onDocumentAnalysisError(String)} this method won't be called until the user clicks the retry button next to the error
     * message. </p>
     *
     * @param document contains the image taken by the camera (original or modified)
     *
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed internally by using the configured {@link
     * GiniVisionNetworkService} implementation. The extractions will be returned in the extra called {@link
     * CameraActivity#EXTRA_OUT_EXTRACTIONS} of the {@link CameraActivity}'s result Intent.
     */
    @Deprecated
    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_analysis);
        setTitle("");
        readExtras();
        if (savedInstanceState == null) {
            initFragment();
        } else {
            retainFragment();
        }
        enableHomeAsUp(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

    /**
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed internally by using the configured {@link
     * GiniVisionNetworkService} implementation. The extractions will be returned in the extra called {@link
     * CameraActivity#EXTRA_OUT_EXTRACTIONS} of the {@link CameraActivity}'s result Intent.
     */
    @Deprecated
    @Override
    public void onDocumentAnalyzed() {
        mFragment.onDocumentAnalyzed();
        final Intent result = new Intent();
        onAddDataToResult(result);
        setResult(RESULT_OK, result);
        finish();
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        final Intent result = new Intent();
        result.putExtra(EXTRA_OUT_ERROR, error);
        setResult(RESULT_ERROR, result);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        trackAnalysisScreenEvent(AnalysisScreenEvent.CANCEL);
    }

    @Override
    public void showError(@NonNull final String message, @NonNull final String buttonTitle,
            @NonNull final View.OnClickListener onClickListener) {
        mFragment.showError(message, buttonTitle, onClickListener);
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        mFragment.showError(message, duration);
    }

    /**
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed internally by using the configured {@link
     * GiniVisionNetworkService} implementation. The extractions will be returned in the extra called {@link
     * CameraActivity#EXTRA_OUT_EXTRACTIONS} of the {@link CameraActivity}'s result Intent.
     */
    @Deprecated
    @Override
    public void startScanAnimation() {
        mFragment.startScanAnimation();
    }

    /**
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed internally by using the configured {@link
     * GiniVisionNetworkService} implementation. The extractions will be returned in the extra called {@link
     * CameraActivity#EXTRA_OUT_EXTRACTIONS} of the {@link CameraActivity}'s result Intent.
     */
    @Deprecated
    @Override
    public void stopScanAnimation() {
        mFragment.stopScanAnimation();
    }

    /**
     * <p> Callback for adding your own data to the Activity's result. </p> <p> Called when the
     * document was analyzed. </p> <p> You should add the results of the analysis as extras and retrieve them when the {@link
     * CameraActivity} returned. </p> <p> <b>Note:</b> you must call {@link AnalysisActivity#onDocumentAnalyzed()} after you received the
     * analysis results from the Gini API, otherwise this method won't be invoked. </p>
     *
     * @param result the {@link Intent} which will be returned as the result data.
     *
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed internally by using the configured {@link
     * GiniVisionNetworkService} implementation. The extractions will be returned in the extra called {@link
     * CameraActivity#EXTRA_OUT_EXTRACTIONS} of the {@link CameraActivity}'s result Intent.
     */
    @Deprecated
    public void onAddDataToResult(final Intent result) {
    }

    @VisibleForTesting
    AnalysisFragmentCompat getFragment() {
        return mFragment;
    }

    private void checkRequiredExtras() {
        if (mDocument == null) {
            throw new IllegalStateException(
                    "AnalysisActivity requires a Document. Set it as an extra using the "
                            + "EXTRA_IN_DOCUMENT key.");
        }
    }

    private void clearMemory() {
        mDocument = null; // NOPMD
    }

    private void createFragment() {
        mFragment = AnalysisFragmentCompat.createInstance(mDocument, mAnalysisErrorMessage);
    }

    private void initFragment() {
        if (!isFragmentShown()) {
            createFragment();
            showFragment();
        }
    }

    private boolean isFragmentShown() {
        return getSupportFragmentManager().findFragmentByTag(ANALYSIS_FRAGMENT) != null;
    }

    private void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDocument = extras.getParcelable(EXTRA_IN_DOCUMENT);
            mAnalysisErrorMessage = extras.getString(EXTRA_IN_DOCUMENT_ANALYSIS_ERROR_MESSAGE);
        }
        checkRequiredExtras();
    }

    private void retainFragment() {
        mFragment = (AnalysisFragmentCompat) getSupportFragmentManager().findFragmentByTag(
                ANALYSIS_FRAGMENT);
    }

    private void showFragment() {
        getSupportFragmentManager().beginTransaction().add(R.id.gv_fragment_analyze_document,
                mFragment, ANALYSIS_FRAGMENT).commit();
    }

    @Override
    public void setListener(@NonNull final AnalysisFragmentListener listener) {
        throw new IllegalStateException("AnalysisFragmentListener must not be altered in the "
                + "AnalysisActivity. Override listener methods in an AnalysisActivity subclass "
                + "instead.");
    }

    @Override
    public void onExtractionsAvailable(
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        final Intent result = new Intent();
        final Bundle extractionsBundle = new Bundle();
        for (final Map.Entry<String, GiniVisionSpecificExtraction> extraction
                : extractions.entrySet()) {
            extractionsBundle.putParcelable(extraction.getKey(), extraction.getValue());
        }
        result.putExtra(CameraActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
        setResult(RESULT_OK, result);
        finish();
        clearMemory();
    }

    @Override
    public void onProceedToNoExtractionsScreen(@NonNull final Document document) {
        if (GiniVisionCoordinator.shouldShowGiniVisionNoResultsScreen(mDocument)) {
            final Intent noResultsActivity = new Intent(this, NoResultsActivity.class);
            noResultsActivity.putExtra(NoResultsActivity.EXTRA_IN_DOCUMENT, mDocument);
            noResultsActivity.setExtrasClassLoader(AnalysisActivity.class.getClassLoader());
            startActivity(noResultsActivity);
            setResult(RESULT_NO_EXTRACTIONS);
        } else {
            final Intent result = new Intent();
            setResult(RESULT_OK, result);
        }
        finish();
    }

    @Override
    public void onDefaultPDFAppAlertDialogCancelled() {
        finish();
    }
}
