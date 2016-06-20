package net.gini.android.vision.advanced;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.gini.android.vision.scanner.ScannerFragmentListener;
import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.visionadvtest.R;

public class CustomScannerActivity extends Activity implements ScannerFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
    }

    @Override
    public void onPhotoTaken(Photo photo) {
        Intent intent = new Intent(this, CustomReviewPhotoActivity.class);
        startActivity(intent);
    }
}
