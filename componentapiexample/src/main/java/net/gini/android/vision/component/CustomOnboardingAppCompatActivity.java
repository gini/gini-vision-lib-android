package net.gini.android.vision.component;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.onboarding.OnboardingFragmentCompat;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomOnboardingAppCompatActivity extends AppCompatActivity implements OnboardingFragmentListener {

    public static final String EXTRA_WITHOUT_EMPTY_LAST_PAGE = "EXTRA_WITHOUT_EMPTY_LAST_PAGE";

    private OnboardingFragmentCompat mFragment;
    private boolean mShowEmptyLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_onboarding_compat);
        readExtras();
        createFragment();
        showFragment();
    }

    private void readExtras() {
        Intent intent = getIntent();
        mShowEmptyLastPage = !intent.getBooleanExtra(EXTRA_WITHOUT_EMPTY_LAST_PAGE, false);
    }

    private void createFragment() {
        if (mShowEmptyLastPage) {
            mFragment = new OnboardingFragmentCompat();
        } else {
            mFragment = OnboardingFragmentCompat.createInstanceWithoutEmptyLastPage();
        }
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_onboarding, mFragment)
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
