package net.gini.android.vision.component;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import net.gini.android.visionadvtest.R;


/**
 * <p>
 *     Shown when none of the Pay5 extractions were received.
 * </p>
 * <p>
 *     Displays information about rotating the image to the correct orientation.
 * </p>
 * <p>
 *     We recommend showing a similar screen in your app to aid the user in taking better pictures to improve the
 *     quality of the extractions.
 * </p>
 */
public class NoExtractionsActivity extends AppCompatActivity {

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
                finish();
            }
        });
    }
}
