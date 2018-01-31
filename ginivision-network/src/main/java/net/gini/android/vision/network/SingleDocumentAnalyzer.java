package net.gini.android.vision.network;

import android.util.Log;

import net.gini.android.Gini;
import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;

import java.util.Map;

/**
 * <p>
 *     Analyzes a single document. If another document has to be analyzed, the running analysis must be cancelled first.
 * </p>
 * <p>
 *     Calling {@link SingleDocumentAnalyzer#analyzeDocument(Document, DocumentAnalyzer.Listener)} will only change the listener, if the running analysis wasn't cancelled with {@link SingleDocumentAnalyzer#cancelAnalysis()}.
 * </p>
 */
class SingleDocumentAnalyzer {

    private final Gini mGiniApi;
    private DocumentAnalyzer mAnalyzer;

    SingleDocumentAnalyzer(final Gini giniApi) {
        mGiniApi = giniApi;
    }

    /**
     * <p>
     *     Analyzes a new document only, if there was no previous analysis or the previous one was completed or cancelled.
     * </p>
     */
    public void analyzeDocument(final Document document, final DocumentAnalyzer.Listener listener) {
        Log.d("gini-api", "Start analyzing document");
        if (mAnalyzer != null) {
            if (!mAnalyzer.isCompleted() && !mAnalyzer.isCancelled()) {
                Log.d("gini-api", "Analysis in progress, only changing the listener");
                setListener(listener);
                return;
            }
            mAnalyzer.setListener(null);
        }
        Log.d("gini-api", "Starting a new analysis");

        mAnalyzer = new DocumentAnalyzer(mGiniApi.getDocumentTaskManager());
        setListener(listener);
        mAnalyzer.analyze(document);
    }

    void setListener(final DocumentAnalyzer.Listener listener) {
        Log.d("gini-api", "Setting listener");
        if (mAnalyzer != null) {
            mAnalyzer.setListener(new DocumentAnalyzer.Listener() {
                @Override
                public void onException(final Exception exception) {
                    Log.e("gini-api", "Analysis failed", exception);
                    listener.onException(exception);
                }

                @Override
                public void onExtractionsReceived(
                        final Map<String, SpecificExtraction> extractions) {
                    logExtractions(extractions);
                    listener.onExtractionsReceived(extractions);
                }
            });
            Log.d("gini-api", "Listener set");
        } else {
            Log.d("gini-api", "No running analysis to set listener on");
        }
    }

    private void logExtractions(final Map<String, SpecificExtraction> extractions) {
        Log.d("gini-api", "Extractions received:");
        final StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (final String extraction : extractions.keySet()) {
            if (!first) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(extraction);
            stringBuilder.append(" = ");
            stringBuilder.append(extractions.get(extraction).getValue());
            first = false;
        }
        Log.d("gini-api", stringBuilder.toString());
    }

    public void cancelAnalysis() {
        Log.d("gini-api", "Canceling analysis");
        if (mAnalyzer != null) {
            mAnalyzer.cancel();
            Log.d("gini-api", "Analysis cancelled");
        } else {
            Log.d("gini-api", "No running analysis to cancel");
        }
    }

    public void removeListener() {
        Log.d("gini-api", "Removing listener");
        if (mAnalyzer != null) {
            mAnalyzer.setListener(null);
            Log.d("gini-api", "Listener removed");
        } else {
            Log.d("gini-api", "No running analysis to remove listener from");
        }
    }

    net.gini.android.models.Document getGiniApiDocument() {
        Log.d("gini-api", "Getting Gini Api Document");
        if (mAnalyzer != null) {
            if (mAnalyzer.isCompleted()) {
                if (mAnalyzer.getGiniApiDocument() == null) {
                    Log.d("gini-api", "No Gini Api Document Available: analysis failed");
                }
                return mAnalyzer.getGiniApiDocument();
            } else {
                Log.d("gini-api", "No Gini Api Document Available: analysis in progress");
            }
        } else {
            Log.d("gini-api", "No Gini Api Document Available: no analysis started");
        }
        return null;
    }

}
