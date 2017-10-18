package net.gini.android.vision.component;

import static net.gini.android.vision.component.Util.hasNoPay5Extractions;
import static net.gini.android.vision.component.Util.isIntentActionViewOrSend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.GiniVisionFileImport;
import net.gini.android.vision.ImportedFileValidationException;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.camera.CameraFragmentStandard;
import net.gini.android.vision.internal.util.IntentHelper;
import net.gini.android.vision.internal.util.UriHelper;
import net.gini.android.vision.noresults.NoResultsFragmentListener;
import net.gini.android.vision.noresults.NoResultsFragmentStandard;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;
import net.gini.android.vision.onboarding.OnboardingFragmentStandard;
import net.gini.android.vision.review.ReviewFragmentListener;
import net.gini.android.vision.review.ReviewFragmentStandard;
import net.gini.android.visionadvtest.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * <p>
 * An example activity which uses the standard Gini Vision Library fragments from its Component
 * API.
 * </p>
 * <p>
 * This is a multi-fragment activity, which handles fragment changes, Gini Vision Library fragment
 * listener callbacks, document analysis with the Gini API SDK and related logic.
 * </p>
 */
public class GiniVisionActivity extends Activity implements CameraFragmentListener,
        OnboardingFragmentListener, ReviewFragmentListener, AnalysisFragmentListener,
        NoResultsFragmentListener {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionActivity.class);

    private static final String STATE_SHOW_CAMERA_ON_START = "STATE_SHOW_CAMERA_ON_START";
    private static final int SHOW_ERROR_DURATION = 4000;

    private Fragment mCurrentFragment;
    private String mDocumentAnalysisErrorMessage;
    private Map<String, SpecificExtraction> mExtractionsFromReviewScreen;
    private GiniVisionCoordinator mGiniVisionCoordinator;
    private boolean mShowCameraOnStart = false;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;
    private String mTitleBeforeOnboarding;

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Analyze document {}", document);
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_analysis");

        startScanAnimation();
        // We can start analyzing the document by sending it to the Gini API
        getSingleDocumentAnalyzer().analyzeDocument(document,
                new SingleDocumentAnalyzer.DocumentAnalysisListener() {
                    @Override
                    public void onException(Exception exception) {
                        stopScanAnimation();
                        String message = "unknown";
                        if (exception.getMessage() != null) {
                            message = exception.getMessage();
                        }

                        if (mCurrentFragment != null
                                && mCurrentFragment instanceof AnalysisFragmentStandard) {
                            // Show the error in the Snackbar with a retry button
                            AnalysisFragmentStandard analysisFragment =
                                    (AnalysisFragmentStandard) mCurrentFragment;
                            final SingleDocumentAnalyzer.DocumentAnalysisListener listener = this;
                            analysisFragment.showError("Analysis failed: " + message,
                                    getString(R.string.retry_analysis), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startScanAnimation();
                                            getSingleDocumentAnalyzer().cancelAnalysis();
                                            getSingleDocumentAnalyzer().analyzeDocument(document,
                                                    listener);
                                        }
                                    });
                        }
                        LOG.error("Analysis failed in the Analysis Screen", exception);
                    }

                    @Override
                    public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                        if (mCurrentFragment != null
                                && mCurrentFragment instanceof AnalysisFragmentStandard) {
                            LOG.debug("Document analyzed in the Analysis Screen");
                            AnalysisFragmentStandard analysisFragment =
                                    (AnalysisFragmentStandard) mCurrentFragment;
                            // Calling onDocumentAnalyzed() is important to notify the Analysis
                            // Fragment that the
                            // analysis has completed successfully
                            analysisFragment.onDocumentAnalyzed();
                            stopScanAnimation();
                            showExtractions(getSingleDocumentAnalyzer().getGiniApiDocument(),
                                    extractions, document);
                        } else {
                            LOG.debug("Document analyzed in the Analysis Screen, but not in the "
                                    + "Analysis Screen anymore.");
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().findFragmentById(R.id.fragment_container_onboarding) != null) {
            removeOnboarding();
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        getFragmentManager().popBackStack();
        getSingleDocumentAnalyzer().cancelAnalysis();
        mDocumentAnalysisErrorMessage = null;
        mExtractionsFromReviewScreen = null;
    }

    @Override
    public void onBackToCameraPressed() {
        getFragmentManager().popBackStack(getCameraFragment().getClass().getSimpleName(), 0);
    }

    @Override
    public void onCloseOnboarding() {
        LOG.debug("Close onboarding");
        removeOnboarding();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gini_vision);
        configureLogging();
        setupGiniVisionCoordinator();
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            if (isIntentActionViewOrSend(intent)) {
                startGiniVisionLibraryForImportedFile(intent);
            } else {
                showCamera();
            }
        } else {
            initState(savedInstanceState);
            retainFragment();
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (isIntentActionViewOrSend(intent)) {
            startGiniVisionLibraryForImportedFile(intent);
        }
    }

    private void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        try {
            final Document document = GiniVisionFileImport.createDocumentForImportedFile(
                    importedFileIntent, this);
            if (document.isReviewable()) {
                pushFragment(getReviewFragment(document), R.string.title_review);
            } else {
                pushFragment(getAnalysisFragment(document), R.string.title_review);
            }
        } catch (ImportedFileValidationException e) {
            e.printStackTrace();
            String message = getString(R.string.gv_document_import_invalid_document);
            if (e.getValidationError() != null) {
                switch (e.getValidationError()) {
                    case TYPE_NOT_SUPPORTED:
                        message = getString(R.string.gv_document_import_error_type_not_supported);
                        break;
                    case SIZE_TOO_LARGE:
                        message = getString(R.string.gv_document_import_error_size_too_large);
                        break;
                    case TOO_MANY_PDF_PAGES:
                        message = getString(R.string.gv_document_import_error_too_many_pdf_pages);
                        break;
                }
            }
            new AlertDialog.Builder(this).setMessage(message).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, final int i) {
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanner, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSingleDocumentAnalyzer().cancelAnalysis();
    }

    @Override
    public void onDocumentAvailable(@NonNull Document document) {
        LOG.debug("Document available {}", document);
        // Cancel analysis to make sure, that the document analysis will start in
        // onShouldAnalyzeDocument()
        getSingleDocumentAnalyzer().cancelAnalysis();
        if (document.isReviewable()) {
            pushFragment(getReviewFragment(document), R.string.title_review);
        } else {
            pushFragment(getAnalysisFragment(document));
        }
    }

    // Set to true to allow execution of the custom code check
    private static final boolean DO_CUSTOM_DOCUMENT_CHECK = false;

    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {
        // We can apply custom checks here to an imported document and notify the Gini Vision
        // Library about the result

        // As an example we allow only documents smaller than 5MB
        if (DO_CUSTOM_DOCUMENT_CHECK) {
            // Use the Intent with which the document was imported to access its contents
            // (document.getData() may be null)
            final Intent intent = document.getIntent();
            if (intent == null) {
                callback.documentRejected(getString(R.string.gv_document_import_error));
                return;
            }
            final Uri uri = IntentHelper.getUri(intent);
            if (uri == null) {
                callback.documentRejected(getString(R.string.gv_document_import_error));
                return;
            }
            // IMPORTANT: always call one of the callback methods
            if (hasLessThan5MB(callback, uri)) {
                callback.documentAccepted();
            } else {
                callback.documentRejected(getString(R.string.document_size_too_large));
            }
        } else {
            // IMPORTANT: always call one of the callback methods
            callback.documentAccepted();
        }
    }

    private boolean hasLessThan5MB(final @NonNull DocumentCheckResultCallback callback,
            final Uri uri) {
        final int fileSize = UriHelper.getFileSizeFromUri(uri, this);
        return fileSize <= 5 * 1024 * 1024;
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        LOG.debug("Reviewed and analyzed document {}", document);
        // If we have received the extractions while in the Review Screen we don't need to go to
        // the Analysis Screen,
        // we can show the extractions
        if (mExtractionsFromReviewScreen != null) {
            showExtractions(getSingleDocumentAnalyzer().getGiniApiDocument(),
                    mExtractionsFromReviewScreen, document);
            mExtractionsFromReviewScreen = null;
        }
    }

    @Override
    public void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation) {
        LOG.debug("Document was rotated: oldRotation={}, newRotation={}, document={}", oldRotation,
                newRotation, document);
        // We need to cancel the analysis here, we will have to upload the rotated document in
        // onAnalyzeDocument() while
        // the Analysis Fragment is shown
        getSingleDocumentAnalyzer().cancelAnalysis();
        mDocumentAnalysisErrorMessage = null;
        mExtractionsFromReviewScreen = null;
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {
        LOG.error("Gini Vision Lib error: {} - {}", error.getErrorCode(), error.getMessage());
        if (mCurrentFragment != null) {
            if (mCurrentFragment instanceof AnalysisFragmentStandard) {
                // We can show errors in a Snackbar in the Analysis Fragment
                AnalysisFragmentStandard analysisFragment =
                        (AnalysisFragmentStandard) mCurrentFragment;
                analysisFragment.showError(
                        "Error: " + error.getErrorCode() + " - " + error.getMessage(),
                        SHOW_ERROR_DURATION);
                return;
            }
        }
        Toast.makeText(this, "Error: " + error.getErrorCode() + " - " + error.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.onboarding) {
            showOnboarding();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        LOG.debug("Proceed to Analysis Screen with document {}", document);
        // As the library requests us to go to the Analysis Screen we should only remove the
        // listener.
        // We should not cancel the analysis here as we don't know, if we proceed because the
        // analysis didn't complete or
        // the user rotated the image
        getSingleDocumentAnalyzer().removeListener();
        pushFragment(getAnalysisFragment(document));
    }

    private void showNoResultsScreen(final @NonNull Document document) {
        getFragmentManager().popBackStack(getCameraFragment().getClass().getSimpleName(), 0);
        pushFragment(NoResultsFragmentStandard.createInstance(document));
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SHOW_CAMERA_ON_START, mShowCameraOnStart);
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
        LOG.debug("Should analyze document in the Review Screen {}", document);
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_review");

        // We should start analyzing the document by sending it to the Gini API.
        // If the user did not modify the image we can get the analysis results earlier.
        // The Gini Vision Library will not request you to proceed to the Analysis Screen, if the
        // results were
        // received in the Review Screen.
        // If the user modified the image or the analysis didn't complete or it failed the Gini
        // Vision Library
        // will request you to proceed to the Analysis Screen.
        getSingleDocumentAnalyzer().analyzeDocument(document,
                new SingleDocumentAnalyzer.DocumentAnalysisListener() {
                    @Override
                    public void onException(Exception exception) {
                        String message = "unknown";
                        if (exception.getMessage() != null) {
                            message = exception.getMessage();
                        }
                        // Don't show the error message here, but forward it to the Analysis
                        // Fragment, where it will be
                        // shown in a Snackbar
                        mDocumentAnalysisErrorMessage = "Analysis failed: " + message;
                        LOG.error("Analysis failed in the Review Screen", exception);
                    }

                    @Override
                    public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                        if (mCurrentFragment != null
                                && mCurrentFragment instanceof ReviewFragmentStandard) {
                            LOG.debug("Document analyzed in the Review Screen");
                            ReviewFragmentStandard reviewFragment =
                                    (ReviewFragmentStandard) mCurrentFragment;
                            // Cache the extractions until the user clicks the next button and
                            // onDocumentReviewedAndAnalyzed()
                            // will have been called
                            mExtractionsFromReviewScreen = extractions;
                            // Calling onDocumentAnalyzed() is important to notify the Review
                            // Fragment that the
                            // analysis has completed successfully
                            reviewFragment.onDocumentAnalyzed();
                        } else {
                            LOG.debug("Document analyzed in the Review Screen, but not in the "
                                    + "Review Screen anymore.");
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        LOG.debug("Start: mShowCameraOnStart={}", mShowCameraOnStart);
        if (mShowCameraOnStart) {
            removeOnboarding();
            showCamera();
            mShowCameraOnStart = false;
        }
    }

    public void removeOnboarding() {
        LOG.debug("Remove the Onboarding Screen");
        showCameraOverlays();
        Fragment fragment = getFragmentManager().findFragmentById(
                R.id.fragment_container_onboarding);
        if (fragment != null) {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in,
                    R.animator.fade_out).remove(fragment).commit();
        }
        setTitle(mTitleBeforeOnboarding != null ? mTitleBeforeOnboarding
                : getString(R.string.title_camera));
        mTitleBeforeOnboarding = null;
    }

    public void pushFragment(Fragment fragment, @StringRes int titleRes) {
        LOG.debug("Pushing fragment {} with title '{}'", fragment.getClass().getSimpleName(),
                getString(titleRes));
        mCurrentFragment = fragment;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
        setTitle(titleRes);
    }

    public void pushFragment(Fragment fragment) {
        LOG.debug("Pushing fragment {} ", fragment.getClass().getSimpleName());
        mCurrentFragment = fragment;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
        setTitle("");
    }

    public void showOnboarding() {
        LOG.debug("Show the Onboarding Screen");
        hideCameraOverlays();
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.fragment_container_onboarding, getOnboardingFragment())
                .commit();
        mTitleBeforeOnboarding = (String) getTitle();
        setTitle(getString(R.string.title_onboarding));
    }

    private void configureLogging() {
        final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        final PatternLayoutEncoder layoutEncoder = new PatternLayoutEncoder();
        layoutEncoder.setContext(lc);
        layoutEncoder.setPattern("%-5level %file:%line [%thread] - %msg%n");
        layoutEncoder.start();

        final LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(layoutEncoder);
        logcatAppender.start();

        final ch.qos.logback.classic.Logger root =
                (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logcatAppender);
    }

    private AnalysisFragmentStandard getAnalysisFragment(Document document) {
        AnalysisFragmentStandard analysisFragment = AnalysisFragmentStandard.createInstance(
                document, mDocumentAnalysisErrorMessage);
        mDocumentAnalysisErrorMessage = null;
        return analysisFragment;
    }

    private CameraFragmentStandard getCameraFragment() {
        return CameraFragmentStandard.createInstance(DocumentImportEnabledFileTypes.PDF_AND_IMAGES);
    }

    private Bundle getExtractionsBundle(Map<String, SpecificExtraction> extractions) {
        final Bundle extractionsBundle = new Bundle();
        for (Map.Entry<String, SpecificExtraction> entry : extractions.entrySet()) {
            extractionsBundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return extractionsBundle;
    }

    private OnboardingFragmentStandard getOnboardingFragment() {
        return new OnboardingFragmentStandard();
    }

    private ReviewFragmentStandard getReviewFragment(Document document) {
        return ReviewFragmentStandard.createInstance(document);
    }

    private SingleDocumentAnalyzer getSingleDocumentAnalyzer() {
        if (mSingleDocumentAnalyzer == null) {
            mSingleDocumentAnalyzer =
                    ((ComponentApiApp) getApplication()).getSingleDocumentAnalyzer();
        }
        return mSingleDocumentAnalyzer;
    }

    private void hideCameraOverlays() {
        LOG.debug("Hide camera overlays");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof CameraFragmentStandard)) {
            return;
        }
        CameraFragmentStandard cameraFragment = (CameraFragmentStandard) mCurrentFragment;
        cameraFragment.hideInterface();
    }

    private void initState(final Bundle savedInstanceState) {
        mShowCameraOnStart = savedInstanceState.getBoolean(STATE_SHOW_CAMERA_ON_START,
                mShowCameraOnStart);
    }

    private void retainFragment() {
        mCurrentFragment = getFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private void setupGiniVisionCoordinator() {
        mGiniVisionCoordinator = GiniVisionCoordinator.createInstance(this);
        mGiniVisionCoordinator.setListener(new GiniVisionCoordinator.Listener() {
            @Override
            public void onShowOnboarding() {
                showOnboarding();
            }
        });
    }

    private void showCamera() {
        LOG.debug("Show the Camera Screen");
        pushFragment(getCameraFragment(), R.string.title_camera);
        // Delay notifying the coordinator to allow the camera fragment view to be created
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mGiniVisionCoordinator.onCameraStarted();
            }
        });
    }

    private void showCameraOverlays() {
        LOG.debug("Show camera overlays");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof CameraFragmentStandard)) {
            return;
        }
        CameraFragmentStandard cameraFragment = (CameraFragmentStandard) mCurrentFragment;
        cameraFragment.showInterface();
    }

    private void showExtractions(net.gini.android.models.Document giniApiDocument,
            Map<String, SpecificExtraction> extractions, Document document) {
        LOG.debug("Show extractions");
        // If we have no Pay 5 extractions we query the Gini Vision Library
        // whether we should show the the Gini Vision No Results Screen
        if (hasNoPay5Extractions(extractions.keySet())
                && GiniVisionCoordinator.shouldShowGiniVisionNoResultsScreen(document)) {
            // Show a special screen, if no Pay5 extractions were found to give the user some
            // hints and tips
            // for using the Gini Vision Library
            showNoResultsScreen(document);
        } else {
            Intent intent = new Intent(this, ExtractionsActivity.class);
            intent.putExtra(ExtractionsActivity.EXTRA_IN_DOCUMENT, giniApiDocument);
            intent.putExtra(ExtractionsActivity.EXTRA_IN_EXTRACTIONS,
                    getExtractionsBundle(extractions));
            startActivity(intent);
            finish();
        }
        mShowCameraOnStart = true;
    }

    private void startScanAnimation() {
        LOG.debug("Start scan animation");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof AnalysisFragmentStandard)) {
            return;
        }
        AnalysisFragmentStandard analysisFragment = (AnalysisFragmentStandard) mCurrentFragment;
        analysisFragment.startScanAnimation();
    }

    private void stopScanAnimation() {
        LOG.debug("Stop scan animation");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof AnalysisFragmentStandard)) {
            return;
        }
        AnalysisFragmentStandard analysisFragment = (AnalysisFragmentStandard) mCurrentFragment;
        analysisFragment.stopScanAnimation();
    }

}
