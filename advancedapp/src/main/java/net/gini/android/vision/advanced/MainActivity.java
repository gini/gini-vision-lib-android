package net.gini.android.vision.advanced;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.gini.android.visionadvtest.R;

public class MainActivity extends Activity {

    private Button mButtonStartScanner;
    private Button mButtonStartScannerCompat;
    private Button mButtonStartOnboarding;
    private Button mButtonStartOnboardingCompat;
    private Button mButtonStartReviewDocument;
    private Button mButtonStartReviewDocumentCompat;
    private Button mButtonStartAnalyseDocument;
    private Button mButtonStartAnalyseDocumentCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        addInputHandlers();
    }

    private void addInputHandlers() {
        mButtonStartScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanner();
            }
        });
        mButtonStartScannerCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScannerCompat();
            }
        });
        mButtonStartOnboarding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnboarding();
            }
        });
        mButtonStartOnboardingCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnboardingCompat();
            }
        });
        mButtonStartReviewDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReviewDocument();
            }
        });
        mButtonStartReviewDocumentCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReviewDocumentCompat();
            }
        });
        mButtonStartAnalyseDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnalyseDocument();
            }
        });
        mButtonStartAnalyseDocumentCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnalyseDocumentCompat();
            }
        });
    }

    private void startScanner() {
        Intent intent = new Intent(this, CustomScannerActivity.class);
        startActivity(intent);
    }

    private void startScannerCompat() {
        Intent intent = new Intent(this, CustomScannerAppCompatActivity.class);
        startActivity(intent);
    }

    private void startOnboarding() {
        Intent intent = new Intent(this, CustomOnboardingActivity.class);
        startActivity(intent);
    }

    private void startOnboardingCompat() {
        Intent intent = new Intent(this, CustomOnboardingAppCompatActivity.class);
        startActivity(intent);
    }

    private void startReviewDocument() {
        Intent intent = new Intent(this, CustomReviewDocumentActivity.class);
        startActivity(intent);
    }

    private void startReviewDocumentCompat() {
        Intent intent = new Intent(this, CustomReviewDocumentAppCompatActivity.class);
        startActivity(intent);
    }

    private void startAnalyseDocument() {
        Intent intent = new Intent(this, CustomAnalyseDocumentActivity.class);
        startActivity(intent);
    }

    private void startAnalyseDocumentCompat() {
        Intent intent = new Intent(this, CustomAnalyseDocumentAppCompatActivity.class);
        startActivity(intent);
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_scanner);
        mButtonStartScannerCompat = (Button) findViewById(R.id.button_start_scanner_compat);
        mButtonStartOnboarding = (Button) findViewById(R.id.button_start_onboarding);
        mButtonStartOnboardingCompat = (Button) findViewById(R.id.button_start_onboarding_compat);
        mButtonStartReviewDocument = (Button) findViewById(R.id.button_start_review_document);
        mButtonStartReviewDocumentCompat = (Button) findViewById(R.id.button_start_review_document_compat);
        mButtonStartAnalyseDocument = (Button) findViewById(R.id.button_start_analyse_document);
        mButtonStartAnalyseDocumentCompat = (Button) findViewById(R.id.button_start_analyse_document_compat);
    }
}
