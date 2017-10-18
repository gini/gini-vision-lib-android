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
 * <li>
 * <b>Title:</b> via the string resource name {@code gv_title_noresults}
 * </li>
 * <li>
 * <b>Title color:</b> via the color resource named {@code gv_action_bar_title}
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
