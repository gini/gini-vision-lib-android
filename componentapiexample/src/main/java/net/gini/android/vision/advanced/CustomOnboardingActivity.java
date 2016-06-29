package net.gini.android.vision.advanced;

import android.app.Activity;
import android.os.Bundle;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomOnboardingActivity extends Activity implements OnboardingFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_onboarding);
    }

    @Override
    public void onCloseOnboarding() {

    }

    @Override
    public void onError(GiniVisionError giniVisionError) {

    }
}
