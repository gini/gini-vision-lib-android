package net.gini.android.vision.easy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.gini.android.ginivisiontest.R;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.onboarding.DefaultPages;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.scanner.ScannerActivity;
import net.gini.android.vision.scanner.photo.Photo;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_SCAN = 1;

    private Button mButtonStartScanner;

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
    }

    private void startScanner() {
        Intent intent = new Intent(this, ScannerActivity.class);
        intent.putParcelableArrayListExtra(ScannerActivity.EXTRA_ONBOARDING_PAGES, getOnboardingPages());
        ScannerActivity.setReviewPhotoActivityExtra(intent, this, ReviewPhotoActivity.class);
        startActivityForResult(intent, REQUEST_SCAN);
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_scanner);
    }

    private ArrayList<OnboardingPage> getOnboardingPages() {
        ArrayList<OnboardingPage> pages = new ArrayList<>(DefaultPages.getPages());
        pages.add(new OnboardingPage(R.string.additional_onboarding_page, R.drawable.additional_onboarding_illustration));
        return pages;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SCAN) {
            if (resultCode == RESULT_OK) {
                Photo original = data.getParcelableExtra(ScannerActivity.EXTRA_ORIGINAL_DOCUMENT);
                Photo document = data.getParcelableExtra(ScannerActivity.EXTRA_DOCUMENT);
                String extractions = data.getStringExtra(ReviewPhotoActivity.EXTRA_OUT_EXTRACTIONS);
            } else {
                GiniVisionError error = data.getParcelableExtra(ScannerActivity.EXTRA_ERROR);
                if (error != null) {
                    Toast.makeText(this, "Error: " +
                                    error.getErrorCode() + " - " +
                                    error.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
