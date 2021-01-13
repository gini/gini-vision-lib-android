package net.gini.android.vision.help;

import static net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.os.Bundle;
import android.view.MenuItem;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.noresults.NoResultsActivity;
import net.gini.android.vision.review.ReviewActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * <h3>Screen API and Component API</h3>
 *
 * <p>
 *     The Supported Formats Screen shows information about the document formats supported by the Gini Vision Library.
 * </p>
 * <p>
 *     This Activity is launched by the {@link HelpActivity} for both Screen and Component APIs.
 * </p>
 * <p>
 *     The contents of this screen are modified according to the features you configured with the {@link GiniVisionFeatureConfiguration}.
 * </p>
 *
 * <h3>Customizing the File Import Screen</h3>
 *
 * <p>
 *     Customizing the look of the File Import Screen is done via overriding of app resources.
 * </p>
 * <p>
 *     The following items are customizable:
 *     <ul>
 *         <li>
 *             <b>Background color:</b> via the color resource named {@code gv_supported_formats_activity_background}.
 *         </li>
 *         <li>
 *             <b>Header text style:</b> via overriding the style named {@code GiniVisionTheme.Help.SupportedFormats.Item.Header.TextStyle}
 *         </li>
 *         <li>
 *             <b>Format info text style:</b> via overriding the style named {@code GiniVisionTheme.Help.SupportedFormats.Item.TextStyle}
 *         </li>
 *         <li>
 *             <b>Format info list item background color:</b> via overriding the style named {@code gv_supported_formats_item_background}
 *         </li>
 *         <li>
 *             <b>Supported format background circle color:</b> via the color resource named {@code gv_supported_formats_item_supported_icon_background}
 *         </li>
 *         <li>
 *             <b>Supported format foreground tick color:</b> via the color resource named {@code gv_supported_formats_item_supported_icon_foreground}
 *         </li>
 *         <li>
 *             <b>Unsupported format background circle color:</b> via the color resource named {@code gv_supported_formats_item_unsupported_icon_background}
 *         </li>
 *         <li>
 *             <b>Unsupported format foreground cross color:</b> via the color resource named {@code gv_supported_formats_item_unsupported_icon_foreground}
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
 * NoResultsActivity}, {@link HelpActivity}, {@link ReviewActivity}, {@link AnalysisActivity}).
 * </p>
 * <p>
 * The following items are customizable:
 * <ul>
 * <li>
 * <b>Background color:</b> via the color resource named {@code gv_action_bar} (highly recommended
 * for Android 5+: customize the status bar color via {@code gv_status_bar})
 * </li>
 * <li>
 * <b>Title:</b> via the string resource name {@code gv_title_supported_formats}
 * </li>
 * <li>
 * <b>Title color:</b> via the color resource named {@code gv_action_bar_title}
 * </li>
 * <li><b>Back button:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named
 * {@code gv_action_bar_back}
 * </li>
 * </ul>
 * </p>
 */
public class SupportedFormatsActivity extends AppCompatActivity {

    /**
     * @suppress
     *
     * @Deprecated Configuration should be applied by creating a {@link GiniVision} instance using
     * {@link GiniVision#newInstance()} and the returned {@link GiniVision.Builder}.
     */
    public static final String EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION =
            "GV_EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION";

    private GiniVisionFeatureConfiguration mGiniVisionFeatureConfiguration;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_supported_formats);
        readExtras();
        setUpFormatsList();
        forcePortraitOrientationOnPhones(this);
        setupHomeButton();
    }

    private void setupHomeButton() {
        if (GiniVision.hasInstance() && GiniVision.getInstance().areBackButtonsEnabled()) {
            enableHomeAsUp(this);
        }
    }

    private void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mGiniVisionFeatureConfiguration = extras.getParcelable(
                    EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION);
            if (mGiniVisionFeatureConfiguration == null) {
                mGiniVisionFeatureConfiguration =
                        GiniVisionFeatureConfiguration.buildNewConfiguration().build();
            }
        }
    }

    private void setUpFormatsList() {
        final RecyclerView recyclerView = findViewById(R.id.gv_formats_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SupportedFormatsAdapter(mGiniVisionFeatureConfiguration));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
