package net.gini.android.vision.onboarding;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.internal.util.AlertDialogHelperCompat;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

/**
 * <h3>Component API</h3>
 *
 * <p>
 * When you use the Component API with the Android Support Library, the {@code
 * OnboardingFragmentCompat} displays important advice for correctly photographing a document.
 * </p>
 * <p>
 * <b>Note:</b> Your Activity hosting this Fragment must extend the {@link
 * androidx.appcompat.app.AppCompatActivity} and use an AppCompat Theme.
 * </p>
 * <p>
 * Include the {@code OnboardingFragmentCompat} into your layout either directly with {@code
 * <fragment>} in your Activity's layout or using the {@link androidx.fragment.app.FragmentManager}.
 * </p>
 * <p>
 * The default way of showing the Onboarding Screen is as an overlay above the camera preview with a
 * semi-transparent background.
 * </p>
 * <p>
 * By default an empty last page is added to enable the revealing of the camera preview before the
 * Onboarding Screen is dismissed.
 * </p>
 * <p>
 * If you would like to display a different number of pages, you can use the {@link
 * OnboardingFragmentCompat#createInstance(ArrayList)} or {@link OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage(ArrayList)}
 * factory method and provide a list of {@link OnboardingPage} objects.
 * </p>
 * <p>
 * If you would like to disable the appending of the empty last page, you can use the {@link
 * OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage(ArrayList)} or the {@link
 * OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage()} factory method.
 * </p>
 * <p>
 * An {@link OnboardingFragmentListener} instance must be available until the {@code
 * OnboardingFragmentCompat} is attached to an activity. Failing to do so will throw an exception.
 * The listener instance can be provided either implicitly by making the hosting Activity implement
 * the {@link OnboardingFragmentListener} interface or explicitly by setting the listener using
 * {@link OnboardingFragmentCompat#setListener(OnboardingFragmentListener)}.
 * </p>
 * <p>
 * Your Activity is automatically set as the listener in {@link OnboardingFragmentCompat#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customizing the Onboarding Screen</h3>
 *
 * <p>
 * See the {@link OnboardingActivity} for details.
 * </p>
 */
public class OnboardingFragmentCompat extends Fragment implements OnboardingFragmentImplCallback,
        OnboardingFragmentInterface {

    @VisibleForTesting
    OnboardingFragmentImpl mFragmentImpl;
    private OnboardingFragmentListener mListener;

    /**
     * <p>
     * Factory method for creating a new instance of the Fragment using the provided list of
     * onboarding pages.
     * </p>
     * <p>
     * This method prevents the appending of an empty page to your pages.
     * </p>
     * <p>
     * If you don't need a custom number of pages and wish to use the default behaviour, you can use
     * the default constructor of {@link OnboardingFragmentCompat}.
     * </p>
     *
     * @param pages the pages to be shown
     *
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
     * Factory method for creating a new instance of the Fragment using the provided list of
     * onboarding pages.
     * </p>
     * <p>
     * If you would like to prevent the appending of an empty page, you can use the {@link
     * OnboardingFragmentCompat#createInstanceWithoutEmptyLastPage(ArrayList)} factory method.
     * </p>
     * <p>
     * If you don't need a custom number of pages and wish to use the default behaviour, you can use
     * the default constructor of {@link OnboardingFragmentCompat}.
     * </p>
     *
     * @param pages the pages to be shown
     *
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
     * Factory method for creating a new instance of the Fragment without appending an empty page to
     * the default pages.
     * </p>
     * <p>
     * If you wish to use the default behaviour, you can use the default constructor of {@link
     * OnboardingFragmentCompat}.
     * </p>
     *
     * @return a new instance of the Fragment
     */
    public static OnboardingFragmentCompat createInstanceWithoutEmptyLastPage() {
        final OnboardingFragmentCompat fragment = new OnboardingFragmentCompat();
        fragment.setArguments(OnboardingFragmentHelper.createArguments(false));
        return fragment;
    }

    /**
     * @param savedInstanceState
     *
     * @suppress
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = createFragmentImpl();
    }

    @VisibleForTesting
    OnboardingFragmentImpl createFragmentImpl() {
        final OnboardingFragmentImpl fragmentImpl =
                OnboardingFragmentHelper.createFragmentImpl(this, getArguments());
        OnboardingFragmentHelper.setListener(fragmentImpl, getActivity(), mListener);
        return fragmentImpl;
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     *
     * @return
     *
     * @suppress
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

    @Override
    public void showAlertDialog(@NonNull final String message,
            @NonNull final String positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @Nullable final String negativeButtonTitle,
            @Nullable final DialogInterface.OnClickListener negativeButtonClickListener,
            @Nullable final DialogInterface.OnCancelListener cancelListener) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        AlertDialogHelperCompat.showAlertDialog(activity, message, positiveButtonTitle,
                positiveButtonClickListener, negativeButtonTitle, negativeButtonClickListener,
                cancelListener);
    }
}
