package net.gini.android.vision.noresults;

/**
 * <p>
 * Interface used by {@link NoResultsFragmentStandard} and {@link NoResultsFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface NoResultsFragmentListener {

    /**
     * <p>
     *     Called when the button on the bottom of the No Results Screen was pressed. This button
     *     should lead the user back to the Camera Screen.
     * </p>
     */
    void onBackToCameraPressed();
}
