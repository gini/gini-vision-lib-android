package net.gini.android.vision.example;

import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import net.gini.android.Gini;
import net.gini.android.SdkBuilder;
import net.gini.android.vision.network.GiniVisionDefaultNetworkApi;
import net.gini.android.vision.network.GiniVisionDefaultNetworkService;
import net.gini.android.vision.network.GiniVisionNetworkApi;
import net.gini.android.vision.network.GiniVisionNetworkService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 *     Facilitates the application wide usage of the Gini API SDK's {@link Gini} instance and a helper
 *     {@link SingleDocumentAnalyzer} instance.
 * </p>
 * <p>
 *     The {@link SingleDocumentAnalyzer} is used to analyze documents.
 *     It aids in starting the analysis of the document when the Review Screen starts and continuing it when the
 *     Analysis Screen was shown, if the document wasn't modified. In case it is modified the analysis is cancelled and
 *     only started when the Analysis Screen was shown where the reviewed final document is available.
 * </p>
 */
public abstract class BaseExampleApp extends MultiDexApplication {

    private static final Logger LOG = LoggerFactory.getLogger(BaseExampleApp.class);

    private Gini mGiniApi;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;
    private GiniVisionDefaultNetworkService mGiniVisionNetworkService;
    private GiniVisionDefaultNetworkApi mGiniVisionNetworkApi;

    public SingleDocumentAnalyzer getSingleDocumentAnalyzer() {
        if (mSingleDocumentAnalyzer == null) {
            mSingleDocumentAnalyzer = new SingleDocumentAnalyzer(getGiniApi());
        }
        return mSingleDocumentAnalyzer;
    }



    protected abstract String getClientId();

    protected abstract String getClientSecret();

    public Gini getGiniApi() {
        if (mGiniApi == null) {
            createGiniApi();
        }
        return mGiniApi;
    }

    private void createGiniApi() {
        final String clientId = getClientId();
        final String clientSecret = getClientSecret();
        if (TextUtils.isEmpty(clientId) || TextUtils.isEmpty(clientSecret)) {
            LOG.warn(
                    "Missing Gini API client credentials. Either create a local.properties file "
                            + "with clientId and clientSecret properties or pass them in as gradle "
                            + "parameters with -PclientId and -PclientSecret.");
        }
        final SdkBuilder builder = new SdkBuilder(this,
                clientId,
                clientSecret,
                "example.com");
        mGiniApi = builder.build();
    }

    @NonNull
    public GiniVisionNetworkService getGiniVisionNetworkService() {
        final String clientId = getClientId();
        final String clientSecret = getClientSecret();
        if (TextUtils.isEmpty(clientId) || TextUtils.isEmpty(clientSecret)) {
            LOG.warn(
                    "Missing Gini API client credentials. Either create a local.properties file "
                            + "with clientId and clientSecret properties or pass them in as gradle "
                            + "parameters with -PclientId and -PclientSecret.");
        }
        if (mGiniVisionNetworkService == null) {
            mGiniVisionNetworkService = GiniVisionDefaultNetworkService.builder(this)
                    .setClientCredentials(clientId, clientSecret, "example.com")
                    .build();
        }
        return mGiniVisionNetworkService;
    }

    @NonNull
    public GiniVisionNetworkApi getGiniVisionNetworkApi() {
        if (mGiniVisionNetworkApi == null) {
            mGiniVisionNetworkApi = GiniVisionDefaultNetworkApi.builder()
                    .withGiniVisionDefaultNetworkService(mGiniVisionNetworkService)
                    .build();
        }
        return mGiniVisionNetworkApi;
    }
}