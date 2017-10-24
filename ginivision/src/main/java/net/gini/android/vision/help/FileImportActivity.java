package net.gini.android.vision.help;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import net.gini.android.vision.R;

public class FileImportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_file_import);

        // Show illustration for the first section only if available (height > 0)
        final Drawable section1Illustration = ContextCompat.getDrawable(this,
                R.drawable.gv_file_import_section_1_illustration);
        if (section1Illustration.getMinimumHeight() > 0) {
            final ImageView section1ImageView = findViewById(R.id.gv_section_1_illustration);
            section1ImageView.setVisibility(View.VISIBLE);
            section1ImageView.setImageDrawable(section1Illustration);
        }
    }
}
