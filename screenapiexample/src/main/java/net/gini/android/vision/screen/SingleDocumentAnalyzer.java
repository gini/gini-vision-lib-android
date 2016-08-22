package net.gini.android.vision.screen;

import net.gini.android.DocumentTaskManager;
import net.gini.android.Gini;
import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.camera.api.UIExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import bolts.Continuation;
import bolts.Task;

/**
 * <p>
 *     Analyzes a single document. If another document has to be analyzed, the running analysis must be cancelled first.
 * </p>
 * <p>
 *     Calling {@link SingleDocumentAnalyzer#analyzeDocument(Document, DocumentAnalysisListener)} will only change the listener, if the running analysis wasn't cancelled with {@link SingleDocumentAnalyzer#cancelAnalysis()}.
 * </p>
 * <p>
 *     Used as an application wide instance (owned by the {@link ScreenApiApp}) in the {@link ReviewActivity} and {@link AnalysisActivity}.
 * </p>
 * <p>
 *     If the document wasn't modified the analysis started in the {@link ReviewActivity} continues in the {@link AnalysisActivity}.
 * </p>
 */
public class SingleDocumentAnalyzer {

    public static final Logger LOG = LoggerFactory.getLogger(SingleDocumentAnalyzer.class);

    private Gini mGiniApi;
    private Analyzer mAnalyzer;
    private UIExecutor mUIExecutor = new UIExecutor();

    public SingleDocumentAnalyzer(Gini giniApi) {
        mGiniApi = giniApi;
    }

    /**
     * <p>
     *     Analyzes a new document only, if there was no previous analysis or the previous one was cancelled.
     * </p>
     */
    public void analyzeDocument(Document document, final DocumentAnalysisListener listener) {
        LOG.debug("Start analyzing document");
        if (mAnalyzer != null) {;
            if (!mAnalyzer.isCancelled()) {
                LOG.debug("Analysis in progress, only changing the listener");
                setListener(listener);
                return;
            }
            mAnalyzer.setListener(null);
        }
        LOG.debug("Starting a new analysis");

        mAnalyzer = new Analyzer(mGiniApi.getDocumentTaskManager());
        setListener(listener);
        mAnalyzer.analyze(document);
    }

    public void cancelAnalysis() {
        LOG.debug("Canceling analysis");
        if (mAnalyzer != null) {
            mAnalyzer.cancel();
            LOG.debug("Analysis cancelled");
        } else {
            LOG.debug("No running analysis to cancel");
        }
    }

    public void removeListener() {
        LOG.debug("Removing listener");
        if (mAnalyzer != null) {
            mAnalyzer.setListener(null);
            LOG.debug("Listener removed");
        } else {
            LOG.debug("No running analysis to remove listener from");
        }
    }

