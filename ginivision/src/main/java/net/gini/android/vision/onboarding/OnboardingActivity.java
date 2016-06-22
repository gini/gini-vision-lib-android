package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;

import java.util.ArrayList;

public class OnboardingActivity extends AppCompatActivity implements OnboardingFragmentListener {

    /**
     * Type: {@code ArrayList<OnboardingPage>}
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
            mOnboardingFragment = OnboardingFragmentCompat.createInstance();
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
