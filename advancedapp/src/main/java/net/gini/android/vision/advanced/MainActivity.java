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
    private Button mButtonStartReviewPhoto;
    private Button mButtonStartReviewPhotoCompat;

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
        mButtonStartReviewPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReviewPhoto();
            }
        });
        mButtonStartReviewPhotoCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReviewPhotoCompat();
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

    private void startReviewPhoto() {
        Intent intent = new Intent(this, CustomReviewPhotoActivity.class);
        startActivity(intent);
    }

    private void startReviewPhotoCompat() {
        Intent intent = new Intent(this, CustomReviewPhotoAppCompatActivity.class);
        startActivity(intent);
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_scanner);
        mButtonStartScannerCompat = (Button) findViewById(R.id.button_start_scanner_compat);
        mButtonStartOnboarding = (Button) findViewById(R.id.button_start_onboarding);
        mButtonStartOnboardingCompat = (Button) findViewById(R.id.button_start_onboarding_compat);
        mButtonStartReviewPhoto = (Button) findViewById(R.id.button_start_review_photo);
        mButtonStartReviewPhotoCompat = (Button) findViewById(R.id.button_start_review_photo_compat);
    }
}
