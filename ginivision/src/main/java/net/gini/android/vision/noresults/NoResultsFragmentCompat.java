package net.gini.android.vision.noresults;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

/**
 * <h3>Component API</h3>
 *
 * <p>
 * When you use the Component API without the Android Support Library, the {@code
 * NoResultsFragmentCompat} displays hints that show how to best take a picture of a document.
 * </p>
 * <p>
 * Include the {@code NoResultsFragmentCompat} into your layout by using the {@link
 * NoResultsFragmentCompat#createInstance()} factory method to create an instance
 * and display it using the {@link android.support.v4.app.FragmentManager}.
 * </p>
 * <p>
 * Your Activity must implement the {@link NoResultsFragmentListener} interface to receive events
 * from the No Results Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 * Your Activity is automatically set as the listener in
 * {@link NoResultsFragmentCompat#onAttach(Context)}.
 * </p>
 */
public class NoResultsFragmentCompat extends Fragment implements FragmentImplCallback {

    private NoResultsFragmentImpl mFragmentImpl;

    public NoResultsFragmentCompat() {
        mFragmentImpl = new NoResultsFragmentImpl(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentImpl.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            final Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * <p>
     * Factory method for creating a new instance of the Fragment.
     * </p>
     *
     * @return a new instance of the Fragment
     */
    public static NoResultsFragmentCompat createInstance() {
        NoResultsFragmentCompat fragment = new NoResultsFragmentCompat();
        return fragment;
    }

}
