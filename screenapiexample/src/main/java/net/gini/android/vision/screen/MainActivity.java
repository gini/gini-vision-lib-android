package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.gini.android.ginivisiontest.BuildConfig;
import net.gini.android.ginivisiontest.R;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.onboarding.DefaultPages;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.requirements.RequirementReport;
import net.gini.android.vision.requirements.RequirementsReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OUT_EXTRACTIONS = "EXTRA_OUT_EXTRACTIONS";

    private static final int REQUEST_SCAN = 1;
    private static final int REQUEST_NO_EXTRACTIONS = 2;

    private Button mButtonStartScanner;
    private TextView mTextGiniVisionLibVersion;
    private TextView mTextAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        addInputHandlers();
        setGiniVisionLibDebugging();
        showVersions();
    }

    private void showVersions() {
        mTextGiniVisionLibVersion.setText("Gini Vision Library v" + net.gini.android.vision.BuildConfig.VERSION_NAME);
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
            public void onClick(View v) {
                startGiniVisionLibrary();
            }
        });
    }

    private void startGiniVisionLibrary() {
        // Uncomment to enable requirements check.
        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
//        RequirementsReport report = GiniVisionRequirements.checkRequirements(this);
//        if (!report.isFulfilled()) {
//            showUnfulfilledRequirementsToast(report);
//            return;
//        }

        Intent intent = new Intent(this, CameraActivity.class);

        // Uncomment to add an extra page to the Onboarding pages
//        intent.putParcelableArrayListExtra(CameraActivity.EXTRA_IN_ONBOARDING_PAGES, getOnboardingPages());

        // Set EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN to false to disable automatically showing the OnboardingActivity the
        // first time the CameraActivity is launched - we highly recommend letting the Gini Vision Library show the
        // OnboardingActivity at first run
        //intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);

        // Set EXTRA_IN_SHOW_ONBOARDING to true, to show the OnboardingActivity when the CameraActivity starts
        //intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING, true);

        // Set your ReviewActivity subclass
        CameraActivity.setReviewActivityExtra(intent, this, ReviewActivity.class);

        // Set your AnalysisActivity subclass
        CameraActivity.setAnalysisActivityExtra(intent, this, AnalysisActivity.class);

        // Start for result in order to receive the error result, in case something went wrong, or the extractions
        // To receive the extractions add it to the result Intent in ReviewActivity#onAddDataToResult(Intent) or
        // AnalysisActivity#onAddDataToResult(Intent) and retrieve them here in onActivityResult()
        startActivityForResult(intent, REQUEST_SCAN);
    }

    private void showUnfulfilledRequirementsToast(RequirementsReport report) {
        StringBuilder stringBuilder = new StringBuilder();
        List<RequirementReport> requirementReports = report.getRequirementReports();
        for (int i = 0; i < requirementReports.size(); i++) {
            RequirementReport requirementReport = requirementReports.get(i);
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
        Toast.makeText(this, "Requirements not fulfilled:\n" + stringBuilder, Toast.LENGTH_LONG).show();
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_scanner);
        mTextGiniVisionLibVersion = (TextView) findViewById(R.id.text_gini_vision_version);
        mTextAppVersion = (TextView) findViewById(R.id.text_app_version);
    }

    private ArrayList<OnboardingPage> getOnboardingPages() {
        // Adding a custom page to the default pages
        ArrayList<OnboardingPage> pages = DefaultPages.asArrayList();
        pages.add(new OnboardingPage(R.string.additional_onboarding_page, R.drawable.additional_onboarding_illustration));
        return pages;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN) {
            if (data == null) {
                return;
            }
            switch (resultCode) {
                case RESULT_OK:
                    // Retrieve the extra we set in our ReviewActivity or AnalysisActivity subclasses' onAddDataToResult()
                    // method
                    // The payload format is up to you. For the example we added all the extractions as key-value pairs to
                    // a Bundle.
                    Bundle extractionsBundle = data.getBundleExtra(EXTRA_OUT_EXTRACTIONS);
                    if (extractionsBundle != null) {
                        // We display only the Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference
                        if (pay5ExtractionsAvailable(extractionsBundle)) {
                            startExtractionsActivity(extractionsBundle);
                        } else {
                            // Show a special screen, if no Pay5 extractions were found to give the user some hints and tips
                            // for using the Gini Vision Library
                            startNoExtractionsActivity();
                        }
                    } else {
                        Toast.makeText(this, "No extractions received", Toast.LENGTH_LONG).show();
                    }
                    break;
                case CameraActivity.RESULT_ERROR:
                    // Something went wrong, retrieve and show the error
                    GiniVisionError error = data.getParcelableExtra(CameraActivity.EXTRA_OUT_ERROR);
                    if (error != null) {
                        Toast.makeText(this, "Error: " +
                                        error.getErrorCode() + " - " +
                                        error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        } else if (requestCode == REQUEST_NO_EXTRACTIONS) {
            // The NoExtractionsActivity has a button for taking another picture which causes the activity to finish
            // and return the result code seen below
            if (resultCode == NoExtractionsActivity.RESULT_START_GINI_VISION) {
                startGiniVisionLibrary();
            }
        }
    }

    private boolean pay5ExtractionsAvailable(Bundle extractionsBundle) {
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

    private void startNoExtractionsActivity() {
        Intent intent = new Intent(this, NoExtractionsActivity.class);
        startActivityForResult(intent, REQUEST_NO_EXTRACTIONS);
    }

    private void startExtractionsActivity(Bundle extractionsBundle) {
        Intent intent = new Intent(this, ExtractionsActivity.class);
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

        final ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(logcatAppender);
    }
}

