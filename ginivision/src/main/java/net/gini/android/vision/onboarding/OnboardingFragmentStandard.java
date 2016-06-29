package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.ui.FragmentImplCallback;

import java.util.ArrayList;

/**
 * <h3>Component API</h3>
 *
 * <p>
 *     When you use the Component API without the Android Support Library, the {@code OnboardingFragmentStandard} displays important advice for correctly photographing a document.
 * </p>
 * <p>
 *     Include the {@code OnboardingFragmentCompat} into your layout either directly with {@code <fragment>} in your Activity's layout or using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 *     If you would like to display a different number of pages, you can use the {@link OnboardingFragmentStandard#createInstance(ArrayList)} factory method and provide a list of {@link OnboardingPage} objects.
 * </p>
 * <p>
 *     Your Activity must implement the {@link OnboardingFragmentListener} interface to receive events from the Onboarding Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link OnboardingFragmentStandard#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customizing the Onboarding Screen</h3>
 *
 * <p>
 *     See the {@link OnboardingActivity} for details.
 * </p>
 */
public class OnboardingFragmentStandard extends Fragment implements FragmentImplCallback {

    private OnboardingFragmentImpl mFragmentImpl;

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment using the provided list of onboarding pages.
     * </p>
     * <p>
     *     If you don't need a custom number of pages, you can use the default constructor.
     * </p>
     * @param pages the pages to be shown
     * @return a new instance of the Fragment
     */
    public static OnboardingFragmentStandard createInstance(ArrayList<OnboardingPage> pages) {
        OnboardingFragmentStandard fragment = new OnboardingFragmentStandard();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(pages));
        return fragment;
    }

    /**
     * @exclude
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = OnboardingFragmentHelper.createFragmentImpl(this, getArguments());
        OnboardingFragmentHelper.setListener(mFragmentImpl, getActivity());
    }

    /**
     * @exclude
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }
}
