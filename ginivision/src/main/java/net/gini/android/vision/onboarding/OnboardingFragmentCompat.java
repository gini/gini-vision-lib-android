package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
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
 *     When you use the Component API with the Android Support Library, the {@code OnboardingFragmentCompat} displays important advice for correctly photographing a document.
 * </p>
 * <p>
 *     <b>Note:</b> Your Activity hosting this Fragment must extend the {@link android.support.v7.app.AppCompatActivity} and use an AppCompat Theme.
 * </p>
 * <p>
 *     Include the {@code OnboardingFragmentCompat} into your layout either directly with {@code <fragment>} in your Activity's layout or using the {@link android.support.v4.app.FragmentManager}.
 * </p>
 * <p>
 *     The default way of showing the Onboarding Screen is as an overlay above the camera preview with a semi-transparent background.
 * </p>
 * <p>
 *     By default an empty last page is added to enable the revealing of the camera preview before the Onboarding Screen is dismissed.
 * </p>
 * <p>
 *     If you would like to display a different number of pages, you can use the {@link OnboardingFragmentCompat#createInstance(ArrayList)} or {@link OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage(ArrayList)} factory method and provide a list of {@link OnboardingPage} objects.
 * </p>
 * <p>
 *     If you would like to disable the appending of the empty last page, you can use the {@link OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage(ArrayList)} or the {@link OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage()} factory method.
 * </p>
 * <p>
 *     An {@link OnboardingFragmentListener} instance must be available until the {@code OnboardingFragmentCompat} is attached to an activity. Failing to do so will throw an exception.
 *     The listener instance can be provided either implicitly by making the hosting Activity implement the {@link OnboardingFragmentListener} interface or explicitly by
 *     setting the listener using {@link OnboardingFragmentCompat#setListener(OnboardingFragmentListener)}.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link OnboardingFragmentCompat#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customizing the Onboarding Screen</h3>
 *
 * <p>
 *     See the {@link OnboardingActivity} for details.
 * </p>
 */
public class OnboardingFragmentCompat extends Fragment implements OnboardingFragmentImplCallback, OnboardingFragmentInterface {

    @VisibleForTesting
    OnboardingFragmentImpl mFragmentImpl;
    private OnboardingFragmentListener mListener;

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
    public static OnboardingFragmentCompat createInstanceWithoutEmptyLastPage(
            @NonNull final ArrayList<OnboardingPage> pages) { // NOPMD - ArrayList required (Bundle)
        final OnboardingFragmentCompat fragment = new OnboardingFragmentCompat();
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
    public static OnboardingFragmentCompat createInstance(
            @NonNull final ArrayList<OnboardingPage> pages) { // NOPMD - ArrayList required (Bundle)
        final OnboardingFragmentCompat fragment = new OnboardingFragmentCompat();
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
    public static OnboardingFragmentCompat createInstanceWithoutEmptyLastPage() {
        final OnboardingFragmentCompat fragment = new OnboardingFragmentCompat();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(false));
        return fragment;
    }

    /**
     * @exclude
     * @param savedInstanceState
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = OnboardingFragmentHelper.createFragmentImpl(this, getArguments());
        OnboardingFragmentHelper.setListener(mFragmentImpl, getActivity(), mListener);
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
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public PagerAdapter getViewPagerAdapter(@NonNull final List<OnboardingPage> pages) {
        return new ViewPagerAdapterCompat(getChildFragmentManager(), pages);
    }

    @Override
    public void setListener(@NonNull final OnboardingFragmentListener listener) {
        if (mFragmentImpl != null) {
            mFragmentImpl.setListener(listener);
        }
        mListener = listener;
    }
}
