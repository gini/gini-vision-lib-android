/**
 * Contains interfaces and classes for adding networking calls to the Gini Vision Library in order
 * to communicate with the Gini API.
 *
 * <p> The Gini Vision Library uses the {@link net.gini.android.vision.network.GiniVisionNetworkService}
 * interface to request network calls when required. By implementing the interface and passing it to
 * the {@link net.gini.android.vision.GiniVision.Builder#setGiniVisionNetworkService(net.gini.android.vision.network.GiniVisionNetworkService)}
 * when creating the {@link net.gini.android.vision.GiniVision} instance clients are free to use any
 * networking implementation that fits their needs.
 *
 * <p> The {@link net.gini.android.vision.network.GiniVisionNetworkApi} can be implemented and used
 * to perform network calls manually outside of the Gini Vision Library (e.g. for sending feedback).
 *
 * <p> The easiest way to get started is to use the Gini Vision Library Network package which
 * provides a default implementation of both interfaces.
 */
package net.gini.android.vision.network;
