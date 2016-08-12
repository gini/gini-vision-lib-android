package net.gini.android.vision.screen;

import android.app.Application;

import net.gini.android.Gini;
import net.gini.android.SdkBuilder;
import net.gini.android.ginivisiontest.R;

public class ScreenApiApp extends Application {

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
