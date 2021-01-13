# Package net.gini.android.vision.review

Contains the Activity and Fragments used for the Review Screen.

## Screen API

Extending the `ReviewActivity` in your application has been deprecated. The preferred way of adding network calls to the Gini Vision Library
is by creating a [net.gini.android.vision.GiniVision] instance with a [net.gini.android.vision.network.GiniVisionNetworkService] and a
[net.gini.android.vision.network.GiniVisionNetworkApi] implementation.

**Note:** [net.gini.android.vision.review.ReviewActivity] extends [androidx.appcompat.app.AppCompatActivity], therefore you have to use an
`AppCompat` theme for your [net.gini.android.vision.review.ReviewActivity] subclass.

## Component API

To use the Component API you have to include the [net.gini.android.vision.review.ReviewFragmentStandard] or the
[net.gini.android.vision.review.ReviewFragmentCompat] in an Activity in your app (a dedicated Activity is recommended). To receive events
from the Fragments your Activity must implement the [net.gini.android.vision.review.ReviewFragmentListener] interface.

