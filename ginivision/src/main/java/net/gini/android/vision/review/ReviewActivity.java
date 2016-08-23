package net.gini.android.vision.review;

import static net.gini.android.vision.util.ActivityHelper.enableHomeAsUp;
import static net.gini.android.vision.util.ActivityHelper.handleMenuItemPressedForHomeButton;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.onboarding.OnboardingActivity;

/**
 * <h3>Screen API</h3>
 *
 * <p>
 *     When you use the Screen API, the {@code ReviewActivity} displays the photographed document and allows the user to review it by checking the sharpness, quality and orientation of the image. The user can correct the orientation by rotating the image.
 * </p>
 * <p>
 *     You must extend the {@code ReviewActivity} in your application and provide it to the {@link CameraActivity} by using the {@link CameraActivity#setReviewActivityExtra(Intent, Context, Class)} helper method.
 * </p>
 * <p>
 *     <b>Note:</b> When declaring your {@code ReviewActivity} subclass in the {@code AndroidManifest.xml} you should set the theme to the {@code GiniVisionTheme} and the title to the string resource named {@code gv_title_review}. If you would like to use your own theme please consider that {@code ReviewActivity} extends {@link AppCompatActivity} and requires an AppCompat Theme.
 * </p>
 * <p>
 *     The {@code ReviewActivity} is started by the {@link CameraActivity} after the user has taken a photo of a document.
 * </p>
 * <p>
 *     In your {@code ReviewActivity} subclass you have to implement the following methods:
 *     <ul>
 *         <li>{@link ReviewActivity#onShouldAnalyzeDocument(Document)} - you should start analyzing the original document by sending it to the Gini API. We assume that in most cases the photo is good enough and this way we are able to provide analysis results quicker.<br/><b>Note:</b> Call {@link ReviewActivity#onDocumentAnalyzed()} when the analysis is done and the Activity wasn't stopped.</li>
 *         <li>{@link ReviewActivity#onAddDataToResult(Intent)} - you can add the results of the analysis to the Intent as extras and retrieve them once the {@link CameraActivity} returns.<br/>This is called only if you called {@link ReviewActivity#onDocumentAnalyzed()} and the image wasn't changed before the user tapped on the Next button.<br/>When this is called, your {@link AnalysisActivity} subclass is not launched, instead control is returned to your Activity which started the {@link CameraActivity} and you can extract the results of the analysis.</li>
 *     </ul>
 *     You can also override the following methods:
 *     <ul>
 *         <li>{@link ReviewActivity#onDocumentWasRotated(Document, int, int)} - you should cancel the analysis started in {@link ReviewActivity#onShouldAnalyzeDocument(Document)} because the document was rotated and analysing the original is not necessary anymore. The Gini Vision Library will proceed to the Analysis Screen where the reviewed document can be analyzed.</li>
 *         <li>{@link ReviewActivity#onProceedToAnalysisScreen(Document)} - called when the Gini Vision Library will continue to the Analysis Screen. For example you can unsubscribe your analysis listener, if you want to continue the analysis in your {@link AnalysisActivity} subclass in case the document wasn't modified.</li>
 *         <li>{@link ReviewActivity#onBackPressed()} - called when the back or the up button was clicked. You should cancel the analysis started in {@link ReviewActivity#onShouldAnalyzeDocument(Document)}.</li>
 *     </ul>
 * </p>
 *
 * <h3>Customizing the Review Screen</h3>
 *
 * <p>
 *   Customizing the look of the Review Screen is done via overriding of app resources.
 * </p>
 * <p>
 *     The following items are customizable:
 *     <ul>
 *         <li>
 *             <b>Rotate button icon:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_review_button_rotate.png}
 *         </li>
 *         <li>
 *             <b>Rotate button color:</b>  via the color resources named {@code gv_review_fab_mini}  and {@code gv_review_fab_mini_pressed}
 *         </li>
 *         <li>
 *             <b>Next button icon:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_review_fab_next.png}
 *         </li>
 *         <li>
 *             <b>Next button color:</b> via the color resources named {@code gv_review_fab} and {@code gv_review_fab_pressed}
 *         </li>
 *         <li>
 *             <b>Bottom advice text:</b> via the string resource named {@code gv_review_bottom_panel_text}
 *         </li>
 *         <li>
 *             <b>Bottom text color:</b> via the color resource named {@code gv_review_bottom_panel_text}
 *         </li>
 *         <li>
 *             <b>Bottom text font:</b> via overriding the style named {@code GiniVisionTheme.Review.BottomPanel.TextStyle} and setting an item named {@code font} with the path to the font file in your {@code assets} folder
 *         </li>
 *         <li>
 *             <b>Bottom text style:</b> via overriding the style named {@code GiniVisionTheme.Review.BottomPanel.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *         </li>
 *         <li>
 *             <b>Bottom text size:</b> via overriding the style named {@code GiniVisionTheme.Review.BottomPanel.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *         </li>
 *         <li>
 *             <b>Bottom panel background color:</b> via the color resource named {@code gv_review_bottom_panel_background}
 *         </li>
 *         <li>
 *             <b>Background color:</b> via the color resource named {@code gv_background}. <b>Note:</b> this color resource is global to all Activities ({@link CameraActivity}, {@link OnboardingActivity}, {@link ReviewActivity}, {@link AnalysisActivity})
 *         </li>
 *     </ul>
 * </p>
 *
 * <p>
 *     <b>Important:</b> All overriden styles must have their respective {@code Root.} prefixed style as their parent. Ex.: the parent of {@code GiniVisionTheme.Review.BottomPanel.TextStyle} must be {@code Root.GiniVisionTheme.Review.BottomPanel.TextStyle}.
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
 *             <b>Title:</b> via the string resource you set in your {@code AndroidManifest.xml} when declaring your Activity that extends {@link ReviewActivity}. The default title string resource is named {@code gv_title_review}
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
public abstract class ReviewActivity extends AppCompatActivity implements ReviewFragmentListener, ReviewFragmentInterface {

    /**
     * @exclude
     */
    public static final String EXTRA_IN_DOCUMENT = "GV_EXTRA_IN_DOCUMENT";
    /**
     * @exclude
     */
    public static final String EXTRA_IN_ANALYSIS_ACTIVITY = "GV_EXTRA_IN_ANALYSIS_ACTIVITY";
    /**
     * @exclude
     */
    public static final String EXTRA_OUT_DOCUMENT = "GV_EXTRA_OUT_DOCUMENT";
    /**
     * @exclude
     */
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    /**
     * @exclude
     */
    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    private static final int ANALYSE_DOCUMENT_REQUEST = 1;

    private static final String REVIEW_FRAGMENT = "REVIEW_FRAGMENT";

    private ReviewFragmentCompat mFragment;
    private Document mDocument;
    private String mDocumentAnalysisErrorMessage;

    private Intent mAnalyzeDocumentActivityIntent;

    @VisibleForTesting
    ReviewFragmentCompat getFragment() {
        return mFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_review);
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

    @VisibleForTesting
    void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDocument = extras.getParcelable(EXTRA_IN_DOCUMENT);
            mAnalyzeDocumentActivityIntent = extras.getParcelable(EXTRA_IN_ANALYSIS_ACTIVITY);
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mDocument == null) {
            throw new IllegalStateException("ReviewActivity requires a Document. Set it as an extra using the EXTRA_IN_DOCUMENT key.");
        }
        if (mAnalyzeDocumentActivityIntent == null) {
            throw new IllegalStateException("ReviewActivity requires an AnalyzeDocumentActivity class. Call setAnalyzeDocumentActivityExtra() to set it.");
        }
    }

    private void initFragment() {
        if (!isFragmentShown()) {
            createFragment();
            showFragment();
        }
    }

    private boolean isFragmentShown() {
        return getSupportFragmentManager().findFragmentByTag(REVIEW_FRAGMENT) != null;
    }

    private void createFragment() {
        mFragment = ReviewFragmentCompat.createInstance(mDocument);
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_review_document, mFragment, REVIEW_FRAGMENT)
                .commit();
    }

    @Override
    public abstract void onShouldAnalyzeDocument(@NonNull Document document);

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        mAnalyzeDocumentActivityIntent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, document);
        if (mDocumentAnalysisErrorMessage != null) {
            mAnalyzeDocumentActivityIntent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT_ANALYSIS_ERROR_MESSAGE, mDocumentAnalysisErrorMessage);
        }
        startActivityForResult(mAnalyzeDocumentActivityIntent, ANALYSE_DOCUMENT_REQUEST);
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        Intent result = new Intent();
        result.putExtra(EXTRA_OUT_DOCUMENT, document);
        onAddDataToResult(result);
        setResult(RESULT_OK, result);
        finish();
    }

    /**
     * <p>
     *     Callback for adding your own data to the Activity's result.
     * </p>
     * <p>
     *     Called when the document has been analyzed and wasn't modified at the time the user tapped on the Next button.
     * </p>
     * <p>
     *     You should add the results of the analysis as extras and retrieve them when the {@link CameraActivity} returns.
     * </p>
     * <p>
     *     <b>Note:</b> you should call {@link ReviewActivity#onDocumentAnalyzed()} after you've received the analysis results from the Gini API, otherwise this method won't be invoked.
     * </p>
     * @param result the {@link Intent} which will be returned as the result data.
     */
    public abstract void onAddDataToResult(@NonNull Intent result);

    @Override
    public void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation) {
        clearDocumentAnalysisError();
    }

    @Override
    public void onDocumentAnalyzed() {
        mFragment.onDocumentAnalyzed();
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {
        Intent result = new Intent();
        result.putExtra(EXTRA_OUT_ERROR, error);
        setResult(RESULT_ERROR, result);
        finish();
    }

    /**
     * <p>
     *     If the analysis started in {@link ReviewActivity#onShouldAnalyzeDocument(Document)} failed you can set
     *     an error message here, which will be shown in the {@link AnalysisActivity} with a retry button.
     * </p>
     * @param message an error message to be shown to the user
     */
    protected void onDocumentAnalysisError(String message) {
        mDocumentAnalysisErrorMessage = message;
    }

    private void clearDocumentAnalysisError() {
        mDocumentAnalysisErrorMessage = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ANALYSE_DOCUMENT_REQUEST) {
            setResult(resultCode, data);
            finish();
        }
        clearMemory();
    }
}
