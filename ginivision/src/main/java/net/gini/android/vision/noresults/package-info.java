/**
 * <p>
 * Contains the Activity and Fragments used for the No Results Screen. The No Results Screen
 * includes
 * hints how to take a picture in an optimal condition. This screen should be shown when the
 * received results from the Gini API don't include the required extractions.
 * </p>
 *
 * <h3>Screen API</h3>
 *
 * <p>
 * The {@link net.gini.android.vision.noresults.NoResultsActivity} should be shown when there are
 * no
 * extractions available.
 * </p>
 *
 * <h3>Component API</h3>
 *
 * <p>
 * To use the Component API you have to include the
 * {@link net.gini.android.vision.noresults.NoResultsFragmentStandard}
 * or
 * the {@link net.gini.android.vision.noresults.NoResultsFragmentCompat} in an Activity in your app
 * (a dedicated Activity is
 * recommended). To receive events from the Fragments your Activity must implement the {@link
 * net.gini.android.vision.noresults.NoResultsFragmentListener} interface.
 * </p>
 */
package net.gini.android.vision.noresults;