package net.gini.android.vision.camera;

import android.support.annotation.NonNull;

/**
 * <p>
 *     Methods which both Camera Fragments must implement.
 * </p>
 */
public interface CameraFragmentInterface {
    /**
     * <p>
     *     Call this method to show the document corner guides.
     * </p>
     * <p>
     *     <b>Note:</b> the document corner guides are shown by default.
     * </p>
     *
     * @deprecated Use {@link CameraFragmentInterface#showInterface()} instead.
     */
    @Deprecated
    void showDocumentCornerGuides();

    /**
     * <p>
     *     Call this method to hide the document corner guides.
     * </p>
     * <p>
     *     <b>Note:</b> the document corner guides are shown by default.
     * </p>
     *
     * @deprecated Use {@link CameraFragmentInterface#hideInterface()} instead.
     */
    @Deprecated
    void hideDocumentCornerGuides();

    /**
     * <p>
     *     Call this method to show the camera trigger button.
     * </p>
     * <p>
     *     <b>Note:</b> the camera trigger button is shown by default.
     * </p>
     *
     * @deprecated Use {@link CameraFragmentInterface#showInterface()} instead.
     */
    @Deprecated
    void showCameraTriggerButton();

    /**
     * <p>
     *     Call this method to hide the camera trigger button.
     * </p>
     * <p>
     *     <b>Note:</b> the camera trigger button is shown by default.
     * </p>
     *
     * @deprecated Use {@link CameraFragmentInterface#hideInterface()} instead.
     */
    @Deprecated
    void hideCameraTriggerButton();

    /**
     * <p>
     *     Call this method to show the interface elements. The camera preview is always visible.
     * </p>
     * <p>
     *     <b>Note:</b> the interface elements are shown by default.
     * </p>
     *
     */
    void showInterface();

    /**
     * <p>
     *     Call this method to hide the interface elements. The camera preview remains visible.
     * </p>
     * <p>
     *     <b>Note:</b> the interface elements are shown by default.
     * </p>
     *
     */
    void hideInterface();

    /**
     * <p>
     *     Call this method to show an activity indicator and disable user interaction.
     *     The camera preview remains visible.
     * </p>
     */
    void showActivityIndicatorAndDisableInteraction();

    /**
     * <p>
     *     Call this method to hide the activity indicator and enable user interaction.
     * </p>
     */
    void hideActivityIndicatorAndEnableInteraction();

    /**
     * <p>
     *     Call this method to show an error message to the user in the Camera Screen.
     * </p>
     *
     * @param message  a short error message
     * @param duration how long should the error message be shown in ms
     */
    void showError(@NonNull String message, int duration);
}
