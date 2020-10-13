package net.gini.android.vision.screen;

import static net.gini.android.vision.example.ExampleUtil.isPay5Extraction;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.gini.android.GiniApiType;
import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.ImportedFileValidationException;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.example.BaseExampleApp;
import net.gini.android.vision.example.RuntimePermissionHandler;
import net.gini.android.vision.onboarding.DefaultPagesPhone;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.requirements.GiniVisionRequirements;
import net.gini.android.vision.requirements.RequirementReport;
import net.gini.android.vision.requirements.RequirementsReport;
import net.gini.android.vision.tracking.AnalysisScreenEvent;
import net.gini.android.vision.tracking.CameraScreenEvent;
import net.gini.android.vision.tracking.Event;
import net.gini.android.vision.tracking.EventTracker;
import net.gini.android.vision.tracking.OnboardingScreenEvent;
import net.gini.android.vision.tracking.ReviewScreenEvent;
import net.gini.android.vision.util.CancellationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

/**
 * Entry point for the screen api example app.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OUT_EXTRACTIONS = "EXTRA_OUT_EXTRACTIONS";

    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    private static final int REQUEST_SCAN = 1;
    private static final int REQUEST_NO_EXTRACTIONS = 2;

    private Button mButtonStartScanner;
    private boolean mRestoredInstance;
    private RuntimePermissionHandler mRuntimePermissionHandler;
    private TextView mTextGiniVisionLibVersion;
    private TextView mTextAppVersion;
    private Spinner mGiniApiTypeSpinner;
    private CancellationToken mFileImportCancellationToken;
    private GiniApiType mGiniApiType = GiniApiType.DEFAULT;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        addInputHandlers();
        setGiniVisionLibDebugging();
        showVersions();
        createRuntimePermissionsHandler();
        mRestoredInstance = savedInstanceState != null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mRestoredInstance) {
            final Intent intent = getIntent();
            if (isIntentActionViewOrSend(intent)) {
                startGiniVisionLibraryForImportedFile(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFileImportCancellationToken != null) {
            mFileImportCancellationToken.cancel();
            mFileImportCancellationToken = null;
        }
    }

    private void createRuntimePermissionsHandler() {
        mRuntimePermissionHandler = RuntimePermissionHandler
                .forActivity(this)
                .withCameraPermissionDeniedMessage(
                        getString(R.string.camera_permission_denied_message))
                .withCameraPermissionRationale(getString(R.string.camera_permission_rationale))
                .withStoragePermissionDeniedMessage(
                        getString(R.string.storage_permission_denied_message))
                .withStoragePermissionRationale(getString(R.string.storage_permission_rationale))
                .withGrantAccessButtonTitle(getString(R.string.grant_access))
                .withCancelButtonTitle(getString(R.string.cancel))
                .build();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (isIntentActionViewOrSend(intent)) {
            startGiniVisionLibraryForImportedFile(intent);
        }
    }

    private void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        mRuntimePermissionHandler.requestStoragePermission(new RuntimePermissionHandler.Listener() {
            @Override
            public void permissionGranted() {
                doStartGiniVisionLibraryForImportedFile(importedFileIntent);
            }

            @Override
            public void permissionDenied() {
                finish();
            }
        });

    }

    private void doStartGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        // Configure the Gini Vision Library
        configureGiniVision();
        if (GiniVision.hasInstance() && GiniVision.getInstance().isMultiPageEnabled()) {
            mFileImportCancellationToken = GiniVision.getInstance().createIntentForImportedFiles(
                    importedFileIntent, this,
                    new AsyncCallback<Intent, ImportedFileValidationException>() {
                        @Override
                        public void onSuccess(final Intent result) {
                            mFileImportCancellationToken = null;
                            startActivityForResult(result, REQUEST_SCAN);
                        }

                        @Override
                        public void onError(final ImportedFileValidationException exception) {
                            mFileImportCancellationToken = null;
                            handleFileImportError(exception);
                        }

                        @Override
                        public void onCancelled() {
                            mFileImportCancellationToken = null;
                        }
                    });
        } else {
            try {
                final Intent giniVisionIntent =
                        GiniVision.createIntentForImportedFile(
                                importedFileIntent,
                                this,
                                ReviewActivity.class,
                                AnalysisActivity.class);
                startActivityForResult(giniVisionIntent, REQUEST_SCAN);

            } catch (final ImportedFileValidationException e) {
                e.printStackTrace();
                handleFileImportError(e);
            }
        }
    }

    private void handleFileImportError(final ImportedFileValidationException exception) {
        String message = exception.getMessage();
        if (exception.getValidationError() != null) {
            message = getString(exception.getValidationError().getTextResource());
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface,
                            final int i) {
                        finish();
                    }
                })
                .show();
    }

    private boolean isIntentActionViewOrSend(@NonNull final Intent intent) {
        final String action = intent.getAction();
        return Intent.ACTION_VIEW.equals(action)
                || Intent.ACTION_SEND.equals(action)
                || Intent.ACTION_SEND_MULTIPLE.equals(action);
    }

    @SuppressLint("SetTextI18n")
    private void showVersions() {
        mTextGiniVisionLibVersion.setText(
                "Gini Vision Library v" + net.gini.android.vision.BuildConfig.VERSION_NAME);
        mTextAppVersion.setText("v" + BuildConfig.VERSION_NAME);
    }

    private void setGiniVisionLibDebugging() {
        if (BuildConfig.DEBUG) {
            GiniVisionDebug.enable();
            configureLogging();
        }
    }

    private void addInputHandlers() {
        mButtonStartScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startGiniVisionLibrary();
            }
        });
        mGiniApiTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                mGiniApiType = GiniApiType.values()[position];
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });
    }

    private void startGiniVisionLibrary() {
        mRuntimePermissionHandler.requestCameraPermission(new RuntimePermissionHandler.Listener() {
            @Override
            public void permissionGranted() {
                doStartGiniVisionLibrary();
            }

            @Override
            public void permissionDenied() {

            }
        });
    }

    private void doStartGiniVisionLibrary() {
        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
        final RequirementsReport report = GiniVisionRequirements.checkRequirements(this);
        if (!report.isFulfilled()) {
            // In production apps you should not launch GVL if requirements were not fulfilled
            // We make an exception here to allow running the app on emulators
            showUnfulfilledRequirementsToast(report);
        }

        // Configure the Gini Vision Library
        configureGiniVision();

        final Intent intent = new Intent(this, CameraScreenApiActivity.class);

        // Deprecated: custom pages added above when creating the GiniVision instance
        // Uncomment to add an extra page to the Onboarding pages
//        intent.putParcelableArrayListExtra(CameraActivity.EXTRA_IN_ONBOARDING_PAGES, getOnboardingPages());

        // Deprecated: configuration applied above when creating the GiniVision instance
        // Set EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN to false to disable automatically showing the OnboardingActivity the
        // first time the CameraActivity is launched - we highly recommend letting the Gini Vision Library show the
        // OnboardingActivity at first run
        //intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);

        // Deprecated: configuration applied above when creating the GiniVision instance
        // Set EXTRA_IN_SHOW_ONBOARDING to true, to show the OnboardingActivity when the CameraActivity starts
        //intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING, true);

        // Set EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY to true, to close library on pressing the back
        // button from any Activity in the library
        //intent.putExtra(CameraActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, true);

        // Deprecated: configuration applied above when creating the GiniVision instance
        // Configure the features you would like to use
//        final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
//                GiniVisionFeatureConfiguration.buildNewConfiguration()
//                        .setDocumentImportEnabledFileTypes(
//                                DocumentImportEnabledFileTypes.PDF_AND_IMAGES)
//                        .setFileImportEnabled(true)
//                        .setQRCodeScanningEnabled(true)
//                        .build();
//
//        intent.putExtra(CameraActivity.EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION,
//                giniVisionFeatureConfiguration);

        // Set your ReviewActivity subclass
        CameraActivity.setReviewActivityExtra(intent, this, ReviewActivity.class);

        // Set your AnalysisActivity subclass
        CameraActivity.setAnalysisActivityExtra(intent, this, AnalysisActivity.class);

        // Start for result in order to receive the error result, in case something went wrong, or the extractions
        // To receive the extractions add it to the result Intent in ReviewActivity#onAddDataToResult(Intent) or
        // AnalysisActivity#onAddDataToResult(Intent) and retrieve them here in onActivityResult()
        startActivityForResult(intent, REQUEST_SCAN);
    }

    private void configureGiniVision() {
        final BaseExampleApp app = (BaseExampleApp) getApplication();
        GiniVision.cleanup(this);
        app.clearGiniVisionNetworkInstances();
        final GiniVision.Builder builder = GiniVision.newInstance()
                .setGiniVisionNetworkService(
                        app.getGiniVisionNetworkService("ScreenAPI",
                                mGiniApiType)
                ).setGiniVisionNetworkApi(app.getGiniVisionNetworkApi());
        if (mGiniApiType == GiniApiType.DEFAULT) {
            builder.setDocumentImportEnabledFileTypes(DocumentImportEnabledFileTypes.PDF_AND_IMAGES)
                    .setFileImportEnabled(true)
                    .setQRCodeScanningEnabled(true)
                    .setMultiPageEnabled(true);
        }
        builder.setFlashButtonEnabled(true);
        builder.setEventTracker(new GVLEventTracker());
        // Uncomment to turn off the camera flash by default
//        builder.setFlashOnByDefault(false);
        // Uncomment to disable back buttons (except in the review and analysis screens)
//        builder.setBackButtonsEnabled(false);
        // Uncomment to add an extra page to the Onboarding pages
//                builder.setCustomOnboardingPages(getOnboardingPages());
        // Uncomment to disable automatically showing the OnboardingActivity the
        // first time the CameraActivity is launched - we highly recommend letting the
        // Gini Vision Library show the OnboardingActivity at first run
//                builder.setShouldShowOnboardingAtFirstRun(false);
        // Uncomment to show the OnboardingActivity every time the CameraActivity starts
//                builder.setShouldShowOnboarding(true);
        // Uncomment to remove the Supported Formats help screen
//                builder.setSupportedFormatsHelpScreenEnabled(false);
        builder.build();
    }

    private void showUnfulfilledRequirementsToast(final RequirementsReport report) {
        final StringBuilder stringBuilder = new StringBuilder();
        final List<RequirementReport> requirementReports = report.getRequirementReports();
        for (int i = 0; i < requirementReports.size(); i++) {
            final RequirementReport requirementReport = requirementReports.get(i);
            if (!requirementReport.isFulfilled()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(requirementReport.getRequirementId());
                if (!requirementReport.getDetails().isEmpty()) {
                    stringBuilder.append(": ");
                    stringBuilder.append(requirementReport.getDetails());
                }
            }
        }
        Toast.makeText(this, "Requirements not fulfilled:\n" + stringBuilder,
                Toast.LENGTH_LONG).show();
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_scanner);
        mTextGiniVisionLibVersion = (TextView) findViewById(R.id.text_gini_vision_version);
        mTextAppVersion = (TextView) findViewById(R.id.text_app_version);
        mGiniApiTypeSpinner = findViewById(R.id.gini_api_type_spinner);
    }

    private ArrayList<OnboardingPage> getOnboardingPages() {
        // Adding a custom page to the default pages
        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();
        pages.add(new OnboardingPage(R.string.additional_onboarding_page,
                R.drawable.additional_onboarding_illustration));
        return pages;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        if (requestCode == REQUEST_SCAN) {
            if (data == null) {
                if (isIntentActionViewOrSend(getIntent())) {
                    finish();
                }
                return;
            }
            switch (resultCode) {
                case RESULT_CANCELED:
                    break;
                case RESULT_OK:
                    // Retrieve the extra we set in our ReviewActivity or AnalysisActivity subclasses' onAddDataToResult()
                    // method
                    // The payload format is up to you. For the example we added all the extractions as key-value pairs to
                    // a Bundle.
                    Bundle extractionsBundle = data.getBundleExtra(
                            CameraActivity.EXTRA_OUT_EXTRACTIONS);
                    if (extractionsBundle == null) {
                        extractionsBundle = data.getBundleExtra(MainActivity.EXTRA_OUT_EXTRACTIONS);
                    }
                    if (extractionsBundle == null) {
                        if (isIntentActionViewOrSend(getIntent())) {
                            finish();
                        }
                        return;
                    }
                    if (pay5ExtractionsAvailable(extractionsBundle)
                            || epsPaymentAvailable(extractionsBundle)) {
                        startExtractionsActivity(extractionsBundle);
                    } else {
                        // Show a special screen, if no Pay5 extractions were found to give
                        // the user some hints and tips
                        // for using the Gini Vision Library
                        startNoExtractionsActivity();
                    }
                    break;
                case CameraActivity.RESULT_ERROR:
                    // Something went wrong, retrieve and show the error
                    final GiniVisionError error = data.getParcelableExtra(
                            CameraActivity.EXTRA_OUT_ERROR);
                    if (error != null) {
                        Toast.makeText(this, "Error: "
                                        + error.getErrorCode() + " - "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
            if (isIntentActionViewOrSend(getIntent())) {
                finish();
            }
        } else if (requestCode == REQUEST_NO_EXTRACTIONS) {
            // The NoExtractionsActivity has a button for taking another picture which causes the activity to finish
            // and return the result code seen below
            if (resultCode == NoExtractionsActivity.RESULT_START_GINI_VISION) {
                startGiniVisionLibrary();
            }
        }
    }

    private boolean pay5ExtractionsAvailable(final Bundle extractionsBundle) {
        for (final String key : extractionsBundle.keySet()) {
            if (isPay5Extraction(key)) {
                return true;
            }
        }
        return false;
    }

    private boolean epsPaymentAvailable(final Bundle extractionsBundle) {
        for (final String key : extractionsBundle.keySet()) {
            if (key.equals("epsPaymentQRCodeUrl")) {
                return true;
            }
        }
        return false;
    }

    private void startNoExtractionsActivity() {
        final Intent intent = new Intent(this, NoExtractionsActivity.class);
        startActivityForResult(intent, REQUEST_NO_EXTRACTIONS);
    }

    private void startExtractionsActivity(final Bundle extractionsBundle) {
        final Intent intent = new Intent(this, ExtractionsActivity.class);
        intent.putExtra(ExtractionsActivity.EXTRA_IN_EXTRACTIONS, extractionsBundle);
        startActivity(intent);
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

    private class GVLEventTracker implements EventTracker {

        @Override
        public void onOnboardingScreenEvent(final Event<OnboardingScreenEvent> event) {
            switch (event.getType()) {
                case START:
                    LOG.info("Onboarding started");
                    break;
                case FINISH:
                    LOG.info("Onboarding finished");
                    break;
            }
        }

        @Override
        public void onCameraScreenEvent(final Event<CameraScreenEvent> event) {
            switch (event.getType()) {
                case TAKE_PICTURE:
                    LOG.info("Take picture");
                    break;
                case HELP:
                    LOG.info("Show help");
                    break;
                case EXIT:
                    LOG.info("Exit");
                    break;
            }
        }

        @Override
        public void onReviewScreenEvent(final Event<ReviewScreenEvent> event) {
            switch (event.getType()) {
                case NEXT:
                    LOG.info("Go next to analyse");
                    break;
                case BACK:
                    LOG.info("Go back to the camera");
                    break;
                case UPLOAD_ERROR:
                    final Throwable error = (Throwable) event.getDetails().get(ReviewScreenEvent.UPLOAD_ERROR_DETAILS_MAP_KEY.ERROR_OBJECT);
                    LOG.info("Upload failed:\nmessage: {}\nerror:",
                            event.getDetails().get(ReviewScreenEvent.UPLOAD_ERROR_DETAILS_MAP_KEY.MESSAGE),
                            error);
                    break;
            }
        }

        @Override
        public void onAnalysisScreenEvent(final Event<AnalysisScreenEvent> event) {
            switch (event.getType()) {
                case ERROR:
                    final Throwable error = (Throwable) event.getDetails().get(AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY.ERROR_OBJECT);
                    LOG.info("Analysis failed:\nmessage: {}\nerror:",
                            event.getDetails().get(AnalysisScreenEvent.ERROR_DETAILS_MAP_KEY.MESSAGE),
                            error);
                    break;
                case RETRY:
                    LOG.info("Retry analysis");
                    break;
                case CANCEL:
                    LOG.info("Analysis cancelled");
                    break;
            }
        }
    }
}

