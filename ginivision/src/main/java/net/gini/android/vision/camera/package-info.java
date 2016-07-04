/**
 * <p>
 * Contains the Activity and Fragments used for the Camera Screen.
 * </p>
 *
 * <h3>Screen API</h3>
 *
 * <p>
 * The {@link net.gini.android.vision.camera.CameraActivity} is the main entry point when using the Screen API. Start
 * {@link net.gini.android.vision.camera.CameraActivity} and as a result, if something went wrong, a
 * {@link net.gini.android.vision.GiniVisionError} is returned.
 * </p>
 *
 * <h3>Component API</h3>
 *
 * <p>
 * To use the Component API you have to include the {@link net.gini.android.vision.camera.CameraFragmentStandard} or
 * the {@link net.gini.android.vision.camera.CameraFragmentCompat} in an Activity in your app (a dedicated Activity is
 * recommended). To receive events from the Fragments your Activity must implement the {@link
 * net.gini.android.vision.camera.CameraFragmentListener} interface.
 * </p>
 */
package net.gini.android.vision.camera;