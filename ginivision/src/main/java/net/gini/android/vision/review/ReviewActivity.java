package net.gini.android.vision.review;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.Document;
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
 *     <b>Note:</b> {@code ReviewActivity} extends {@link AppCompatActivity} and requires an AppCompat Theme.
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
 *             <b>Bottom panel background color:</b> via the color resource named {@code gv_review_bottom_panel_background}
 *         </li>
 *         <li>
 *             <b>Background color:</b> via the color resource named {@code gv_background}. <b>Note:</b> this color resource is global to all Activities ({@link CameraActivity}, {@link OnboardingActivity}, {@link ReviewActivity}, {@link AnalysisActivity})
 *         </li>
 *     </ul>
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
 *             <b>Title:</b> via the string resource you set in your {@code AndroidManifest.xml} when declaring your Activity that extends {@link ReviewActivity}
 *         </li>
 *         <li>
 *             <b>Title color:</b> via the color resource named {@code gv_action_bar_title}
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
    public static final String EXTRA_OUT_DOCUMENT = "GV_EXTRA_OUT_DOCUMENT";
    /**
     * @exclude
     */
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    /**
     * @exclude
     */
    public static final int RESULT_PHOTO_WAS_REVIEWED = RESULT_FIRST_USER + 1;
    /**
     * @exclude
     */
    public static final int RESULT_PHOTO_WAS_REVIEWED_AND_ANALYZED = RESULT_FIRST_USER + 2;
    /**
     * @exclude
     */
    public static final int RESULT_ERROR = RESULT_FIRST_USER + 3;

    private ReviewFragmentCompat mFragment;
    private Document mDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_review);
        if (savedInstanceState == null) {
            readExtras();
            createFragment();
            showFragment();
        }
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
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mDocument == null) {
            throw new IllegalStateException("ReviewActivity requires a Document. Set it as an extra using the EXTRA_IN_DOCUMENT key.");
        }
    }

    private void createFragment() {
        mFragment = ReviewFragmentCompat.createInstance(mDocument);
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_review_document, mFragment)
                .commit();
    }

    // callback for subclasses for uploading the photo before it was reviewed, if the photo is not changed
    // no new upload is required
    @Override
    public abstract void onShouldAnalyzeDocument(@NonNull Document document);

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        Intent result = new Intent();
        result.putExtra(EXTRA_OUT_DOCUMENT, document);
        onAddDataToResult(result);
        setResult(RESULT_PHOTO_WAS_REVIEWED, result);
        finish();
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        Intent result = new Intent();
        result.putExtra(EXTRA_OUT_DOCUMENT, document);
        onAddDataToResult(result);
        setResult(RESULT_PHOTO_WAS_REVIEWED_AND_ANALYZED, result);
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

    // TODO: call this, if the photo was analyzed before the review was completed, it prevents the analyze activity to
    // be started, if the photo was already analyzed and the user didn't change it
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
}
