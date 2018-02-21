package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.test.R;

import java.util.ArrayList;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class OnboardingFragmentHostActivityNotListener extends AppCompatActivity {

    private static final String ONBOARDING_FRAGMENT = "ONBOARDING_FRAGMENT";

    static OnboardingFragmentListener sListener;

    private OnboardingFragmentCompat mOnboardingFragmentCompat;

    public OnboardingFragmentCompat getOnboardingFragmentCompat() {
        return mOnboardingFragmentCompat;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding_fragment_host);

        if (savedInstanceState == null) {
            final ArrayList<OnboardingPage> pages = new ArrayList<>();
            pages.add(new OnboardingPage(R.string.gv_onboarding_flat,
                    R.drawable.gv_onboarding_flat));
            mOnboardingFragmentCompat = OnboardingFragmentCompat.createInstanceWithoutEmptyLastPage(pages);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mOnboardingFragmentCompat, ONBOARDING_FRAGMENT)
                    .commit();
        } else {
            mOnboardingFragmentCompat =
                    (OnboardingFragmentCompat) getSupportFragmentManager()
                            .findFragmentByTag(ONBOARDING_FRAGMENT);
        }

        if (sListener != null) {
            mOnboardingFragmentCompat.setListener(sListener);
        }
    }

}
