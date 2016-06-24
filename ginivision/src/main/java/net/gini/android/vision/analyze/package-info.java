/**
 * <p>
 * Contains the Activity and Fragments used for the Analysis Screen.
 * </p>
 *
 * <h3>Screen API</h3>
 *
 * <p>
 * The {@link net.gini.android.vision.analyze.AnalyzeDocumentActivity} is an abstract Activity which you need to
 * extend in your application. By implementing the abstract methods you can handle events coming from the Gini Vision
 * Lib.
 * </p>
 *
 * <p>
 * <b>Note:</b> {@link net.gini.android.vision.analyze.AnalyzeDocumentActivity} extends {@link
 * android.support.v7.app.AppCompatActivity}, therefore you have to use an {@code AppCompat} theme for your {@link
 * net.gini.android.vision.analyze.AnalyzeDocumentActivity} subclass.
 * </p>
 *
 * <h3>Component API</h3>
 *
 * <p>
 * To use the Component API you have to include the {@link net.gini.android.vision.analyze.AnalyzeDocumentFragmentStandard}
 * or
 * the {@link net.gini.android.vision.analyze.AnalyzeDocumentFragmentCompat} in an Activity in your app (a
 * dedicated activity is
 * recommended). To receive events from the Fragments your Activity must implement the {@link
 * net.gini.android.vision.analyze.AnalyzeDocumentFragmentListener} interface.
 * </p>
 */
package net.gini.android.vision.analyze;