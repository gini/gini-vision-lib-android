package net.gini.android.vision.help;

import static net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp;
import static net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.noresults.NoResultsActivity;
import net.gini.android.vision.review.ReviewActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * <h3>Screen API and Component API</h3>
 *
 * <p>
 *     On the File Import Screen users can get information about how import files from other apps via "open with".
 * </p>
 * <p>
 *     This Activity is launched by the {@link HelpActivity} for both Screen and Component APIs.
 * </p>
 * <p>
 *     <b>Important:</b> The File Import Screen is only shown only if file import was enabled with the {@link GiniVisionFeatureConfiguration}.
 * </p>
 * <p>
 *     The contents of this screen need to be customized to insert your App's name or label for the "open with" functionality into the texts and illustrations.
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
 *             <b>Background color:</b> via the color resource named {@code gv_file_import_activity_background}.
 *         </li>
 *         <li>
 *             <b>Header text:</b> via overriding the string resource named {@code gv_file_import_header}
 *         </li>
 *         <li>
 *             <b>Header text style:</b> via overriding the style named {@code GiniVisionTheme.Help.FileImport.Header.TextStyle}
 *         </li>
 *         <li>
 *             <b>Separator line color:</b> via the color resource named {@code gv_file_import_separator}
 *         </li>
 *         <li>
 *             <b>Section numbers' background circle color:</b> via the color resource named {@code gv_file_import_section_number_background}
 *         </li>
 *         <li>
 *             <b>Section numbers' text color:</b>  via the color resource named {@code gv_file_import_section_number}
 *         </li>
 *         <li>
 *             <b>Section title text style:</b> via overriding the style named {@code GiniVisionTheme.Help.FileImport.Section.Title.TextStyle}
 *         </li>
 *         <li>
 *             <b>Section body text style:</b> via overriding the style named {@code GiniVisionTheme.Help.FileImport.Section.Body.TextStyle}
 *         </li>
 *         <li>
 *             <b>Section 1 title:</b> via overriding the string resource named {@code gv_file_import_section_1_title}
 *         </li>
 *         <li>
 *             <b>Section 1 body:</b> via overriding the string resource named {@code gv_file_import_section_1_body}
 *         </li>
 *         <li>
 *             <b>Section 1 illustration image:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
 *             named {@code gv_file_import_section_1_illustration.png}. For creating your custom illustration you may use <a href="https://github.com/gini/gini-vision-lib-assets/blob/master/Gini-Vision-Lib-Design-Elements/Illustrations/PDF/android_pdf_open_with_illustration_1.pdf" target="_blank">this template</a> from the <a href="https://github.com/gini/gini-vision-lib-assets" target="_blank">Gini Vision Library UI Assets</a> repository.
 *         </li>
 *         <li>
 *             <b>Section 2 title:</b> via overriding the string resource named {@code gv_file_import_section_2_title}
 *         </li>
 *         <li>
 *             <b>Section 2 body:</b> via overriding the string resource named {@code gv_file_import_section_2_body}
 *         </li>
 *         <li>
 *             <b>Section 2 illustration image:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
 *             named {@code gv_file_import_section_2_illustration.png}. For creating your custom illustration you may use <a href="https://github.com/gini/gini-vision-lib-assets/blob/master/Gini-Vision-Lib-Design-Elements/Illustrations/PDF/android_pdf_open_with_illustration_2.pdf" target="_blank">this template</a> from the <a href="https://github.com/gini/gini-vision-lib-assets" target="_blank">Gini Vision Library UI Assets</a> repository.
 *         </li>
 *         <li>
 *             <b>Section 3 title:</b> via overriding the string resource named {@code gv_file_import_section_3_title}
 *         </li>
 *         <li>
 *             <b>Section 3 body:</b> via overriding the string resource named {@code gv_file_import_section_3_body}
 *         </li>
 *         <li>
 *             <b>Section 3 illustration image:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi
 *             named {@code gv_file_import_section_3_illustration.png}
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
 * <b>Title:</b> via the string resource name {@code gv_title_file_import}
 * </li>
 * <li>
 * <b>Title color:</b> via the color resource named {@code gv_action_bar_title}
 * </li>
 * <li> <b>Back button:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named
 * {@code gv_action_bar_back}
 * </li>
 * </ul>
 * </p>
 */
public class FileImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_file_import);
        forcePortraitOrientationOnPhones(this);

        // Show illustration for the first section only if available (height > 0)
        final Drawable section1Illustration = ContextCompat.getDrawable(this,
                R.drawable.gv_file_import_section_1_illustration);
        if (section1Illustration.getMinimumHeight() > 0) {
            final ImageView section1ImageView = findViewById(R.id.gv_section_1_illustration);
            section1ImageView.setVisibility(View.VISIBLE);
            section1ImageView.setImageDrawable(section1Illustration);
        }
        setupHomeButton();
    }

    private void setupHomeButton() {
        if (GiniVision.hasInstance() && GiniVision.getInstance().areBackButtonsEnabled()) {
            enableHomeAsUp(this);
        }
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
