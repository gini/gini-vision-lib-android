package net.gini.android.vision.screen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.gini.android.ginivisiontest.R;

public class NoExtractionsActivity extends AppCompatActivity {

    public static final int RESULT_START_GINI_VISION = RESULT_FIRST_USER + 1;

    private Button mButtonNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_extractions);
        bindViews();
        setInputHandlers();
    }

    private void bindViews() {
        mButtonNew = (Button) findViewById(R.id.button_new);
    }

    private void setInputHandlers() {
        mButtonNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_START_GINI_VISION);
                finish();
            }
        });
    }
}
