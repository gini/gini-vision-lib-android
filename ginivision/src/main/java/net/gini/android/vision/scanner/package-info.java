/**
 * <p>
 * Contains the Activity and Fragments used for the Camera Screen.
 * </p>
 *
 * <h3>Screen API</h3>
 *
 * <p>
 * The {@link net.gini.android.vision.scanner.ScannerActivity} is the main entry point when using the Screen API. Start
 * {@link net.gini.android.vision.scanner.ScannerActivity} and as a result the original {@link
 * net.gini.android.vision.scanner.Document} and the reviewed {@link net.gini.android.vision.scanner.Document} or a
 * {@link net.gini.android.vision.GiniVisionError} is returned.
 * </p>
 *
 * <h3>Component API</h3>
 *
 * <p>
 * To use the Component API you have to include the {@link net.gini.android.vision.scanner.ScannerFragmentStandard} or
 * the {@link net.gini.android.vision.scanner.ScannerFragmentCompat} in an Activity in your app (a dedicated activity is
 * recommended). To receive events from the Fragments your Activity must implement the {@link
 * net.gini.android.vision.scanner.ScannerFragmentListener} interface.
 * </p>
 */
package net.gini.android.vision.scanner;