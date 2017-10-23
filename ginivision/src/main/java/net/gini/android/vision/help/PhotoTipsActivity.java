package net.gini.android.vision.help;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import net.gini.android.vision.R;

public class PhotoTipsActivity extends AppCompatActivity {

    static final int RESULT_SHOW_CAMERA_SCREEN = RESULT_FIRST_USER + 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_photo_tips);
        findViewById(R.id.gv_button_photo_tips_camera).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        setResult(RESULT_SHOW_CAMERA_SCREEN);
                        finish();
                    }
                });
    }
}
