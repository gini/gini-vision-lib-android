/**
 * <p>
 * Contains the Activity and Fragments used for the Onboarding Screen.
 * </p>
 *
 * <h3>Screen API</h3>
 *
 * <p>
 * The {@link net.gini.android.vision.onboarding.OnboardingActivity} is launched directly by the {@link net.gini.android.vision.camera.CameraActivity}. It can be configured by overriding Gini Vision Lib app resources and/or by supplying the {@link net.gini.android.vision.camera.CameraActivity} an {@link java.util.ArrayList} with {@link net.gini.android.vision.onboarding.OnboardingPage} objects in the {@link net.gini.android.vision.camera.CameraActivity#EXTRA_IN_ONBOARDING_PAGES} extra.
 * </p>
 *
 * <h3>Component API</h3>
 *
 * <p>
 * To use the Component API you have to include the {@link net.gini.android.vision.onboarding.OnboardingFragmentStandard}
 * or
 * the {@link net.gini.android.vision.onboarding.OnboardingFragmentCompat} in an Activity in your app (a
 * dedicated activity is
 * recommended). To receive events from the Fragments your Activity must implement the {@link
 * net.gini.android.vision.onboarding.OnboardingFragmentListener} interface.
 * </p>
 *
 * <p>
 *     The Onboarding Screen was designed as a semi-transparent overlay for the camera preview. Your Activity should be configured to be transparent and the Scanner Fragment should not stop the camera when the onboarding is shown.
 * </p>
 */
package net.gini.android.vision.onboarding;