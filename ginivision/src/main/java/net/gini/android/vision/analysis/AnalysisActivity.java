package net.gini.android.vision.analysis;

import static net.gini.android.vision.util.ActivityHelper.enableHomeAsUp;
import static net.gini.android.vision.util.ActivityHelper.handleMenuItemPressedForHomeButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.review.ReviewActivity;

/**
 * <h3>Screen API</h3>
 *
 * <p>
 *     When you use the Screen API, the {@code AnalysisActivity} displays the captured document and an activity indicator while the document is being analyzed by the Gini API.
 * </p>
 * <p>
 *     You must extend the {@code AnalysisActivity} in your application and provide it to the {@link CameraActivity} by using the {@link CameraActivity#setAnalysisActivityExtra(Intent, Context, Class)} helper method.
 * </p>
 * <p>
 *     <b>Note:</b> When declaring your {@code AnalysisActivity} subclass in the {@code AndroidManifest.xml} you should set the theme to the {@code GiniVisionTheme} and the title to the string resource named {@code gv_title_analysis}. If you would like to use your own theme please consider that {@code AnalysisActivity} extends {@link AppCompatActivity} and requires an AppCompat Theme.
 * </p>
 * <p>
 *     The {@code AnalysisActivity} is started by the {@link CameraActivity} after the user has reviewed the document and either made no changes to the document and it hasn't been analyzed before tapping the Next button, or the user has modified the document, e.g. by rotating it.
 * </p>
 * <p>
 *     In your {@code AnalysisActivity} subclass you have to implement the following methods:
 *     <ul>
 *         <li>
 *          {@link AnalysisActivity#onAnalyzeDocument(Document)} - start analyzing the document by sending it to the Gini API.<br/><b>Note:</b> Call {@link AnalysisActivity#onDocumentAnalyzed()} when the analysis is done and the Activity hasn't been stopped.<br/><b>Note:</b> If an analysis error message was set in the Review Screen with {@link ReviewActivity#onDocumentAnalysisError(String)} this method won't be called until the user clicks the
 *     retry button next to the error message.
 *         </li>
 *         <li>{@link AnalysisActivity#onAddDataToResult(Intent)} - you should add the results of the analysis to the Intent as extras and retrieve them once the {@link CameraActivity} returns.<br/>This is called only if you called {@link AnalysisActivity#onDocumentAnalyzed()} before.<br/>When this is called, control is returned to your Activity which started the {@link CameraActivity} and you can extract the results of the analysis.</li>
 *     </ul>
 *     You can also override the following method:
 *     <ul>
 *         <li>{@link AnalysisActivity#onBackPressed()} - called when the back or the up button was clicked.</li>
 *     </ul>
 * </p>
 *
 * <h3>Customizing the Analysis Screen</h3>
 *
 * <p>
 *   Customizing the look of the Analysis Screen is done via overriding of app resources.
 * </p>
 * <p>
 *     The following items are customizable:
 *     <ul>
 *         <li>
 *             <b>Activity indicator color:</b> via the color resource named {@code gv_analysis_activity_indicator}
 *         </li>
 *         <li>
 *             <b>Background color:</b> via the color resource named {@code gv_background}. <b>Note:</b> this color resource is global to all Activities ({@link CameraActivity}, {@link OnboardingActivity}, {@link ReviewActivity}, {@link AnalysisActivity})
 *         </li>
 *         <li>
 *             <b>Error message text color:</b> via the color resource named {@code gv_snackbar_error_text}
 *         </li>
 *         <li>
 *             <b>Error message font:</b> via overriding the style named {@code GiniVisionTheme.Snackbar.Error.TextStyle} and setting an item named {@code font} with the path to the font file in your {@code assets} folder
 *         </li>
 *         <li>
 *             <b>Error message text style:</b> via overriding the style named {@code GiniVisionTheme.Snackbar.Error.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *         </li>
 *         <li>
 *             <b>Error message text size:</b> via overriding the style named {@code GiniVisionTheme.Snackbar.Error.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *         </li>
 *         <li>
 *             <b>Error message button text color:</b> via the color resource named {@code gv_snackbar_error_button_title} and {@code gv_snackbar_error_button_title_pressed}
 *         </li>
 *         <li>
 *             <b>Error message button font:</b> via overriding the style named {@code GiniVisionTheme.Snackbar.Error.Button.TextStyle} and setting an item named {@code font} with the path to the font file in your {@code assets} folder
 *         </li>
 *         <li>
 *             <b>Error message button text style:</b> via overriding the style named {@code GiniVisionTheme.Snackbar.Error.Button.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *         </li>
 *         <li>
 *             <b>Error message button text size:</b> via overriding the style named {@code GiniVisionTheme.Snackbar.Error.Button.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *         </li>
 *         <li>
 *             <b>Error message background color:</b> via the color resource named {@code gv_snackbar_error_background}
 *         </li>
 *         <li>
 *             <b>Document analysis error message retry button text:</b> via the string resource named {@code gv_document_analysis_error_retry}
 *         </li>
 *     </ul>
 * </p>
 *
 * <p>
 *     <b>Important:</b> All overriden styles must have their respective {@code Root.} prefixed style as their parent. Ex.: the parent of {@code GiniVisionTheme.Snackbar.Error.TextStyle} must be {@code Root.GiniVisionTheme.Snackbar.Error.TextStyle}.
 * </p>
 *
 * <h3>Customizing the Action Bar</h3>
 *
 * <p>
 *     Customizing the Action Bar is also done via overriding of app resources and each one - except the title string resource - is global to all Activities ({@link CameraActivity}, {@link OnboardingActivity}, {@link ReviewActivity}, {@link AnalysisActivity}).
 * </p>
 * <p>
 *     The following items are customizable:
 *     <ul>
 *         <li>
 *             <b>Background color:</b> via the color resource named {@code gv_action_bar} (highly recommended for Android 5+: customize the status bar color via {@code gv_status_bar})
 *         </li>
 *         <li>
 *             <b>Title:</b> via the string resource you set in your {@code AndroidManifest.xml} when declaring your Activity that extends {@link AnalysisActivity}. The default title string resource is named {@code gv_title_analysis}
 *         </li>
 *         <li>
 *             <b>Title color:</b> via the color resource named {@code gv_action_bar_title}
 *         </li>
 *         <li>
 *             <b>Back button (only for {@link ReviewActivity} and {@link AnalysisActivity}):</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_action_bar_back}
 *         </li>
 *     </ul>
 * </p>
 */
