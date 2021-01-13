package net.gini.android.vision.analysis;

import static net.gini.android.vision.internal.util.NullabilityHelper.getMapOrEmpty;
import static net.gini.android.vision.tracking.EventTrackingHelper.trackAnalysisScreenEvent;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.internal.camera.photo.ParcelableMemoryCache;
import net.gini.android.vision.internal.document.DocumentRenderer;
import net.gini.android.vision.internal.document.DocumentRendererFactory;
import net.gini.android.vision.internal.storage.ImageDiskStore;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.util.FileImportHelper;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.tracking.AnalysisScreenEvent;
import net.gini.android.vision.tracking.AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 08.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class AnalysisScreenPresenter extends AnalysisScreenContract.Presenter {

    @VisibleForTesting
    static final String PARCELABLE_MEMORY_CACHE_TAG = "ANALYSIS_FRAGMENT";
    private static final Logger LOG = LoggerFactory.getLogger(AnalysisScreenPresenter.class);
    private static final AnalysisFragmentListener NO_OP_LISTENER = new AnalysisFragmentListener() {
        @Override
        public void onAnalyzeDocument(@NonNull final Document document) {
        }

        @Override
        public void onError(@NonNull final GiniVisionError error) {
        }

        @Override
        public void onExtractionsAvailable(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        }

        @Override
        public void onProceedToNoExtractionsScreen(@NonNull final Document document) {
        }

        @Override
        public void onDefaultPDFAppAlertDialogCancelled() {
        }

    };

    private final GiniVisionMultiPageDocument<GiniVisionDocument, GiniVisionDocumentError>
            mMultiPageDocument;
    private final String mDocumentAnalysisErrorMessage;
    private final AnalysisInteractor mAnalysisInteractor;
    private final List<AnalysisHint> mHints;
    @VisibleForTesting
    DocumentRenderer mDocumentRenderer;
    private AnalysisFragmentListener mListener;
    private boolean mStopped;
    private boolean mAnalysisCompleted;

    AnalysisScreenPresenter(
            @NonNull final Activity activity,
            @NonNull final AnalysisScreenContract.View view,
            @NonNull final Document document,
            @Nullable final String documentAnalysisErrorMessage) {
        this(activity, view, document, documentAnalysisErrorMessage,
                new AnalysisInteractor(activity.getApplication()));
    }

    @VisibleForTesting
    AnalysisScreenPresenter(
            @NonNull final Activity activity,
            @NonNull final AnalysisScreenContract.View view,
            @NonNull final Document document,
            @Nullable final String documentAnalysisErrorMessage,
            @NonNull final AnalysisInteractor analysisInteractor) {
        super(activity, view);
        view.setPresenter(this);
        mMultiPageDocument = asMultiPageDocument(document);
        // Tag the documents to be able to clean up the automatically parcelled data
        tagDocumentsForParcelableMemoryCache(document, mMultiPageDocument);
        mDocumentAnalysisErrorMessage = documentAnalysisErrorMessage;
        mAnalysisInteractor = analysisInteractor;
        mHints = generateRandomHintsList();
    }

    private List<AnalysisHint> generateRandomHintsList() {
        final List<AnalysisHint> list = AnalysisHint.getArray();
        Collections.shuffle(list, new Random());
        return list;
    }

    private void tagDocumentsForParcelableMemoryCache(
            @NonNull final Document document,
            @NonNull final GiniVisionMultiPageDocument<GiniVisionDocument, GiniVisionDocumentError>
                    multiPageDocument) {
        if (document instanceof GiniVisionDocument) {
            ((GiniVisionDocument) document).setParcelableMemoryCacheTag(
                    PARCELABLE_MEMORY_CACHE_TAG);
        }
        multiPageDocument.setParcelableMemoryCacheTag(PARCELABLE_MEMORY_CACHE_TAG);
    }

    @SuppressWarnings("unchecked")
    private GiniVisionMultiPageDocument<GiniVisionDocument,
            GiniVisionDocumentError> asMultiPageDocument(@NonNull final Document document) {
        if (!(document instanceof GiniVisionMultiPageDocument)) {
            return DocumentFactory.newMultiPageDocument((GiniVisionDocument) document);
        } else {
            return (GiniVisionMultiPageDocument) document;
        }
    }

    @Override
    void finish() {
        clearParcelableMemoryCache();
    }

    @VisibleForTesting
    void clearParcelableMemoryCache() {
        // Remove data from the memory cache. The data had been added when the document in the
        // arguments was automatically parcelled when the activity was stopped
        ParcelableMemoryCache.getInstance().removeEntriesWithTag(PARCELABLE_MEMORY_CACHE_TAG);
    }

    @Override
    public void hideError() {
        getView().hideErrorSnackbar();
    }

    @Override
    public void onNoExtractionsFound() {
        // Nothing to do here. We don't need to update the UI or do anything else here.
        // The client has to decide what to do when there are no extractions
        // when using the Component API.
        // For the Screen API there is an implementation of this interface method in the
        // AnalysisActivity.
    }

    @Override
    public void onDocumentAnalyzed() {
        mAnalysisCompleted = true;
        clearSavedImages();
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        getView().showErrorSnackbar(message, duration, null, null);
    }

    @Override
    public void showError(@NonNull final String message, @NonNull final String buttonTitle,
            @NonNull final View.OnClickListener onClickListener) {
        getView().showErrorSnackbar(message, ErrorSnackbar.LENGTH_INDEFINITE, buttonTitle,
                onClickListener);
    }

    @Override
    public void startScanAnimation() {
        getView().showScanAnimation();
    }

    @Override
    public void stopScanAnimation() {
        getView().hideScanAnimation();
    }

    @Override
    public void setListener(@NonNull final AnalysisFragmentListener listener) {
        mListener = listener;
    }

    @VisibleForTesting
    void clearSavedImages() {
        ImageDiskStore.clear(getActivity());
    }

    @Override
    public void start() {
        mStopped = false;
        createDocumentRenderer();
        clearParcelableMemoryCache();
        getView().showScanAnimation();
        loadDocumentData();
        showHintsForImage();
    }

    @Override
    public void stop() {
        mStopped = true;
        stopScanAnimation();
        if (!mAnalysisCompleted) {
            deleteUploadedDocuments();
        } else if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getImageMultiPageDocumentMemoryStore()
                    .clear();
        }
    }

    private void deleteUploadedDocuments() {
        if (mMultiPageDocument.getType() == Document.Type.PDF_MULTI_PAGE) {
            // Delete PDF partial documents here because the Camera Screen
            // doesn't keep references to them
            mAnalysisInteractor.deleteMultiPageDocument(mMultiPageDocument);
        } else {
            // Delete only the composite document
            mAnalysisInteractor.deleteDocument(mMultiPageDocument);
        }
    }

    @VisibleForTesting
    boolean isStopped() {
        return mStopped;
    }

    @VisibleForTesting
    GiniVisionMultiPageDocument<
            GiniVisionDocument, GiniVisionDocumentError> getMultiPageDocument() {
        return mMultiPageDocument;
    }

    @VisibleForTesting
    List<AnalysisHint> getHints() {
        return mHints;
    }

    @VisibleForTesting
    void createDocumentRenderer() {
        final GiniVisionDocument documentToRender = getFirstDocument();
        if (documentToRender != null) {
            mDocumentRenderer = DocumentRendererFactory.fromDocument(documentToRender);
        }
    }

    @Nullable
    @VisibleForTesting
    String getPdfFilename(final PdfDocument pdfDocument) {
        final Uri uri = pdfDocument.getUri();
        try {
            return UriHelper.getFilenameFromUri(uri, getActivity());
        } catch (final IllegalStateException e) { // NOPMD
            // Ignore
        }
        return null;
    }

    @VisibleForTesting
    void analyzeDocument() {
        if (MimeType.APPLICATION_PDF.asString().equals(mMultiPageDocument.getMimeType())) {
            showAlertIfOpenWithDocumentAndAppIsDefault(mMultiPageDocument,
                    new FileImportHelper.ShowAlertCallback() {
                        @Override
                        public void showAlertDialog(@NonNull final String message,
                                @NonNull final String positiveButtonTitle,
                                @NonNull final DialogInterface.OnClickListener
                                        positiveButtonClickListener,
                                @Nullable final String negativeButtonTitle,
                                @Nullable final DialogInterface.OnClickListener
                                        negativeButtonClickListener,
                                @Nullable final DialogInterface.OnCancelListener cancelListener) {
                            getView().showAlertDialog(message, positiveButtonTitle,
                                    positiveButtonClickListener, negativeButtonTitle,
                                    negativeButtonClickListener, cancelListener);
                        }
                    })
                    .handle(new CompletableFuture.BiFun<Void, Throwable, Void>() {
                        @Override
                        public Void apply(final Void aVoid, final Throwable throwable) {
                            if (throwable != null) {
                                getAnalysisFragmentListenerOrNoOp()
                                        .onDefaultPDFAppAlertDialogCancelled();
                            } else {
                                showErrorIfAvailableAndAnalyzeDocument();
                            }
                            return null;
                        }
                    });
        } else {
            showErrorIfAvailableAndAnalyzeDocument();
        }
    }

    @VisibleForTesting
    CompletableFuture<Void> showAlertIfOpenWithDocumentAndAppIsDefault(
            @NonNull final GiniVisionDocument document,
            @NonNull final FileImportHelper.ShowAlertCallback showAlertCallback) {
        return FileImportHelper.showAlertIfOpenWithDocumentAndAppIsDefault(getActivity(), document,
                showAlertCallback);
    }

    @VisibleForTesting
    void doAnalyzeDocument() {
        startScanAnimation();
        mAnalysisInteractor.analyzeMultiPageDocument(mMultiPageDocument)
                .handle(new CompletableFuture.BiFun<
                        AnalysisInteractor.ResultHolder, Throwable, Void>() {
                    @Override
                    public Void apply(final AnalysisInteractor.ResultHolder resultHolder,
                            final Throwable throwable) {
                        stopScanAnimation();
                        if (throwable != null) {
                            handleAnalysisError(throwable);
                            return null;
                        }
                        final AnalysisInteractor.Result result = resultHolder.getResult();
                        switch (result) {
                            case SUCCESS_NO_EXTRACTIONS:
                                mAnalysisCompleted = true;
                                getAnalysisFragmentListenerOrNoOp()
                                        .onProceedToNoExtractionsScreen(mMultiPageDocument);
                                break;
                            case SUCCESS_WITH_EXTRACTIONS:
                                mAnalysisCompleted = true;
                                if (resultHolder.getExtractions().isEmpty()) {
                                    getAnalysisFragmentListenerOrNoOp()
                                            .onProceedToNoExtractionsScreen(mMultiPageDocument);
                                    return null;
                                }
                                    getAnalysisFragmentListenerOrNoOp()
                                            .onExtractionsAvailable(getMapOrEmpty(resultHolder.getExtractions()));

                            case NO_NETWORK_SERVICE:
                                getAnalysisFragmentListenerOrNoOp().onAnalyzeDocument(
                                        getFirstDocument());
                                break;
                            default:
                                throw new UnsupportedOperationException(
                                        "Unknown AnalysisInteractor result: " + result);
                        }
                        if (result != AnalysisInteractor.Result.NO_NETWORK_SERVICE) {
                            clearSavedImages();
                        }
                        return null;
                    }
                });
    }

    private void loadDocumentData() {
        LOG.debug("Loading document data");
        mMultiPageDocument.loadData(getActivity(),
                new AsyncCallback<byte[], Exception>() {
                    @Override
                    public void onSuccess(final byte[] result) {
                        LOG.debug("Document data loaded");
                        if (isStopped()) {
                            return;
                        }
                        getView().waitForViewLayout()
                                .thenRun(new Runnable() {
                                    @Override
                                    public void run() {
                                        onViewLayoutFinished();
                                    }
                                });
                    }

                    @Override
                    public void onError(final Exception exception) {
                        LOG.error("Failed to load document data", exception);
                        if (isStopped()) {
                            return;
                        }
                        getAnalysisFragmentListenerOrNoOp().onError(
                                new GiniVisionError(GiniVisionError.ErrorCode.ANALYSIS,
                                        "An error occurred while loading the document."));
                    }

                    @Override
                    public void onCancelled() {
                        // Not used
                    }
                });
    }

    private void onViewLayoutFinished() {
        LOG.debug("View layout finished");
        showPdfInfoForPdfDocument();
        showDocument();
        analyzeDocument();
    }

    @NonNull
    private AnalysisFragmentListener getAnalysisFragmentListenerOrNoOp() {
        return mListener != null ? mListener : NO_OP_LISTENER;
    }

    private void showHintsForImage() {
        if (getFirstDocument().getType() == Document.Type.IMAGE) {
            getView().showHints(mHints);
        }
    }

    private GiniVisionDocument getFirstDocument() {
        return mMultiPageDocument.getDocuments().get(0);
    }

    private void showPdfInfoForPdfDocument() {
        final GiniVisionDocument documentToRender = getFirstDocument();
        if (documentToRender instanceof PdfDocument) {
            final PdfDocument pdfDocument = (PdfDocument) documentToRender;
            getView().showPdfInfoPanel();
            final String filename = getPdfFilename(pdfDocument);
            if (filename != null) {
                getView().showPdfTitle(filename);
            }
            mDocumentRenderer.getPageCount(getActivity(), new AsyncCallback<Integer, Exception>() {
                @Override
                public void onSuccess(final Integer result) {
                    if (result > 0) {
                        final String pageCount = getActivity().getResources().getQuantityString(
                                R.plurals.gv_analysis_pdf_pages, result, result);
                        getView().showPdfPageCount(pageCount);
                    } else {
                        getView().hidePdfPageCount();
                    }
                }

                @Override
                public void onError(final Exception exception) {
                    getView().hidePdfPageCount();
                }

                @Override
                public void onCancelled() {
                    // Not used
                }
            });
        }
    }

    private void showDocument() {
        LOG.debug("Rendering the document");
        mDocumentRenderer.toBitmap(getActivity(), getView().getPdfPreviewSize(),
                new DocumentRenderer.Callback() {
                    @Override
                    public void onBitmapReady(@Nullable final Bitmap bitmap,
                            final int rotationForDisplay) {
                        LOG.debug("Document rendered");
                        if (isStopped()) {
                            return;
                        }
                        getView().showBitmap(bitmap, rotationForDisplay);
                    }
                });
    }

    private void showErrorIfAvailableAndAnalyzeDocument() {
        if (mDocumentAnalysisErrorMessage != null) {
            final Map<String, Object> errorDetails = new HashMap<>();
            errorDetails.put(ERROR_DETAILS_MAP_KEY.MESSAGE, mDocumentAnalysisErrorMessage);

            if (GiniVision.hasInstance()) {
                final Throwable reviewScreenAnalysisError = GiniVision.getInstance().internal().getReviewScreenAnalysisError();
                if (reviewScreenAnalysisError != null) {
                    errorDetails.put(ERROR_DETAILS_MAP_KEY.ERROR_OBJECT, reviewScreenAnalysisError);
                }
            }

            trackAnalysisScreenEvent(AnalysisScreenEvent.ERROR, errorDetails);
            showError(mDocumentAnalysisErrorMessage,
                    getActivity().getString(
                            R.string.gv_document_analysis_error_retry),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            trackAnalysisScreenEvent(AnalysisScreenEvent.RETRY);
                            doAnalyzeDocument();
                        }
                    });
        } else {
            doAnalyzeDocument();
        }
    }

    private void handleAnalysisError(final Throwable throwable) {
        final Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put(ERROR_DETAILS_MAP_KEY.MESSAGE, throwable.getMessage());
        errorDetails.put(ERROR_DETAILS_MAP_KEY.ERROR_OBJECT, throwable);
        trackAnalysisScreenEvent(AnalysisScreenEvent.ERROR, errorDetails);
        showError(getActivity().getString(R.string.gv_document_analysis_error),
                getActivity().getString(R.string.gv_document_analysis_error_retry),
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        trackAnalysisScreenEvent(AnalysisScreenEvent.RETRY);
                        doAnalyzeDocument();
                    }
                });
    }
}
