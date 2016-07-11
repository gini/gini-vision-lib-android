package net.gini.android.vision.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;
import net.gini.android.vision.onboarding.OnboardingFragmentStandard;
import net.gini.android.visionadvtest.R;

public class CustomOnboardingActivity extends Activity implements OnboardingFragmentListener {

    public static final String EXTRA_WITHOUT_EMPTY_LAST_PAGE = "EXTRA_WITHOUT_EMPTY_LAST_PAGE";

    private OnboardingFragmentStandard mFragment;
    private boolean mShowEmptyLastPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_onboarding);
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
            mFragment = new OnboardingFragmentStandard();
        } else {
            mFragment = OnboardingFragmentStandard.createInstanceWithoutEmptyLastPage();
        }
    }

    private void showFragment() {
        getFragmentManager()
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
