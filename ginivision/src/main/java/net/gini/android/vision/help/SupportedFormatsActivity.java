package net.gini.android.vision.help;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.R;

public class SupportedFormatsActivity extends AppCompatActivity {

    public static final String EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION =
            "GV_EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION";

    private GiniVisionFeatureConfiguration mGiniVisionFeatureConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_supported_formats);
        readExtras();
        setUpFormatsList();
    }

    private void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mGiniVisionFeatureConfiguration = extras.getParcelable(
                    EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION);
            if (mGiniVisionFeatureConfiguration == null) {
                mGiniVisionFeatureConfiguration =
                        GiniVisionFeatureConfiguration.buildNewConfiguration().build();
            }
        }
    }

    private void setUpFormatsList() {
        final RecyclerView recyclerView = findViewById(R.id.gv_formats_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new SupportedFormatsAdapter(mGiniVisionFeatureConfiguration));
    }
}
