package net.gini.android.vision.example;

import android.app.Application;
import android.text.TextUtils;

import net.gini.android.vision.GiniVisionApplication;
import net.gini.android.vision.network.GiniVisionNetwork;
import net.gini.android.vision.network.GiniVisionNetworkHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseExampleApp extends Application implements GiniVisionApplication {

    private static final Logger LOG = LoggerFactory.getLogger(BaseExampleApp.class);

    private GiniVisionNetworkHandler mGiniVisionNetworkHandler;

    protected abstract String getClientId();

    protected abstract String getClientSecret();

    @Override
    public GiniVisionNetwork getGiniVisionNetwork() {
        final String clientId = getClientId();
        final String clientSecret = getClientSecret();
        if (TextUtils.isEmpty(clientId) || TextUtils.isEmpty(clientSecret)) {
            LOG.warn(
                    "Missing Gini API client credentials. Either create a local.properties file "
                            + "with clientId and clientSecret properties or pass them in as gradle "
                            + "parameters with -PclientId and -PclientSecret.");
        }
        if (mGiniVisionNetworkHandler == null) {
            mGiniVisionNetworkHandler = GiniVisionNetworkHandler.builder(this)
                    .setClientCredentials(clientId, clientSecret, "example.com")
                    .build();
        }
        return mGiniVisionNetworkHandler;
    }
}