package net.gini.android.vision.component;

import static net.gini.android.vision.example.ExampleUtil.isIntentActionViewOrSend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.requirements.RequirementReport;
import net.gini.android.vision.requirements.RequirementsReport;

import java.util.List;

public class MainActivity extends Activity {

    private Button mButtonStartGiniVisionStandard;
    private Button mButtonStartGiniVisionCompat;
    private TextView mTextGiniVisionLibVersion;
    private TextView mTextAppVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        addInputHandlers();
        setGiniVisionLibDebugging();
        showVersions();
        if (savedInstanceState == null) {
            final Intent intent = getIntent();
            if (isIntentActionViewOrSend(intent)) {
                startGiniVisionLibraryForImportedFile(intent);
            }
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (isIntentActionViewOrSend(intent)) {
            startGiniVisionLibraryForImportedFile(intent);
        }
    }

    private void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        new AlertDialog.Builder(this)
                .setMessage("Open file with standard or compat Gini Vision Library?")
                .setPositiveButton("Compat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        startGiniVisionCompat(importedFileIntent);
                        finish();
                    }
                })
                .setNegativeButton("Standard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        startGiniVisionStandard(importedFileIntent);
                        finish();
                    }
                }).show();
    }

    private void showVersions() {
        mTextGiniVisionLibVersion.setText("Gini Vision Library v" + net.gini.android.vision.BuildConfig.VERSION_NAME);
        mTextAppVersion.setText("v" + BuildConfig.VERSION_NAME);
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
        startGiniVisionStandard(null);
    }

    private void startGiniVisionStandard(@Nullable final Intent importedFileIntent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Toast.makeText(this, "Component API Standard requires API Level 17 or higher",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Uncomment to enable requirements check.
        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
//        RequirementsReport report = GiniVisionRequirements.checkRequirements(this);
//        if (!report.isFulfilled()) {
//            showUnfulfilledRequirementsToast(report);
//            return;
//        }

        final Intent intent;
        if (importedFileIntent == null) {
            intent = new Intent(this, GiniVisionActivity.class);
        } else {
            intent = new Intent(importedFileIntent);
            intent.setClass(this, GiniVisionActivity.class);
        }
        startActivity(intent);
    }

    private void startGiniVisionCompat() {
        startGiniVisionCompat(null);
    }

    private void startGiniVisionCompat(@Nullable final Intent importedFileIntent) {
        // Uncomment to enable requirements check.
        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
//        RequirementsReport report = GiniVisionRequirements.checkRequirements(this);
//        if (!report.isFulfilled()) {
//            showUnfulfilledRequirementsToast(report);
//            return;
//        }

        final Intent intent;
        if (importedFileIntent == null) {
            intent = new Intent(this, GiniVisionAppCompatActivity.class);
        } else {
            intent = new Intent(importedFileIntent);
            intent.setClass(this, GiniVisionAppCompatActivity.class);
        }
        startActivity(intent);
    }

    private void bindViews() {
        mButtonStartGiniVisionStandard = (Button) findViewById(R.id.button_start_gini_vision_standard);
        mButtonStartGiniVisionCompat = (Button) findViewById(R.id.button_start_gini_vision_compat);
        mTextGiniVisionLibVersion = (TextView) findViewById(R.id.text_gini_vision_version);
        mTextAppVersion = (TextView) findViewById(R.id.text_app_version);
    }

    private void showUnfulfilledRequirementsToast(RequirementsReport report) {
        StringBuilder stringBuilder = new StringBuilder();
        List<RequirementReport> requirementReports = report.getRequirementReports();
        for (int i = 0; i < requirementReports.size(); i++) {
            RequirementReport requirementReport = requirementReports.get(i);
            if (!requirementReport.isFulfilled()) {
                if (stringBuilder.length() > 0) {
                    stringBuilder.append("\n");
                }
                stringBuilder.append(requirementReport.getRequirementId());
                if (!requirementReport.getDetails().isEmpty()) {
                    stringBuilder.append(": ");
                    stringBuilder.append(requirementReport.getDetails());
                }
            }
        }
        Toast.makeText(this, "Requirements not fulfilled:\n" + stringBuilder, Toast.LENGTH_LONG).show();
    }
}
