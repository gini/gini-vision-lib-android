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

    private Button mButtonStartGiniVisionStandard;
    private Button mButtonStartGiniVisionCompat;

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
        mButtonStartGiniVisionStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGiniVisionStandard();
            }
        });
        mButtonStartGiniVisionCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGiniVisionCompat();
            }
        });
    }

    private void startGiniVisionStandard() {
        Intent intent = new Intent(this, GiniVisionActivity.class);
        startActivity(intent);
    }

    private void startGiniVisionCompat() {
        Intent intent = new Intent(this, GiniVisionAppCompatActivity.class);
        startActivity(intent);
    }

    private void bindViews() {
        mButtonStartGiniVisionStandard = (Button) findViewById(R.id.button_start_gini_vision_standard);
        mButtonStartGiniVisionCompat = (Button) findViewById(R.id.button_start_gini_vision_compat);
    }
}
