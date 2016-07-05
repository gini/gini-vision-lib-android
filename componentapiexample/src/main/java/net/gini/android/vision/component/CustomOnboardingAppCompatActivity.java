package net.gini.android.vision.component;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomOnboardingAppCompatActivity extends AppCompatActivity implements OnboardingFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_onboarding_compat);
    }

    @Override
    public void onCloseOnboarding() {
        finish();
    }

    @Override
    public void onError(GiniVisionError giniVisionError) {

    }
}
