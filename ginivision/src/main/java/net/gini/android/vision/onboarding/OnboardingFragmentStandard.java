package net.gini.android.vision.onboarding;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * <h3>Component API</h3>
 *
 * <p>
 *     When you use the Component API without the Android Support Library, the {@code OnboardingFragmentStandard} displays important advice for correctly photographing a document.
 * </p>
 * <p>
 *     Include the {@code OnboardingFragmentStandard} into your layout either directly with {@code <fragment>} in your Activity's layout or using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 *     The default way of showing the Onboarding Screen is as an overlay above the camera preview with a semi-transparent background.
 * </p>
 * <p>
 *     By default an empty last page is added to enable the revealing of the camera preview before the Onboarding Screen is dismissed.
 * </p>
 * <p>
 *     If you would like to display a different number of pages, you can use the {@link OnboardingFragmentStandard#createInstance(ArrayList)} or {@link OnboardingFragmentStandard#createInstanceWithoutEmptyLastPage(ArrayList)} factory method and provide a list of {@link OnboardingPage} objects.
 * </p>
 * <p>
 *     If you would like to disable the appending of the empty last page, you can use the {@link OnboardingFragmentStandard#createInstanceWithoutEmptyLastPage(ArrayList)} or the {@link OnboardingFragmentStandard#createInstanceWithoutEmptyLastPage()} factory method.
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
public class OnboardingFragmentStandard extends Fragment implements OnboardingFragmentImplCallback {

    private OnboardingFragmentImpl mFragmentImpl;

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment using the provided list of onboarding pages.
     * </p>
     * <p>
     *     This method prevents the appending of an empty page to your pages.
     * </p>
     * <p>
     *     If you don't need a custom number of pages and wish to use the default behaviour, you can use the default constructor of {@link OnboardingFragmentCompat}.
     * </p>
     * @param pages the pages to be shown
     * @return a new instance of the Fragment
     */
    public static OnboardingFragmentStandard createInstanceWithoutEmptyLastPage(
            @NonNull ArrayList<OnboardingPage> pages) {
        OnboardingFragmentStandard fragment = new OnboardingFragmentStandard();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(pages, false));
        return fragment;
    }

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment using the provided list of onboarding pages.
     * </p>
     * <p>
     *     If you would like to prevent the appending of an empty page, you can use the {@link OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage(ArrayList)} factory method.
     * </p>
     * <p>
     *     If you don't need a custom number of pages and wish to use the default behaviour, you can use the default constructor of {@link OnboardingFragmentCompat}.
     * </p>
     * @param pages the pages to be shown
     * @return a new instance of the Fragment
     */
    public static OnboardingFragmentStandard createInstance(
            @NonNull ArrayList<OnboardingPage> pages) {
        OnboardingFragmentStandard fragment = new OnboardingFragmentStandard();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(pages, true));
        return fragment;
    }

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment without appending an empty page to the default pages.
     * </p>
     * <p>
     *     If you wish to use the default behaviour, you can use the default constructor of {@link OnboardingFragmentCompat}.
     * </p>
     * @return a new instance of the Fragment
     */
    public static OnboardingFragmentStandard createInstanceWithoutEmptyLastPage() {
        OnboardingFragmentStandard fragment = new OnboardingFragmentStandard();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(false));
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
        mFragmentImpl.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public PagerAdapter getViewPagerAdapter(@NonNull List<OnboardingPage> pages) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            throw new IllegalStateException("Component API requires API Level 17 or higher");
        }
        return new ViewPagerAdapterStandard(getChildFragmentManager(), pages);
    }
}
