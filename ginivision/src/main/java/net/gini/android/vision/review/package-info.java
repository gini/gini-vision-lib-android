/**
 * Contains the Activity and Fragments used for the Review Screen.
 *
 * <h3>Screen API</h3>
 *
 * <p> Extending the {@code ReviewActivity} in your application has been deprecated. The preferred
 * way of adding network calls to the Gini Vision Library is by creating a {@link net.gini.android.vision.GiniVision}
 * instance with a {@link net.gini.android.vision.network.GiniVisionNetworkService} and a {@link net.gini.android.vision.network.GiniVisionNetworkApi}
 * implementation.
 *
 * <p> <b>Note:</b> {@link net.gini.android.vision.review.ReviewActivity} extends {@link
 * android.support.v7.app.AppCompatActivity}, therefore you have to use an {@code AppCompat} theme
 * for your {@link net.gini.android.vision.review.ReviewActivity} subclass.
 *
 * <h3>Component API</h3>
 *
 * <p> To use the Component API you have to include the {@link net.gini.android.vision.review.ReviewFragmentStandard}
 * or the {@link net.gini.android.vision.review.ReviewFragmentCompat} in an Activity in your app (a
 * dedicated Activity is recommended). To receive events from the Fragments your Activity must
 * implement the {@link net.gini.android.vision.review.ReviewFragmentListener} interface.
 */
package net.gini.android.vision.review;