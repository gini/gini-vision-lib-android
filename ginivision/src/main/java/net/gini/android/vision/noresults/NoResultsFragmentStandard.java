package net.gini.android.vision.noresults;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
 * NoResultsFragmentStandard} displays hints that show how to best take a picture of a document.
 * </p>
 * <p>
 * Include the {@code NoResultsFragmentStandard} into your layout by using the {@link
 * NoResultsFragmentStandard#createInstance(Document)} factory method to create an instance
 * and display it using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 * Your Activity must implement the {@link NoResultsFragmentListener} interface to receive events
 * from the No Results Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 * Your Activity is automatically set as the listener in
 * {@link NoResultsFragmentStandard#onCreate(Bundle)}.
 * </p>
 */
public class NoResultsFragmentStandard extends Fragment implements FragmentImplCallback {

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
    public static NoResultsFragmentStandard createInstance(@NonNull final Document document) {
        NoResultsFragmentStandard fragment = new NoResultsFragmentStandard();
        fragment.setArguments(NoResultsFragmentHelper.createArguments(document));
        return fragment;
    }

}
