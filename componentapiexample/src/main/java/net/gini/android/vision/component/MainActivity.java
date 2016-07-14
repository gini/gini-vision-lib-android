package net.gini.android.vision.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.visionadvtest.BuildConfig;
import net.gini.android.visionadvtest.R;

public class MainActivity extends Activity {

    private Button mButtonStartScanner;
    private Button mButtonStartScannerCompat;
    private Button mButtonStartOnboarding;
    private Button mButtonStartOnboardingWithoutLastPage;
    private Button mButtonStartOnboardingCompat;
    private Button mButtonStartOnboardingWithoutLastPageCompat;
    private Button mButtonStartReviewDocument;
    private Button mButtonStartReviewDocumentCompat;
    private Button mButtonStartAnalyzeDocument;
    private Button mButtonStartAnalyzeDocumentCompat;

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
        }
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
        mButtonStartOnboardingWithoutLastPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnboardingWithoutLastPage();
            }
        });
        mButtonStartOnboardingCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnboardingCompat();
            }
        });
        mButtonStartOnboardingWithoutLastPageCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnboardingWithoutLastPageCompat();
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
        mButtonStartAnalyzeDocument.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnalyzeDocument();
            }
        });
        mButtonStartAnalyzeDocumentCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnalyzeDocumentCompat();
            }
        });
    }

    private void startScanner() {
        Intent intent = new Intent(this, CustomCameraActivity.class);
        startActivity(intent);
    }

    private void startScannerCompat() {
        Intent intent = new Intent(this, CustomCameraAppCompatActivity.class);
        startActivity(intent);
    }

    private void startOnboarding() {
        Intent intent = new Intent(this, CustomOnboardingActivity.class);
        startActivity(intent);
    }

    private void startOnboardingWithoutLastPage() {
        Intent intent = new Intent(this, CustomOnboardingActivity.class);
        intent.putExtra(CustomOnboardingActivity.EXTRA_WITHOUT_EMPTY_LAST_PAGE, true);
        startActivity(intent);
    }

    private void startOnboardingCompat() {
        Intent intent = new Intent(this, CustomOnboardingAppCompatActivity.class);
        startActivity(intent);
    }

    private void startOnboardingWithoutLastPageCompat() {
        Intent intent = new Intent(this, CustomOnboardingAppCompatActivity.class);
        intent.putExtra(CustomOnboardingAppCompatActivity.EXTRA_WITHOUT_EMPTY_LAST_PAGE, true);
        startActivity(intent);
    }

    private void startReviewDocument() {
        Intent intent = new Intent(this, CustomReviewActivity.class);
        startActivity(intent);
    }

    private void startReviewDocumentCompat() {
        Intent intent = new Intent(this, CustomReviewAppCompatActivity.class);
        startActivity(intent);
    }

    private void startAnalyzeDocument() {
        Intent intent = new Intent(this, CustomAnalysisActivity.class);
        startActivity(intent);
    }

    private void startAnalyzeDocumentCompat() {
        Intent intent = new Intent(this, CustomAnalysisAppCompatActivity.class);
        startActivity(intent);
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_camera);
        mButtonStartScannerCompat = (Button) findViewById(R.id.button_start_camera_compat);
        mButtonStartOnboarding = (Button) findViewById(R.id.button_start_onboarding);
        mButtonStartOnboardingWithoutLastPage = (Button) findViewById(R.id.button_start_onboarding_without_last_page);
        mButtonStartOnboardingCompat = (Button) findViewById(R.id.button_start_onboarding_compat);
        mButtonStartOnboardingWithoutLastPageCompat = (Button) findViewById(R.id.button_start_onboarding_without_last_page_compat);
        mButtonStartReviewDocument = (Button) findViewById(R.id.button_start_review_document);
        mButtonStartReviewDocumentCompat = (Button) findViewById(R.id.button_start_review_document_compat);
        mButtonStartAnalyzeDocument = (Button) findViewById(R.id.button_start_analysis);
        mButtonStartAnalyzeDocumentCompat = (Button) findViewById(R.id.button_start_analysis_compat);
    }
}
