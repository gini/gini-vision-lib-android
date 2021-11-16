package net.gini.android.vision.onboarding

import android.app.Application
import android.content.res.Resources
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import net.gini.android.vision.R
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import java.util.*

/**
 * Created by Alpar Szotyori on 21.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
@RunWith(AndroidJUnit4::class)
class OnboardingFragmentCompatTest {

    @Test(expected = IllegalStateException::class)
    fun `should throw exception when listener was not set`() {
        // Given
        val scenario = launchFragment(initialState = Lifecycle.State.INITIALIZED) {
            OnboardingFragmentCompat.createInstance(arrayListOf(
                    OnboardingPage(R.string.gv_onboarding_flat, R.drawable.gv_onboarding_flat)
            ))
        }

        // When
        scenario.moveToState(Lifecycle.State.RESUMED)
    }
}