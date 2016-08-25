package net.gini.android.vision.component;

import android.app.Application;

import net.gini.android.Gini;
import net.gini.android.SdkBuilder;
import net.gini.android.visionadvtest.R;

/**
 * <p>
 *     Facilitates the application wide usage of the Gini API SDK's {@link Gini} instance and a helper
 *     {@link SingleDocumentAnalyzer} instance.
 * </p>
 * <p>
 *     The {@link SingleDocumentAnalyzer} is used to analyze the document in the {@link GiniVisionActivity} or ({@link GiniVisionAppCompatActivity}.
 *     It aids in starting the analysis of the document when the Review Screen starts and continuing it when the
 *     Analysis Screen was shown, if the document wasn't modified. In case it is modified the analysis is cancelled and
 *     only started when the Analysis Screen was shown where the reviewed final document is available.
 * </p>
 */
public class ComponentApiApp extends Application {

    private Gini mGiniApi;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    public SingleDocumentAnalyzer getSingleDocumentAnalyzer() {
        if (mSingleDocumentAnalyzer == null) {
            mSingleDocumentAnalyzer = new SingleDocumentAnalyzer(getGiniApi());
        }
        return mSingleDocumentAnalyzer;
    }

    public Gini getGiniApi() {
        if (mGiniApi == null) {
            createGiniApi();
        }
        return mGiniApi;
    }

    private void createGiniApi() {
        SdkBuilder builder = new SdkBuilder(this,
                this.getString(R.string.gini_api_client_id),
                this.getString(R.string.gini_api_client_secret),
                "example.com");
        mGiniApi = builder.build();
    }

}