    public void setListener(final DocumentAnalysisListener listener) {
        LOG.debug("Setting listener");
        if (mAnalyzer != null) {
            mAnalyzer.setListener(new DocumentAnalysisListener() {
                @Override
                public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                    logExtractions(extractions);
                    listener.onExtractionsReceived(extractions);
                }

                @Override
                public void onException(Exception exception) {
                    LOG.error("Analysis failed", exception);
                    listener.onException(exception);
                }
            });
            LOG.debug("Listener set");
        } else {
            LOG.debug("No running analysis to set listener on");
        }
    }

    public net.gini.android.models.Document getGiniApiDocument() {
        LOG.debug("Getting Gini Api Document");
        if (mAnalyzer != null){
            if(mAnalyzer.isCompleted()) {
                if (mAnalyzer.getGiniApiDocument() == null) {
                    LOG.debug("No Gini Api Document Available: analysis failed");
                }
                return mAnalyzer.getGiniApiDocument();
            } else {
                LOG.debug("No Gini Api Document Available: analysis in progress");
            }
        } else {
            LOG.debug("No Gini Api Document Available: no analysis started");
        }
        return null;
    }

    public interface DocumentAnalysisListener {
        void onExtractionsReceived(Map<String, SpecificExtraction> extractions);

        void onException(Exception exception);
    }

    private void logExtractions(Map<String, SpecificExtraction> extractions) {
        LOG.debug("Extractions received:");
        StringBuilder stringBuilder = new StringBuilder();
        boolean first = true;
        for (String extraction : extractions.keySet()) {
            if (!first) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(extraction);
            stringBuilder.append(" = ");
            stringBuilder.append(extractions.get(extraction).getValue());
            first = false;
        }
        LOG.debug(stringBuilder.toString());
    }

    private class Analyzer {

        private final Logger LOG = LoggerFactory.getLogger(Analyzer.class);

        private final DocumentTaskManager mDocumentTaskManager;
        private boolean mCancelled = false;
        private DocumentAnalysisListener mListener;
        private Task<Map<String, SpecificExtraction>> mResultTask;
        private net.gini.android.models.Document mGiniApiDocument;

        private Analyzer(DocumentTaskManager documentTaskManager) {
            mDocumentTaskManager = documentTaskManager;
        }

        public synchronized void analyze(Document document) {
            mDocumentTaskManager.createDocument(document.getJpeg(), null, null)
                    .onSuccessTask(new Continuation<net.gini.android.models.Document, Task<net.gini.android.models.Document>>() {
                        @Override
                        public Task<net.gini.android.models.Document> then(Task<net.gini.android.models.Document> task) throws Exception {
                            LOG.debug("Document created");
                            if (isCancelled()) {
                                LOG.debug("Analysis cancelled");
                                return Task.cancelled();
                            }
                            setGiniApiDocument(task.getResult());
                            LOG.debug("Polling document");
                            return mDocumentTaskManager.pollDocument(getGiniApiDocument());
                        }
                    })
                    .onSuccessTask(new Continuation<net.gini.android.models.Document, Task<Map<String, SpecificExtraction>>>() {
                        @Override
                        public Task<Map<String, SpecificExtraction>> then(Task<net.gini.android.models.Document> task) throws Exception {
                            LOG.debug("Document polling done");
                            if (isCancelled()) {
                                LOG.debug("Analysis cancelled");
                                return Task.cancelled();
                            }
                            LOG.debug("Getting extractions");
                            return mDocumentTaskManager.getExtractions(task.getResult());
                        }
                    })
                    .continueWith(new Continuation<Map<String, SpecificExtraction>, Map<String, SpecificExtraction>>() {
                        @Override
                        public Map<String, SpecificExtraction> then(final Task<Map<String, SpecificExtraction>> task) throws Exception {
                            if (isCancelled()) {
                                LOG.debug("Analysis completed with cancellation");
                                return null;
                            }
                            LOG.debug("Analysis completed with {}", task.isFaulted() ? "fault" : "success");
                            setResultTask(task);
                            publishResult();
                            return null;
                        }
                    });
        }

        private synchronized void publishResult() {
            mUIExecutor.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Task<Map<String, SpecificExtraction>> resultTask = getResultTask();
                    DocumentAnalysisListener listener = getListener();
                    if (resultTask == null || isCancelled() || listener == null) {
                        return;
                    }
                    LOG.debug("Publishing result");
                    if (resultTask.isFaulted()) {
                        listener.onException(resultTask.getError());
                    } else {
                        listener.onExtractionsReceived(resultTask.getResult());
                    }
                }
            });
        }

        public synchronized void cancel() {
            mCancelled = true;
            mListener = null;
        }

        public synchronized boolean isCancelled() {
            return mCancelled;
        }

        public synchronized void setListener(DocumentAnalysisListener listener) {
            mListener = listener;
            publishResult();
        }

        public synchronized boolean isCompleted() {
            return mResultTask != null;
        }

        private synchronized void setGiniApiDocument(net.gini.android.models.Document giniApiDocument) {
            mGiniApiDocument = giniApiDocument;
        }

        public synchronized net.gini.android.models.Document getGiniApiDocument() {
            return mGiniApiDocument;
        }

        private synchronized DocumentAnalysisListener getListener() {
            return mListener;
        }

        private synchronized Task<Map<String, SpecificExtraction>> getResultTask() {
            return mResultTask;
        }

        private synchronized void setResultTask(Task<Map<String, SpecificExtraction>> resultTask) {
            mResultTask = resultTask;
        }
    }
}
