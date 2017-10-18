package net.gini.android.vision.noresults;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.Document;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = NoResultsFragmentHelper.createFragmentImpl(this, getArguments());
        NoResultsFragmentHelper.setListener(mFragmentImpl, getActivity());
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
     * @param document a {@link Document} for which no valid extractions were received
     * @return a new instance of the Fragment
     */
    public static NoResultsFragmentCompat createInstance(@NonNull final Document document) {
        NoResultsFragmentCompat fragment = new NoResultsFragmentCompat();
        fragment.setArguments(NoResultsFragmentHelper.createArguments(document));
        return fragment;
    }

}
