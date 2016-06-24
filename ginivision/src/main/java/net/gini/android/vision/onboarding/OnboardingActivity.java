package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;

import java.util.ArrayList;

/**
 * <p>
 *     When using the Screen API {@code OnboardingActivity} displays important advices for correctly photgraphing a document.
 * </p>
 * <p>
 *     The {@code OnboardingActivity} is started by the {@link net.gini.android.vision.scanner.ScannerActivity} when it is launched for the first time. You may disable this behaviour - we recommend keeping it - by setting the {@link net.gini.android.vision.scanner.ScannerActivity#EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN} to {@code false} when starting the {@link net.gini.android.vision.scanner.ScannerActivity}.
 * </p>
 * <p>
 *     You can change the number of displayed pages and their content (image and short text) by setting an {@link ArrayList} containing {@link OnboardingPage} objects for the {@link net.gini.android.vision.scanner.ScannerActivity#EXTRA_IN_ONBOARDING_PAGES} when starting the {@link net.gini.android.vision.scanner.ScannerActivity}.
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
    public void onError(GiniVisionError giniVisionError) {

    }
}
