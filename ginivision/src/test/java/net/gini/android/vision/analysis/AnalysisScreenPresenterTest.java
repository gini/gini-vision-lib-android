package net.gini.android.vision.analysis;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.GiniVisionHelper;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageDocumentFake;
import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.document.PdfDocumentFake;
import net.gini.android.vision.internal.document.DocumentRenderer;
import net.gini.android.vision.internal.document.ImageMultiPageDocumentMemoryStore;
import net.gini.android.vision.internal.ui.ErrorSnackbar;
import net.gini.android.vision.internal.util.FileImportHelper;
import net.gini.android.vision.internal.util.Size;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.tracking.AnalysisScreenEvent;
import net.gini.android.vision.tracking.Event;
import net.gini.android.vision.tracking.EventTracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 10.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
@RunWith(AndroidJUnit4.class)
public class AnalysisScreenPresenterTest {

    @Mock
    private Activity mActivity;
    @Mock
    private AnalysisScreenContract.View mView;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        GiniVisionHelper.setGiniVisionInstance(null);
    }

    @Test
    public void should_convertSinglePageDocument_intoMultiPage() throws Exception {
        // Given
        final GiniVisionDocument document = DocumentFactory.newEmptyImageDocument(
                Document.Source.newCameraSource(), Document.ImportMethod.NONE);

        // When
        final AnalysisScreenPresenter presenter = createPresenter(document);

        // Then
        final GiniVisionDocument documentInMultiPage =
                presenter.getMultiPageDocument().getDocuments().get(0);
        assertThat(documentInMultiPage).isEqualTo(document);
    }

    private AnalysisScreenPresenter createPresenter(
            @NonNull final Document document) {
        return createPresenter(document, (String) null);
    }

    private AnalysisScreenPresenter createPresenter(
            @NonNull final Document document,
            @Nullable final String documentAnalysisErrorMessage) {
        return createPresenter(document, null, 0, 0, null, documentAnalysisErrorMessage);
    }

    private AnalysisScreenPresenter createPresenter(
            @NonNull final Document document,
            @Nullable final Bitmap bitmap, final int rotationForDisplay, final int pdfPageCount,
            @Nullable final Exception pdfPageCountError,
            @Nullable final String documentAnalysisErrorMessage) {
        return createPresenter(document, bitmap, rotationForDisplay, pdfPageCount,
                pdfPageCountError, documentAnalysisErrorMessage, null);
    }

    private AnalysisScreenPresenter createPresenter(
            @NonNull final Document document,
            @Nullable final Bitmap bitmap, final int rotationForDisplay, final int pdfPageCount,
            @Nullable final Exception pdfPageCountError,
            @Nullable final String documentAnalysisErrorMessage,
            @Nullable final AnalysisInteractor analysisInteractor) {
        when(mView.waitForViewLayout()).thenReturn(CompletableFuture.<Void>completedFuture(null));
        final DocumentRenderer documentRenderer = new DocumentRenderer() {
            @Override
            public void toBitmap(@NonNull final Context context,
                    @NonNull final Size targetSize,
                    @NonNull final Callback callback) {
                callback.onBitmapReady(bitmap, rotationForDisplay);
            }

            @Override
            public void getPageCount(@NonNull final Context context,
                    @NonNull final AsyncCallback<Integer, Exception> asyncCallback) {
                if (pdfPageCountError == null) {
                    asyncCallback.onSuccess(pdfPageCount);
                } else {
                    asyncCallback.onError(pdfPageCountError);
                }
            }
        };
        if (analysisInteractor == null) {
            return new AnalysisScreenPresenter(mActivity, mView, document,
                    documentAnalysisErrorMessage) {
                @Override
                void createDocumentRenderer() {
                    mDocumentRenderer = documentRenderer;
                }
            };
        } else {
            return new AnalysisScreenPresenter(mActivity, mView, document,
                    documentAnalysisErrorMessage,
                    analysisInteractor) {
                @Override
                void createDocumentRenderer() {
                    mDocumentRenderer = documentRenderer;
                }
            };
        }
    }

    @Test
    public void should_tagDocuments_forParcelableMemoryCache() throws Exception {
        // Given
        final GiniVisionDocument document = DocumentFactory.newEmptyImageDocument(
                Document.Source.newCameraSource(), Document.ImportMethod.NONE);

        // When
        final AnalysisScreenPresenter presenter = createPresenter(document);

        // Then
        assertThat(document.getParcelableMemoryCacheTag())
                .isEqualTo(AnalysisScreenPresenter.PARCELABLE_MEMORY_CACHE_TAG);
        assertThat(presenter.getMultiPageDocument().getParcelableMemoryCacheTag())
                .isEqualTo(AnalysisScreenPresenter.PARCELABLE_MEMORY_CACHE_TAG);
    }

    @Test
    public void should_generateHintsList_withRandomOrder() throws Exception {
        // Given
        final List<AnalysisScreenPresenter> presenters = new ArrayList<>();
        final int nrOfPresenters = 5;
        for (int i = 0; i < nrOfPresenters; i++) {
            presenters.add(createPresenterWithEmptyImageDocument());
        }

        // Then
        assertHaveDifferentHintOrders(presenters);
    }

    private AnalysisScreenPresenter createPresenterWithEmptyImageDocument() {
        final GiniVisionDocument document = DocumentFactory.newEmptyImageDocument(
                Document.Source.newCameraSource(), Document.ImportMethod.NONE);
        document.setData(new byte[42]);
        return createPresenter(document);
    }

    private void assertHaveDifferentHintOrders(final List<AnalysisScreenPresenter> presenters) {
        final List<AnalysisHint> hints1 = presenters.get(0).getHints();
        int countSamePosition = 0;
        for (int i = 0; i < hints1.size(); i++) {
            for (int j = 0; j < presenters.size(); j++) {
                final AnalysisScreenPresenter lhs = presenters.get(j);
                for (int k = j + 1; k < presenters.size(); k++) {
                    final AnalysisScreenPresenter rhs = presenters.get(k);
                    if (lhs.getHints().get(i).equals(rhs.getHints().get(i))) {
                        countSamePosition++;
                    }
                }
            }
        }
        final int nrOfComparisons = presenters.size() - 1;
        final int nrOfPairwiseComparisons = (int) ((nrOfComparisons / 2.0) * (nrOfComparisons + 1));
        final int samePositionCountIfSameOrder = nrOfPairwiseComparisons * hints1.size();
        assertThat(countSamePosition).isLessThan(samePositionCountIfSameOrder);
    }

    @Test
    public void should_hideError() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = createPresenterWithEmptyImageDocument();

        // When
        presenter.hideError();

        // Then
        verify(mView).hideErrorSnackbar();
    }

    @Test
    public void should_showError() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = createPresenterWithEmptyImageDocument();

        // When
        final String message = "Error message";
        final int duration = 1000;
        presenter.showError(message, duration);

        // Then
        verify(mView).showErrorSnackbar(message, duration, null, null);
    }

    @Test
    public void should_showError_withButtonTitle_andOnClickListener() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = createPresenterWithEmptyImageDocument();

        // When
        final String message = "Error message";
        final String buttonTitle = "Retry";
        final View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

            }
        };
        presenter.showError(message, buttonTitle, onClickListener);

        // Then
        verify(mView).showErrorSnackbar(message, ErrorSnackbar.LENGTH_INDEFINITE, buttonTitle,
                onClickListener);
    }

    @Test
    public void should_startScanAnimation() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = createPresenterWithEmptyImageDocument();

        // When
        presenter.startScanAnimation();

        // Then
        verify(mView).showScanAnimation();
    }

    @Test
    public void should_stopScanAnimation() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = createPresenterWithEmptyImageDocument();

        // When
        presenter.stopScanAnimation();

        // Then
        verify(mView).hideScanAnimation();
    }

    @Test
    public void should_clearImagesFromDisk_onDocumentAnalyzed() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = spy(createPresenterWithEmptyImageDocument());
        final File filesDir = spy(new File("file:///gv-images/12343.jpg"));
        when(filesDir.exists()).thenReturn(true);
        when(mActivity.getFilesDir()).thenReturn(filesDir);

        // When
        presenter.onDocumentAnalyzed();

        // Then
        verify(presenter).clearSavedImages();
    }

    @Test
    public void should_clearParcelableMemoryCache_whenStarted() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = spy(createPresenterWithEmptyImageDocument());

        // When
        presenter.start();

        // Then
        verify(presenter).clearParcelableMemoryCache();
    }

    @Test
    public void should_startScanAnimation_whenStarted() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = createPresenterWithEmptyImageDocument();

        // When
        presenter.start();

        // Then
        verify(mView, atLeastOnce()).showScanAnimation();
    }

    @Test
    public void should_loadDocumentData_whenStarted() throws Exception {
        // Given
        final GiniVisionDocument document = spy(DocumentFactory.newEmptyImageDocument(
                Document.Source.newCameraSource(), Document.ImportMethod.NONE));
        final AnalysisScreenPresenter presenter = createPresenter(document);

        // When
        presenter.start();

        // Then
        verify(document).loadData(eq(mActivity), any(AsyncCallback.class));
    }

    @Test
    public void should_showHints_forImageDocument() throws Exception {
        // Given
        final AnalysisScreenPresenter presenter = createPresenterWithEmptyImageDocument();

        // When
        presenter.start();

        // Then
        verify(mView).showHints(presenter.getHints());
    }

    @Test
    public void should_notShowHints_forNonImageDocument() throws Exception {
        // Given
        final PdfDocument pdfDocument = mock(PdfDocument.class);
        when(pdfDocument.getType()).thenReturn(Document.Type.PDF);

        final AnalysisScreenPresenter presenter = createPresenter(pdfDocument);

        // When
        presenter.start();

        // Then
        verify(mView, never()).showHints(presenter.getHints());
    }

    @Test
    public void should_returnError_throughAnalysisFragmentListener_whenDocumentLoadingFailed()
            throws Exception {
        // Given
        final ImageDocumentFake imageDocument = new ImageDocumentFake();
        imageDocument.failWithException = new RuntimeException("Whoopsie");

        final AnalysisScreenPresenter presenter = createPresenter(imageDocument);
        final AnalysisFragmentListener listener = mock(AnalysisFragmentListener.class);
        presenter.setListener(listener);

        // When
        presenter.start();

        // Then
        verify(listener).onError(any(GiniVisionError.class));
    }

    @Test
    public void should_showPdfInfo_forPdfDocument_afterDocumentWasLoaded()
            throws Exception {
        // Given
        final PdfDocument pdfDocument = new PdfDocumentFake();

        final int pdfPageCount = 3;
        final String pdfPageCountString = pdfPageCount + " pages";
        final Resources resources = mock(Resources.class);
        when(resources.getQuantityString(anyInt(), anyInt(), any())).thenReturn(pdfPageCountString);
        when(mActivity.getResources()).thenReturn(resources);

        final AnalysisScreenPresenter presenter = spy(
                createPresenter(pdfDocument, pdfPageCount, null));

        final String pdfFilename = "Invoice.pdf";
        doReturn(pdfFilename).when(presenter).getPdfFilename(pdfDocument);

        // When
        presenter.start();

        // Then
        verify(mView).showPdfInfoPanel();
        verify(mView).showPdfTitle(pdfFilename);
        verify(mView).showPdfPageCount(pdfPageCountString);
    }

    private AnalysisScreenPresenter createPresenter(
            @NonNull final Document document,
            final int pdfPageCount,
            @Nullable final Exception pdfPageCountError) {
        return createPresenter(document, null, 0, pdfPageCount,
                pdfPageCountError, null);
    }

    @Test
    public void should_showPdfInfo_withoutPageCount_whenNotAvailable_afterDocumentWasLoaded()
            throws Exception {
        // Given
        final PdfDocument pdfDocument = new PdfDocumentFake();

        final AnalysisScreenPresenter presenter = spy(
                createPresenter(pdfDocument, 0, null));

        final String pdfFilename = "Invoice.pdf";
        doReturn(pdfFilename).when(presenter).getPdfFilename(pdfDocument);

        // When
        presenter.start();

        // Then
        verify(mView).showPdfInfoPanel();
        verify(mView).showPdfTitle(pdfFilename);
        verify(mView).hidePdfPageCount();
    }

    @Test
    public void should_showPdfInfo_withoutPageCount_whenErrorGettingIt_afterDocumentWasLoaded()
            throws Exception {
        // Given
        final PdfDocument pdfDocument = new PdfDocumentFake();

        final AnalysisScreenPresenter presenter = spy(
                createPresenter(pdfDocument, 0, new RuntimeException()));

        final String pdfFilename = "Invoice.pdf";
        doReturn(pdfFilename).when(presenter).getPdfFilename(pdfDocument);

        // When
        presenter.start();

        // Then
        verify(mView).showPdfInfoPanel();
        verify(mView).showPdfTitle(pdfFilename);
        verify(mView).hidePdfPageCount();
    }

    @Test
    public void should_showDocument_afterDocumentWasLoaded()
            throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        when(mView.getPdfPreviewSize()).thenReturn(new Size(1024, 768));

        final Bitmap bitmap = mock(Bitmap.class);
        final int rotationForDisplay = 90;
        final AnalysisScreenPresenter presenter = spy(
                createPresenter(imageDocument, bitmap,
                        rotationForDisplay));

        // When
        presenter.start();

        // Then
        verify(mView).showBitmap(bitmap, rotationForDisplay);
    }

    private AnalysisScreenPresenter createPresenter(
            @NonNull final Document document,
            @Nullable final Bitmap bitmap, final int rotationForDisplay) {
        return createPresenter(document, bitmap, rotationForDisplay, 0,
                null, null);
    }

    @Test
    public void should_analyzeDocument_afterDocumentWasLoaded() throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final AnalysisScreenPresenter presenter = spy(createPresenter(imageDocument));

        // When
        presenter.start();

        // Then
        verify(presenter).analyzeDocument();
    }

    @Test
    public void should_showError_ifAvailable_beforeAnalysis() throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final String errorMessage = "Something went wrong";
        final AnalysisScreenPresenter presenter = spy(
                createPresenter(imageDocument, errorMessage));

        // When
        presenter.start();

        // Then
        verify(mView).showErrorSnackbar(eq(errorMessage), anyInt(), (String) any(),
                any(View.OnClickListener.class));
    }

    @Test
    public void should_analyzeDocument_afterTappingRetry_onErrorSnackbar() throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final String errorMessage = "Something went wrong";
        final AnalysisScreenPresenter presenter = spy(createPresenter(imageDocument, errorMessage));

        // When
        presenter.start();

        final ArgumentCaptor<View.OnClickListener> onClickListenerCaptor = ArgumentCaptor.forClass(
                View.OnClickListener.class);
        verify(mView).showErrorSnackbar(eq(errorMessage), anyInt(), (String) any(),
                onClickListenerCaptor.capture());
        onClickListenerCaptor.getValue().onClick(mock(View.class));

        // Then
        verify(presenter).doAnalyzeDocument();
    }

    @Test
    public void should_startScanAnimation_whenAnalyzingDocument() throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final AnalysisScreenPresenter presenter = createPresenter(imageDocument);

        // When
        presenter.start();

        // Then
        // Two times, because scan animation is also started when starting the presenter
        verify(mView, atLeast(2)).showScanAnimation();
    }

    @Test
    public void should_stopScanAnimation_whenAnalysisFinished() throws Exception {
        // Given
        when(mActivity.getString(anyInt())).thenReturn("A String");

        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.complete(new AnalysisInteractor.ResultHolder(
                AnalysisInteractor.Result.SUCCESS_NO_EXTRACTIONS));

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        // When
        presenter.start();

        // Then
        verify(mView).hideScanAnimation();
    }

    private AnalysisScreenPresenter createPresenterWithAnalysisFuture(
            @NonNull final Document document,
            @NonNull final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture) {
        final AnalysisInteractor analysisInteractor = mock(AnalysisInteractor.class);
        //noinspection unchecked
        when(analysisInteractor.analyzeMultiPageDocument(any(GiniVisionMultiPageDocument.class)))
                .thenReturn(analysisFuture);
        return createPresenter(document, analysisInteractor);
    }

    private AnalysisScreenPresenter createPresenter(
            @NonNull final Document document,
            @NonNull final AnalysisInteractor analysisInteractor) {
        return createPresenter(document, null, 0, 0, null, null, analysisInteractor);
    }

    @Test
    public void should_showError_whenAnalysisFailed() throws Exception {
        // Given
        when(mActivity.getString(anyInt())).thenReturn("A String");

        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.completeExceptionally(new RuntimeException());

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        // When
        presenter.start();

        // Then
        verify(mView).showErrorSnackbar(anyString(), anyInt(), anyString(),
                any(View.OnClickListener.class));
    }

    @Test
    public void should_requestProceedingToNoExtractionsScreen_whenAnalysisSucceeded_withoutExtractions()
            throws Exception {
        // Given
        when(mActivity.getString(anyInt())).thenReturn("A String");

        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.complete(new AnalysisInteractor.ResultHolder(
                AnalysisInteractor.Result.SUCCESS_NO_EXTRACTIONS));

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        final AnalysisFragmentListener listener = mock(AnalysisFragmentListener.class);
        presenter.setListener(listener);

        // When
        presenter.start();

        // Then
        verify(listener).onProceedToNoExtractionsScreen(any(GiniVisionMultiPageDocument.class));
    }

    @Test
    public void should_returnExtractions_whenAnalysisSucceeded_withExtractions()
            throws Exception {
        // Given
        when(mActivity.getString(anyInt())).thenReturn("A String");

        final ImageDocument imageDocument = new ImageDocumentFake();

        final Map<String, GiniVisionSpecificExtraction> extractions = Collections.singletonMap(
                "extraction", mock(GiniVisionSpecificExtraction.class));
        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.complete(new AnalysisInteractor.ResultHolder(
                AnalysisInteractor.Result.SUCCESS_WITH_EXTRACTIONS,
                extractions));

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        final AnalysisFragmentListener listener = mock(AnalysisFragmentListener.class);
        presenter.setListener(listener);

        // When
        presenter.start();

        // Then
        verify(listener).onExtractionsAvailable(extractions);
    }

    @Test
    public void should_requestDocumentAnalysis_whenNoNetworkService_wasSet() throws Exception {
        // Given
        when(mActivity.getString(anyInt())).thenReturn("A String");

        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.complete(
                new AnalysisInteractor.ResultHolder(AnalysisInteractor.Result.NO_NETWORK_SERVICE));

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        final AnalysisFragmentListener listener = mock(AnalysisFragmentListener.class);
        presenter.setListener(listener);

        // When
        presenter.start();

        // Then
        verify(listener).onAnalyzeDocument(imageDocument);
    }

    @Test
    public void should_clearSavedImages_afterAnalysis_whenNetworkService_wasSet() throws Exception {
        // Given
        when(mActivity.getString(anyInt())).thenReturn("A String");

        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.complete(new AnalysisInteractor.ResultHolder(
                AnalysisInteractor.Result.SUCCESS_NO_EXTRACTIONS));

        final AnalysisScreenPresenter presenter = spy(
                createPresenterWithAnalysisFuture(imageDocument,
                        analysisFuture));

        final AnalysisFragmentListener listener = mock(AnalysisFragmentListener.class);
        presenter.setListener(listener);

        // When
        presenter.start();

        // Then
        verify(presenter).clearSavedImages();
    }

    @Test
    public void should_showAlertDialog_forOpenWithPdfDocument_ifAppIsDefaultForPdfs()
            throws Exception {
        // Given
        final PdfDocument pdfDocument = spy(new PdfDocumentFake());
        doReturn(Document.ImportMethod.OPEN_WITH).when(pdfDocument).getImportMethod();

        final AnalysisScreenPresenter presenter = spy(createPresenter(pdfDocument));

        final String pdfFilename = "Invoice.pdf";
        doReturn(pdfFilename).when(presenter).getPdfFilename(pdfDocument);

        // When
        presenter.start();

        final ArgumentCaptor<FileImportHelper.ShowAlertCallback> callbackCaptor =
                ArgumentCaptor.forClass(FileImportHelper.ShowAlertCallback.class);

        verify(presenter).showAlertIfOpenWithDocumentAndAppIsDefault(any(GiniVisionDocument.class),
                callbackCaptor.capture());

        final String message = "Message";
        final String positiveButton = "Positive Button";
        final DialogInterface.OnClickListener onClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {

                    }
                };
        final String negativeButton = "Negative Button";
        callbackCaptor.getValue().showAlertDialog(message, positiveButton,
                onClickListener, negativeButton, null, null);

        // Then
        verify(mView).showAlertDialog(message, positiveButton,
                onClickListener, negativeButton, null, null);
    }

    @Test
    public void should_analyzeDocument_whenAlertDialog_wasClosed_forOpenWithPdfDocument_ifAppIsDefaultForPdfs()
            throws Exception {
        // Given
        final PdfDocument pdfDocument = spy(new PdfDocumentFake());
        doReturn(Document.ImportMethod.OPEN_WITH).when(pdfDocument).getImportMethod();

        final AnalysisScreenPresenter presenter = spy(createPresenter(pdfDocument));

        final String pdfFilename = "Invoice.pdf";
        doReturn(pdfFilename).when(presenter).getPdfFilename(pdfDocument);

        doReturn(CompletableFuture.completedFuture(null))
                .when(presenter)
                .showAlertIfOpenWithDocumentAndAppIsDefault(any(GiniVisionDocument.class),
                        any(FileImportHelper.ShowAlertCallback.class));

        // When
        presenter.start();

        // Then
        verify(presenter).doAnalyzeDocument();
    }

    @Test
    public void should_notifiyListener_whenAlertDialog_wasCancelled_forOpenWithPdfDocument_ifAppIsDefaultForPdfs()
            throws Exception {
        // Given
        final PdfDocument pdfDocument = spy(new PdfDocumentFake());
        doReturn(Document.ImportMethod.OPEN_WITH).when(pdfDocument).getImportMethod();

        final AnalysisScreenPresenter presenter = spy(createPresenter(pdfDocument));

        final String pdfFilename = "Invoice.pdf";
        doReturn(pdfFilename).when(presenter).getPdfFilename(pdfDocument);

        final CompletableFuture<Void> future = new CompletableFuture<>();
        future.completeExceptionally(new CancellationException());
        doReturn(future)
                .when(presenter)
                .showAlertIfOpenWithDocumentAndAppIsDefault(any(GiniVisionDocument.class),
                        any(FileImportHelper.ShowAlertCallback.class));

        final AnalysisFragmentListener listener = mock(AnalysisFragmentListener.class);
        presenter.setListener(listener);

        // When
        presenter.start();

        // Then
        verify(listener).onDefaultPDFAppAlertDialogCancelled();
    }

    @Test
    public void should_stopScanAnimation_whenStopped() throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final AnalysisScreenPresenter presenter = createPresenter(imageDocument);

        // When
        presenter.stop();

        // Then
        verify(mView).hideScanAnimation();
    }

    @Test
    public void should_deleteUploadedDocument_ifAnalysisDidntComplete_whenStopped()
            throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final AnalysisInteractor analysisInteractor = mock(AnalysisInteractor.class);
        final AnalysisScreenPresenter presenter = createPresenter(imageDocument,
                analysisInteractor);

        // When
        presenter.stop();

        // Then
        verify(analysisInteractor).deleteDocument(any(GiniVisionDocument.class));
    }

    @Test
    public void should_deleteMultiPageUploadedDocuments_forPdfs_ifAnalysisDidntComplete_whenStopped()
            throws Exception {
        // Given
        final PdfDocument pdfDocument = new PdfDocumentFake();

        final AnalysisInteractor analysisInteractor = mock(AnalysisInteractor.class);
        final AnalysisScreenPresenter presenter = createPresenter(pdfDocument, analysisInteractor);

        // When
        presenter.stop();

        // Then
        //noinspection unchecked
        verify(analysisInteractor).deleteMultiPageDocument(any(GiniVisionMultiPageDocument.class));
    }

    @Test
    public void should_clearImageMultiPageDocumentMemoryStore_ifAnalysisCompleted_whenStopped()
            throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.complete(new AnalysisInteractor.ResultHolder(
                AnalysisInteractor.Result.SUCCESS_NO_EXTRACTIONS));

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        final ImageMultiPageDocumentMemoryStore memoryStore = mock(
                ImageMultiPageDocumentMemoryStore.class);

        final GiniVision.Internal internal = mock(GiniVision.Internal.class);
        when(internal.getImageMultiPageDocumentMemoryStore()).thenReturn(memoryStore);

        final GiniVision giniVision = mock(GiniVision.class);
        when(giniVision.internal()).thenReturn(internal);

        GiniVisionHelper.setGiniVisionInstance(giniVision);

        // When
        presenter.start();
        presenter.stop();

        // Then
        verify(memoryStore).clear();
    }

    @Test
    public void should_clearParcelableMemoryCache_whenFinished() throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final AnalysisScreenPresenter presenter = spy(createPresenter(imageDocument));

        // When
        presenter.finish();

        // Then
        verify(presenter).clearParcelableMemoryCache();
    }

    @Test
    public void should_notWaitForViewLayout_ifStopped_beforeLoadingDocumentDataFinishes()
            throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        final AnalysisScreenPresenter presenter = spy(createPresenter(imageDocument));
        doReturn(true).when(presenter).isStopped();

        // When
        presenter.start();

        // Then
        verify(mView, never()).waitForViewLayout();
    }

    @Test
    public void should_notReturnError_ifStopped_beforeLoadingDocumentDataFinishes()
            throws Exception {
        // Given
        final ImageDocumentFake imageDocument = new ImageDocumentFake();
        imageDocument.failWithException = new RuntimeException();

        final AnalysisScreenPresenter presenter = spy(createPresenter(imageDocument));
        doReturn(true).when(presenter).isStopped();

        final AnalysisFragmentListener listener = mock(AnalysisFragmentListener.class);
        presenter.setListener(listener);

        // When
        presenter.start();

        // Then
        verify(listener, never()).onError(any(GiniVisionError.class));
    }

    @Test
    public void should_notShowDocument_ifStopped_beforeDocumentRendererFinishes()
            throws Exception {
        // Given
        final ImageDocument imageDocument = new ImageDocumentFake();

        when(mView.getPdfPreviewSize()).thenReturn(new Size(1024, 768));

        final Bitmap bitmap = mock(Bitmap.class);
        final int rotationForDisplay = 90;
        final AnalysisScreenPresenter presenter = spy(
                createPresenter(imageDocument, bitmap, rotationForDisplay));
        doReturn(true).when(presenter).isStopped();

        // When
        presenter.start();

        // Then
        verify(mView, never()).showBitmap(bitmap, rotationForDisplay);
    }

    @Test
    public void should_triggerErrorEvent_forError_fromReviewScreen() throws Exception {
        // Given
        final EventTracker eventTracker = spy(EventTracker.class);
        new GiniVision.Builder().setEventTracker(eventTracker).build();

        final ImageDocument imageDocument = new ImageDocumentFake();

        final Exception exception = new Exception("Something is not working");
        GiniVision.getInstance().internal().setReviewScreenAnalysisError(exception);

        final String errorMessage = "Something went wrong";
        final AnalysisScreenPresenter presenter = createPresenter(imageDocument, errorMessage);

        // When
        presenter.start();

        // Then
        final Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put(AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY.MESSAGE, errorMessage);
        errorDetails.put(AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY.ERROR_OBJECT, exception);
        verify(eventTracker).onAnalysisScreenEvent(new Event<>(AnalysisScreenEvent.ERROR, errorDetails));
    }

    @Test
    public void should_triggerErrorEvent_forAnalysisError() throws Exception {
        // Given
        final EventTracker eventTracker = spy(EventTracker.class);
        new GiniVision.Builder().setEventTracker(eventTracker).build();

        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        final RuntimeException exception = new RuntimeException("error message");
        analysisFuture.completeExceptionally(exception);

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        // When
        presenter.start();

        // Then
        final Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put(AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY.MESSAGE, exception.getMessage());
        errorDetails.put(AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY.ERROR_OBJECT, exception);
        verify(eventTracker).onAnalysisScreenEvent(new Event<>(AnalysisScreenEvent.ERROR, errorDetails));
    }

    @Test
    public void should_triggerRetryEvent_forError_fromReviewScreen_whenRetry_wasClicked() throws Exception {
        // Given
        final EventTracker eventTracker = spy(EventTracker.class);
        new GiniVision.Builder().setEventTracker(eventTracker).build();

        final ImageDocument imageDocument = new ImageDocumentFake();

        final String errorMessage = "Something went wrong";
        final AnalysisScreenPresenter presenter = createPresenter(imageDocument, errorMessage);

        // When
        presenter.start();

        final ArgumentCaptor<View.OnClickListener> onClickListenerCaptor = ArgumentCaptor.forClass(
                View.OnClickListener.class);
        verify(mView).showErrorSnackbar(anyString(), anyInt(), (String) any(),
                onClickListenerCaptor.capture());
        onClickListenerCaptor.getValue().onClick(mock(View.class));

        // Then
        verify(eventTracker).onAnalysisScreenEvent(new Event<>(AnalysisScreenEvent.RETRY));
    }

    @Test
    public void should_triggerRetryEvent_forAnalysisError_whenRetry_wasClicked() throws Exception {
        // Given
        when(mActivity.getString(anyInt())).thenReturn("A String");

        final EventTracker eventTracker = spy(EventTracker.class);
        new GiniVision.Builder().setEventTracker(eventTracker).build();

        final ImageDocument imageDocument = new ImageDocumentFake();

        final CompletableFuture<AnalysisInteractor.ResultHolder> analysisFuture =
                new CompletableFuture<>();
        analysisFuture.completeExceptionally(new RuntimeException("error message"));

        final AnalysisScreenPresenter presenter = createPresenterWithAnalysisFuture(imageDocument,
                analysisFuture);

        // When
        presenter.start();

        final ArgumentCaptor<View.OnClickListener> onClickListenerCaptor = ArgumentCaptor.forClass(
                View.OnClickListener.class);
        verify(mView).showErrorSnackbar(anyString(), anyInt(), (String) any(),
                onClickListenerCaptor.capture());
        onClickListenerCaptor.getValue().onClick(mock(View.class));

        // Then
        verify(eventTracker).onAnalysisScreenEvent(new Event<>(AnalysisScreenEvent.RETRY));
    }
}