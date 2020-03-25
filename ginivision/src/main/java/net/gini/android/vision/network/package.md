# Package net.gini.android.vision.network

Contains interfaces and classes for adding networking calls to the Gini Vision Library in order to communicate with the Gini API.

The Gini Vision Library uses the [net.gini.android.vision.network.GiniVisionNetworkService] interface to request network calls when
required. By implementing the interface and passing it to the [net.gini.android.vision.GiniVision.Builder.setGiniVisionNetworkService()]
when creating the [net.gini.android.vision.GiniVision] instance clients are free to use any networking implementation that fits their needs.

The [net.gini.android.vision.network.GiniVisionNetworkApi] can be implemented and used to perform network calls manually outside of the Gini
Vision Library (e.g. for sending feedback).

The easiest way to get started is to use the Gini Vision Network Library package which provides a default implementation of both interfaces.