public abstract class AnalysisActivity extends AppCompatActivity implements AnalysisFragmentListener, AnalysisFragmentInterface {

    /**
     * @exclude
     */
    public static final String EXTRA_IN_DOCUMENT = "GV_EXTRA_IN_DOCUMENT";
    /**
     * @exclude
     */
    public static final String EXTRA_IN_DOCUMENT_ANALYSIS_ERROR_MESSAGE = "GV_EXTRA_IN_DOCUMENT_ANALYSIS_ERROR_MESSAGE";
    /**
     * @exclude
     */
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    /**
     * @exclude
     */
    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    private static final String ANALYSIS_FRAGMENT = "ANALYSIS_FRAGMENT";

    private AnalysisFragmentCompat mFragment;
    private Document mDocument;
    private String mAnalysisErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_analysis);
        if (savedInstanceState == null) {
            readExtras();
            initFragment();
        }
        enableHomeAsUp(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (handleMenuItemPressedForHomeButton(this, item)) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

    private void clearMemory() {
        mDocument = null;
    }

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDocument = extras.getParcelable(EXTRA_IN_DOCUMENT);
            mAnalysisErrorMessage = extras.getString(EXTRA_IN_DOCUMENT_ANALYSIS_ERROR_MESSAGE);
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mDocument == null) {
            throw new IllegalStateException("AnalysisActivity requires a Document. Set it as an extra using the EXTRA_IN_DOCUMENT key.");
        }
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

    private void createFragment() {
        mFragment = AnalysisFragmentCompat.createInstance(mDocument, mAnalysisErrorMessage);
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_analyze_document, mFragment, ANALYSIS_FRAGMENT)
                .commit();
    }

    @VisibleForTesting
    AnalysisFragmentCompat getFragment() {
        return mFragment;
    }

    /**
     * <p>
     *     <b>Screen API:</b> If an analysis error message was set in the Review Screen with {@link ReviewActivity#onDocumentAnalysisError(String)} this method won't be called until the user clicks the
     *     retry button next to the error message.
     * </p>
     * @param document contains the image taken by the camera (original or modified)
     */
    @Override
    public abstract void onAnalyzeDocument(@NonNull Document document);

    @Override
    public void onError(@NonNull GiniVisionError error) {
        Intent result = new Intent();
        result.putExtra(EXTRA_OUT_ERROR, error);
        setResult(RESULT_ERROR, result);
        finish();
    }

    @Override
    public void startScanAnimation() {
        mFragment.startScanAnimation();
    }

    @Override
    public void stopScanAnimation() {
        mFragment.stopScanAnimation();
    }

    @Override
    public void onDocumentAnalyzed() {
        mFragment.onDocumentAnalyzed();
        Intent result = new Intent();
        onAddDataToResult(result);
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * <p>
     *     Callback for adding your own data to the Activity's result.
     * </p>
     * <p>
     *     Called when the document was analyzed.
     * </p>
     * <p>
     *     You should add the results of the analysis as extras and retrieve them when the {@link CameraActivity} returned.
     * </p>
     * <p>
     *     <b>Note:</b> you must call {@link AnalysisActivity#onDocumentAnalyzed()} after you received the analysis results from the Gini API, otherwise this method won't be invoked.
     * </p>
     * @param result the {@link Intent} which will be returned as the result data.
     */
    public abstract void onAddDataToResult(Intent result);

    @Override
    public void showError(@NonNull String message, @NonNull String buttonTitle, @NonNull View.OnClickListener onClickListener) {
        mFragment.showError(message, buttonTitle, onClickListener);
    }

    @Override
    public void showError(@NonNull String message, int duration) {
        mFragment.showError(message, duration);
    }

    @Override
    public void hideError() {
        mFragment.hideError();
    }
}
