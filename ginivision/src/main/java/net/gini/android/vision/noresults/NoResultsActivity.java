package net.gini.android.vision.noresults;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.review.ReviewActivity;

/**
 * <h3>Screen API</h3>
 *
 * <p>
 * When you use the Screen API, the {@code NoResultsFragmentCompat} displays hints that show how to
 * best take a picture of a document.
 * </p>
 *
 * <h3>Customizing the No Results Screen</h3>
 *
 * <p>
 *   Customizing the look of the No Results Screen is done via overriding of app resources.
 * </p>
 * <p>
 *     The following items are customizable:
 *     <ul>
 *         <li>
 *             <b>Header text color:</b> via the color resource named {@code
 *             gv_noresults_header}
 *         </li>
 *         <li>
 *             <b>Header text font:</b> via overriding the style named {@code GiniVisionTheme
 *             .NoResults.Header.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in your {@code assets} folder
 *         </li>
 *         <li>
 *             <b>Header text style:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Header.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *         </li>
 *         <li>
 *             <b>Header text size:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Header.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *         </li>
 *         <li>
 *             <b>Headline text color:</b> via the color resource named {@code gv_noresults_headline}
 *         </li>
 *         <li>
 *             <b>Headline text font:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Headline.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in your {@code assets} folder
 *         </li>
 *         <li>
 *             <b>Headline text style:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Headline.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *         </li>
 *         <li>
 *             <b>Headline text size:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Headline.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *         </li>
 *         <li>
 *             <b>Tip text color:</b> via the color resource named {@code gv_noresults_tip}
 *         </li>
 *         <li>
 *             <b>Tip text font:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Tip.TextStyle} and setting an item named {@code gvCustomFont} with the path to the font file in your {@code assets} folder
 *         </li>
 *         <li>
 *             <b>Tip text style:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Tip.TextStyle} and setting an item named {@code android:textStyle} to {@code normal}, {@code bold} or {@code italic}
 *         </li>
 *         <li>
 *             <b>Tip text size:</b> via overriding the style named {@code GiniVisionTheme.NoResults.Tip.TextStyle} and setting an item named {@code android:textSize} to the desired {@code sp} size
 *         </li>
 *         <li>
 *             <b>Tip image - Good lighting:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
 *             named
 *             {@code gv_photo_tip_lighting.png}
 *         </li>
 *         <li>
 *             <b>Tip image - Document should be flat:</b> via images for mdpi, hdpi, xhdpi, xxhdpi,
 *             xxxhdpi
 *             named {@code gv_photo_tip_flat.png}
 *         </li>
 *         <li>
 *             <b>Tip image - Device should be parallel to document:</b> via images for mdpi, hdpi,
 *             xhdpi,xxhdpi, xxxhdpi named {@code gv_photo_tip_parallel.png}
 *         </li>
 *         <li>
 *             <b>Tip image - Document should be aligned with corner guides:</b> via
 *             images for mdpi, hdpi, xhdpi,xxhdpi, xxxhdpi named {@code gv_photo_tip_align.png}
 *         </li>
 *         <li>
 *             <b>Button color:</b> via the color resource named {@code gv_noresults_button}
 *         </li>
 *         <li>
 *             <b>Button text color:</b> via the color resource named {@code gv_noresults_button_text}
 *         </li>
 *         <li>
 *             <b>Background color:</b> via the color resource named {@code gv_noresults_background}.
 *         </li>
 *     </ul>
 * </p>
 *
 * <p>
 *     <b>Important:</b> All overriden styles must have their respective {@code Root.} prefixed style as their parent. Ex.: the parent of {@code GiniVisionTheme.Onboarding.Message.TextStyle} must be {@code Root.GiniVisionTheme.Onboarding.Message.TextStyle}.
 * </p>
 *
 * <h3>Customizing the Action Bar</h3>
 *
 * <p>
 * Customizing the Action Bar is done via overriding of app resources and each one - except the
 * title string resource - is global to all Activities ({@link CameraActivity}, {@link
 * NoResultsActivity}, {@link ReviewActivity}, {@link AnalysisActivity}).
 * </p>
 * <p>
 * The following items are customizable:
 * <ul>
 * <li>
 * <b>Background color:</b> via the color resource named {@code gv_action_bar} (highly recommended
 * for Android 5+: customize the status bar color via {@code gv_status_bar})
 * </li>
 * </ul>
 * </p>
 */
public class NoResultsActivity extends AppCompatActivity implements NoResultsFragmentListener {

    /**
     * @exclude
     */
    public static final String EXTRA_IN_DOCUMENT = "GV_EXTRA_IN_DOCUMENT";

    private Document mDocument;

    @Override
    public void onBackToCameraPressed() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_noresults);
        setTitle("");
        readExtras();
        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }
        if (savedInstanceState == null) {
            initFragment();
        }
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
            throw new IllegalStateException(
                    "NoResultsActivity requires a Document. Set it as an extra using the EXTRA_IN_DOCUMENT key.");
        }
    }

    private void initFragment() {
        NoResultsFragmentCompat noResultsFragment = NoResultsFragmentCompat.createInstance(
                mDocument);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_noresults, noResultsFragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
