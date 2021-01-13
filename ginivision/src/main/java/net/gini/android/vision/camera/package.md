# Package net.gini.android.vision.camera


Contains the Activity and Fragments used for the Camera Screen.

## Screen API

The [net.gini.android.vision.camera.CameraActivity] is the main entry point when using the Screen API. Start
[net.gini.android.vision.camera.CameraActivity] and as a result, if something went wrong, a [net.gini.android.vision.GiniVisionError] is
returned.

## Component API

To use the Component API you have to include the [net.gini.android.vision.camera.CameraFragmentStandard] or the
[net.gini.android.vision.camera.CameraFragmentCompat] in an Activity in your app (a dedicated Activity is recommended). To receive events
from the Fragments your Activity must implement the [net.gini.android.vision.camera.CameraFragmentListener] interface.

