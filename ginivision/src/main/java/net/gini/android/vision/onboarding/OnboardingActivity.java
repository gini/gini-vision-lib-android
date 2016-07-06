package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.review.ReviewActivity;

import java.util.ArrayList;

/**
 * <h3>Screen API</h3>
 *
 * <p>
 *     When you use the Screen API, the {@code OnboardingActivity} displays important advice for correctly photographing a document.
 * </p>
 * <p>
 *     The {@code OnboardingActivity} is started by the {@link CameraActivity} when the latter is launched for the first time. You may disable this behavior - we highly recommend keeping it - by setting the {@link CameraActivity#EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN} to {@code false} when starting the {@link CameraActivity}.
 * </p>
 * <p>
 *     You can change the number of displayed pages and their content (image and short text) by setting an {@link ArrayList} containing {@link OnboardingPage} objects for the {@link CameraActivity#EXTRA_IN_ONBOARDING_PAGES} when starting the {@link CameraActivity}.
 * </p>
 *
 * <h3>Customizing the Onboarding Screen</h3>
 *
 * <p>
 *   Customizing the look of the Onboarding Screen is done via overriding of app resources or by providing your own pages with your own strings and drawable resources.
 * </p>
 * <p>
 *     The following items are customizable:
 *     <ul>
 *         <li>
 *             <b>Next button icon:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_onboarding_fab_next.png}
 *         </li>
 *         <li>
 *             <b>Next button color:</b> via the color resources named {@code gv_onboarding_fab} and {@code gv_onboarding_fab_pressed}
 *         </li>
 *         <li>
 *             <b>Page indicators:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_onboarding_indicator_active.png} and {@code gv_onboarding_indicator_inactive.png}
 *         </li>
 *         <li>
 *             <b>First page image:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_onboarding_flat.png}
 *         </li>
 *         <li>
 *             <b>First page text:</b> via the string resource named {@code gv_onboarding_flat}
 *         </li>
 *         <li>
 *             <b>Second page image:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_onboarding_parallel.png}
 *         </li>
 *         <li>
 *             <b>Second page text:</b> via the string resource named {@code gv_onboarding_parallel}
 *         </li>
 *         <li>
 *             <b>Third page image:</b> via images for mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi named {@code gv_onboarding_align.png}
 *         </li>
 *         <li>
 *             <b>Third page text:</b> via the string resource named {@code gv_onboarding_align}
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
 *             <b>Title:</b> via the string resource named {@code gv_title_onboarding}
 *         </li>
 *         <li>
 *             <b>Title color:</b> via the color resource named {@code gv_action_bar_title}
 *         </li>
 *     </ul>
 * </p>
 */
public class OnboardingActivity extends AppCompatActivity implements OnboardingFragmentListener {

    /**
     * @exclude
     */
    public static final String EXTRA_ONBOARDING_PAGES = "GV_EXTRA_PAGES";

    private ArrayList<OnboardingPage> mPages;
    private OnboardingFragmentCompat mOnboardingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_onboarding);
        readExtras();
        createFragment();
        showFragment();
    }

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mPages = extras.getParcelableArrayList(EXTRA_ONBOARDING_PAGES);
        }
    }

    private void createFragment() {
        if (mPages != null) {
            mOnboardingFragment = OnboardingFragmentCompat.createInstance(mPages);
        } else {
            mOnboardingFragment = new OnboardingFragmentCompat();
        }
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_onboarding, mOnboardingFragment)
                .commit();
    }

    @Override
    public void onCloseOnboarding() {
        finish();
    }

    @Override
    public void onError(@NonNull GiniVisionError giniVisionError) {

    }
}
