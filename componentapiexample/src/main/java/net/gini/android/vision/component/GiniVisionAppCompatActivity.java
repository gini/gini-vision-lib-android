package net.gini.android.vision.component;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.camera.CameraFragmentCompat;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.onboarding.OnboardingFragmentCompat;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;
import net.gini.android.vision.review.ReviewFragmentCompat;
import net.gini.android.vision.review.ReviewFragmentListener;
import net.gini.android.visionadvtest.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * <p>
 *     An example activity which uses the Android support library Gini Vision Library fragments from its Component API.
 * </p>
 * <p>
 *     This is a multi-fragment activity, which handles fragment changes, Gini Vision Library fragment listener callbacks, document analysis with the Gini API SDK and related logic.
 * </p>
 */
public class GiniVisionAppCompatActivity extends AppCompatActivity
        implements CameraFragmentListener, OnboardingFragmentListener, ReviewFragmentListener, AnalysisFragmentListener {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionActivity.class);

    private Fragment mCurrentFragment;

    private GiniVisionCoordinator mGiniVisionCoordinator;

    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    private String mDocumentAnalysisErrorMessage;
    private Map<String, SpecificExtraction> mExtractionsFromReviewScreen;
    private boolean mShowCameraOnStart = false;
    private String mTitleBeforeOnboarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gini_vision);
        configureLogging();
        bindViews();
        setupGiniVisionCoordinator();
        showCamera();
        mSingleDocumentAnalyzer = ((ComponentApiApp)getApplication()).getSingleDocumentAnalyzer();
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
        showFragment(getCameraFragment(), R.string.title_camera);
        // Delay notifying the coordinator to allow the camera fragment view to be created
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mGiniVisionCoordinator.onCameraStarted();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanner, menu);
        return true;
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
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container_onboarding) != null) {
            removeOnboarding();
            return;
        }
        // We recommend returning to the Camera Screen, skipping the Review Screen, if back was pressed while in the
        // Analysis Screen
        if (isShowingCamera()) {
            finish();
        } else {
            showCamera();
        }
        mSingleDocumentAnalyzer.cancelAnalysis();
        mDocumentAnalysisErrorMessage = null;
        mExtractionsFromReviewScreen = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSingleDocumentAnalyzer.cancelAnalysis();
    }

    public boolean isShowingCamera() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return fragment != null && fragment instanceof CameraFragmentCompat;
    }

    private void bindViews() {
    }

    private CameraFragmentCompat getCameraFragment() {
        return new CameraFragmentCompat();
    }

    private OnboardingFragmentCompat getOnboardingFragment() {
        return new OnboardingFragmentCompat();
    }

    private ReviewFragmentCompat getReviewFragment(Document document) {
        return ReviewFragmentCompat.createInstance(document);
    }

    private AnalysisFragmentCompat getAnalysisFragment(Document document) {
        AnalysisFragmentCompat analysisFragment = AnalysisFragmentCompat.createInstance(document, mDocumentAnalysisErrorMessage);
        mDocumentAnalysisErrorMessage = null;
        return analysisFragment;
    }

    public void showFragment(Fragment fragment, @StringRes int titleRes) {
        LOG.debug("Showing fragment {} with title '{}'", fragment.getClass().getSimpleName(), getString(titleRes));
        mCurrentFragment = fragment;
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
        setTitle(titleRes);
    }

    public void showOnboarding() {
        LOG.debug("Show the Onboarding Screen");
        hideCameraOverlays();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.fragment_container_onboarding, getOnboardingFragment())
                .commit();
        mTitleBeforeOnboarding = (String) getTitle();
        setTitle(getString(R.string.title_onboarding));
    }

    private void hideCameraOverlays() {
        LOG.debug("Hide camera overlays");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof CameraFragmentCompat)) {
            return;
        }
        CameraFragmentCompat cameraFragment = (CameraFragmentCompat) mCurrentFragment;
        cameraFragment.hideDocumentCornerGuides();
        cameraFragment.hideCameraTriggerButton();
    }

    public void removeOnboarding() {
        LOG.debug("Remove the Onboarding Screen");
        showCameraOverlays();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container_onboarding);
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .remove(fragment)
                    .commit();
        }
        setTitle(mTitleBeforeOnboarding != null ? mTitleBeforeOnboarding : getString(R.string.title_camera));
        mTitleBeforeOnboarding = null;
    }

    private void showCameraOverlays() {
        LOG.debug("Show camera overlays");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof CameraFragmentCompat)) {
            return;
        }
        CameraFragmentCompat cameraFragment = (CameraFragmentCompat) mCurrentFragment;
        cameraFragment.showDocumentCornerGuides();
        cameraFragment.showCameraTriggerButton();
    }

    @Override
    public void onDocumentAvailable(@NonNull Document document) {
        LOG.debug("Document available {}", document);
        mSingleDocumentAnalyzer.cancelAnalysis();
        showFragment(getReviewFragment(document), R.string.title_review);
    }

    @Override
    public void onCloseOnboarding() {
        LOG.debug("Close onboarding");
        removeOnboarding();
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
        LOG.debug("Should analyze document in the Review Screen {}", document);
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_review");

        // We should start analyzing the document by sending it to the Gini API.
        // If the user did not modify the image we can get the analysis results earlier.
        // The Gini Vision Library will not request you to proceed to the Analysis Screen, if the results were
        // received in the Review Screen.
        // If the user modified the image or the analysis didn't complete or it failed the Gini Vision Library
        // will request you to proceed to the Analysis Screen.
        mSingleDocumentAnalyzer.analyzeDocument(document, new SingleDocumentAnalyzer.DocumentAnalysisListener() {
            @Override
            public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                if (mCurrentFragment != null && mCurrentFragment instanceof ReviewFragmentCompat) {
                    LOG.debug("Document analyzed in the Review Screen");
                    ReviewFragmentCompat reviewFragment = (ReviewFragmentCompat) mCurrentFragment;
                    // Calling onDocumentAnalyzed() is important to notify the Review Fragment that the
                    // analysis has completed successfully
                    reviewFragment.onDocumentAnalyzed();
                    // Cache the extractions until the user clicks the next button and onDocumentReviewedAndAnalyzed()
                    // will have been called
                    mExtractionsFromReviewScreen = extractions;
                } else {
                    LOG.debug("Document analyzed in the Review Screen, but not in the Review Screen anymore.");
                }
            }

            @Override
            public void onException(Exception exception) {
                String message = "unknown";
                if (exception.getMessage() != null) {
                    message = exception.getMessage();
                }
                // Don't show the error message here, but forward it to the Analysis Fragment, where it will be
                // shown in a Snackbar
                mDocumentAnalysisErrorMessage = "Analysis failed: " + message;
                LOG.error("Analysis failed in the Review Screen", exception);
            }
        });
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        LOG.debug("Proceed to Analysis Screen with document {}", document);
        // As the library requests us to go to the Analysis Screen we should only remove the listener.
        // We should not cancel the analysis here as we don't know, if we proceed because the analysis didn't complete or
        // the user rotated the image
        mSingleDocumentAnalyzer.removeListener();
        showFragment(getAnalysisFragment(document), R.string.title_analysis);
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        LOG.debug("Reviewed and analyzed document {}", document);
        // If we have received the extractions while in the Review Screen we don't need to go to the Analysis Screen,
        // we can show the extractions
        if (mExtractionsFromReviewScreen != null) {
            showExtractions(mSingleDocumentAnalyzer.getGiniApiDocument(), mExtractionsFromReviewScreen);
            mExtractionsFromReviewScreen = null;
        }
    }

    @Override
    public void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation) {
        LOG.debug("Document was rotated: oldRotation={}, newRotation={}, document={}", oldRotation, newRotation, document);
        // We need to cancel the analysis here, we will have to upload the rotated document in onAnalyzeDocument() while
        // the Analysis Fragment is shown
        mSingleDocumentAnalyzer.cancelAnalysis();
        mDocumentAnalysisErrorMessage = null;
        mExtractionsFromReviewScreen = null;
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {
        LOG.error("Gini Vision Lib error: {} - {}", error.getErrorCode(), error.getMessage());
        if (mCurrentFragment != null && mCurrentFragment instanceof AnalysisFragmentCompat) {
            // We can show errors in a Snackbar in the Analysis Fragment
            AnalysisFragmentCompat analysisFragment = (AnalysisFragmentCompat) mCurrentFragment;
            analysisFragment.showError("Error: " +
                            error.getErrorCode() + " - " +
                            error.getMessage(),
                    Toast.LENGTH_LONG);
        } else {
            Toast.makeText(this, "Error: " +
                            error.getErrorCode() + " - " +
                            error.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Analyze document {}", document);
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_analysis");

        startScanAnimation();
        // We can start analyzing the document by sending it to the Gini API
        mSingleDocumentAnalyzer.analyzeDocument(document, new SingleDocumentAnalyzer.DocumentAnalysisListener() {
            @Override
            public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                if (mCurrentFragment != null && mCurrentFragment instanceof AnalysisFragmentCompat) {
                    LOG.debug("Document analyzed in the Analysis Screen");
                    AnalysisFragmentCompat analysisFragment = (AnalysisFragmentCompat) mCurrentFragment;
                    // Calling onDocumentAnalyzed() is important to notify the Analysis Fragment that the
                    // analysis has completed successfully
                    analysisFragment.onDocumentAnalyzed();
                    stopScanAnimation();
                    showExtractions(mSingleDocumentAnalyzer.getGiniApiDocument(), extractions);
                } else {
                    LOG.debug("Document analyzed in the Analysis Screen, but not in the Analysis Screen anymore.");
                }
            }

            @Override
            public void onException(Exception exception) {
                stopScanAnimation();
                String message = "unknown";
                if (exception.getMessage() != null) {
                    message = exception.getMessage();
                }

                if (mCurrentFragment != null && mCurrentFragment instanceof AnalysisFragmentCompat) {
                    // Show the error in the Snackbar with a retry button
                    AnalysisFragmentCompat analysisFragment = (AnalysisFragmentCompat) mCurrentFragment;
                    final SingleDocumentAnalyzer.DocumentAnalysisListener listener = this;
                    analysisFragment.showError("Analysis failed: " + message, getString(R.string.retry_analysis), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startScanAnimation();
                            mSingleDocumentAnalyzer.cancelAnalysis();
                            mSingleDocumentAnalyzer.analyzeDocument(document, listener);
                        }
                    });
                }
                LOG.error("Analysis failed in the Analysis Screen", exception);
            }
        });
    }

    private void showExtractions(net.gini.android.models.Document giniApiDocument, Map<String, SpecificExtraction> extractions) {
        LOG.debug("Show extractions");
        // We display only the Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference
        if (pay5ExtractionsAvailable(extractions)) {
            Intent intent = new Intent(this, ExtractionsActivity.class);
            intent.putExtra(ExtractionsActivity.EXTRA_IN_DOCUMENT, giniApiDocument);
            intent.putExtra(ExtractionsActivity.EXTRA_IN_EXTRACTIONS, getExtractionsBundle(extractions));
            startActivity(intent);
        } else {
            // Show a special screen, if no Pay5 extractions were found to give the user some hints and tips
            // for using the Gini Vision Library
            Intent intent = new Intent(this, NoExtractionsActivity.class);
            startActivity(intent);
        }
        mShowCameraOnStart = true;
    }

    private boolean pay5ExtractionsAvailable(Map<String, SpecificExtraction> extractionsBundle) {
        for (String key : extractionsBundle.keySet()) {
            if (key.equals("amountToPay") ||
                    key.equals("bic") ||
                    key.equals("iban") ||
                    key.equals("paymentReference") ||
                    key.equals("paymentRecipient")) {
                return true;
            }
        }
        return false;
    }

    private Bundle getExtractionsBundle(Map<String, SpecificExtraction> extractions) {
        final Bundle extractionsBundle = new Bundle();
        for (Map.Entry<String, SpecificExtraction> entry : extractions.entrySet()) {
            extractionsBundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return extractionsBundle;
    }

    private void startScanAnimation() {
        LOG.debug("Start scan animation");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof AnalysisFragmentCompat)) {
            return;
        }
        AnalysisFragmentCompat analysisFragment = (AnalysisFragmentCompat) mCurrentFragment;
        analysisFragment.startScanAnimation();
    }

    private void stopScanAnimation() {
        LOG.debug("Stop scan animation");
        if (mCurrentFragment == null || !(mCurrentFragment instanceof AnalysisFragmentCompat)) {
            return;
        }
        AnalysisFragmentCompat analysisFragment = (AnalysisFragmentCompat) mCurrentFragment;
        analysisFragment.stopScanAnimation();
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

        final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logcatAppender);
    }

}
