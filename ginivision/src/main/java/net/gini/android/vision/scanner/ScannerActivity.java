package net.gini.android.vision.scanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

import net.gini.android.vision.R;

public class ScannerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_scanner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gv_scanner, menu);
        return true;
    }
}
