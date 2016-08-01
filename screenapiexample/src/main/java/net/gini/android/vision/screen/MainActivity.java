package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.gini.android.ginivisiontest.BuildConfig;
import net.gini.android.ginivisiontest.R;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.onboarding.DefaultPages;
import net.gini.android.vision.onboarding.OnboardingPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_OUT_EXTRACTIONS = "EXTRA_OUT_EXTRACTIONS";

    private static final int REQUEST_SCAN = 1;

    private Button mButtonStartScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        addInputHandlers();
        setGiniVisionLibDebugging();
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
                startScanner();
            }
        });
    }

    private void startScanner() {
        Intent intent = new Intent(this, CameraActivity.class);

        // Add an extra page to the Onboarding pages
        intent.putParcelableArrayListExtra(CameraActivity.EXTRA_IN_ONBOARDING_PAGES, getOnboardingPages());

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

        // Start for result in order to receive the error result, in case something went wrong
        startActivityForResult(intent, REQUEST_SCAN);
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_scanner);
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
                    // Retrieve the extra we set in our ReviewActivity or AnalysisActivity subclasses
                    String extractions = data.getStringExtra(EXTRA_OUT_EXTRACTIONS);
                    Toast.makeText(this, extractions, Toast.LENGTH_LONG).show();
                    break;
                case CameraActivity.RESULT_ERROR:
                    // Something went wrong, retrieve the error
                    GiniVisionError error = data.getParcelableExtra(CameraActivity.EXTRA_OUT_ERROR);
                    if (error != null) {
                        Toast.makeText(this, "Error: " +
                                        error.getErrorCode() + " - " +
                                        error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }
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

