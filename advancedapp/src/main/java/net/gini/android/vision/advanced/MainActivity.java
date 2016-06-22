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
    }

    private void startScanner() {
        Intent intent = new Intent(this, CustomScannerActivity.class);
        startActivity(intent);
    }

    private void startScannerCompat() {
        Intent intent = new Intent(this, CustomScannerAppCompatActivity.class);
        startActivity(intent);
    }

    private void bindViews() {
        mButtonStartScanner = (Button) findViewById(R.id.button_start_scanner);
        mButtonStartScannerCompat = (Button) findViewById(R.id.button_start_scanner_compat);
    }
}
