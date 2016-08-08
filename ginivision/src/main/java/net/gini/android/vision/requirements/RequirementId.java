package net.gini.android.vision.requirements;

/**
 * <p>
 *     Enumerates the checked requirements for the Gini Vision Library.
 * </p>
 */
public enum RequirementId {
    /**
     * <p>
     *     Permission to access the camera is required.
     * </p>
     * <p>
     *     <b>Note:</b> On Android 6.0 and later you may ignore the unfulfillment of the camera permission. The Camera Screen
     *     handles the lack of the camera permission and allows the user to access the application settings to grant the
     *     permission.
     * </p>
     * <p>
     *     On Android before version 6.0 the camera permission must be declared
     *     in the manifest.
     * </p>
     * <p>
     *     On Android 6.0 and later in addition to the manifest declaration the camera permission must be granted by
     *     the user during run-time.
     * </p>
     * <p>
     *     Although the Camera Screen handles the lack of the camera permission, we recommend asking the user for the
     *     permission (and explaining why it's needed) before you start the Gini Vision Library.
     * </p>
     */
    CAMERA_PERMISSION,
    /**
     * <p>
     *    A back-facing camera needs to be present.
     * </p>
     */
    CAMERA,
    /**
     * <p>
     *    The camera must support a preview resolution with a 4:3 aspect ratio and a picture resolution higher than 8MP
     *    with an aspect ratio of 4:3.
     * </p>
     */
    CAMERA_RESOLUTION,
    /**
     * <p>
     *     The camera must support always-on flash mode.
     * </p>
     */
    CAMERA_FLASH,
    /**
     * <p>
     *     The camera must support continuous picture focus and auto focus modes.
     * </p>
     */
    CAMERA_FOCUS,
    /**
     * <p>
     *     The device must have sufficient memory for in-memory bitmap handling, compression and jpeg decompression.
     * </p>
     */
    DEVICE_MEMORY
}
