package net.gini.android.vision.component;

import static net.gini.android.vision.example.ExampleUtil.isIntentActionViewOrSend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.component.camera.compat.CameraExampleAppCompatActivity;
import net.gini.android.vision.component.camera.standard.CameraExampleActivity;
import net.gini.android.vision.example.RuntimePermissionHandler;
import net.gini.android.vision.requirements.GiniVisionRequirements;
import net.gini.android.vision.requirements.RequirementReport;
import net.gini.android.vision.requirements.RequirementsReport;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mButtonStartGiniVisionCompat;
    private Button mButtonStartGiniVisionStandard;
    private boolean mRestoredInstance;
    private RuntimePermissionHandler mRuntimePermissionHandler;
    private TextView mTextAppVersion;
    private TextView mTextGiniVisionLibVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        addInputHandlers();
        setGiniVisionLibDebugging();
        showVersions();
        createRuntimePermissionsHandler();
        mRestoredInstance = savedInstanceState != null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mRestoredInstance) {
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
                        startGiniVisionCompatForImportedFile(importedFileIntent);
                    }
                })
                .setNegativeButton("Standard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        startGiniVisionStandardForImportedFile(importedFileIntent);
                    }
                }).show();
    }

    private void startGiniVisionStandardForImportedFile(@NonNull final Intent importedFileIntent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Toast.makeText(this, "Component API Standard requires API Level 17 or higher",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mRuntimePermissionHandler.requestStoragePermission(
                new RuntimePermissionHandler.Listener() {
                    @Override
                    public void permissionGranted() {
                        final Intent intent = new Intent(importedFileIntent);
                        intent.setClass(MainActivity.this, CameraExampleActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void permissionDenied() {
                        finish();
                    }
                });
    }

    private void startGiniVisionCompatForImportedFile(@Nullable final Intent importedFileIntent) {
        mRuntimePermissionHandler.requestStoragePermission(
                new RuntimePermissionHandler.Listener() {
                    @Override
                    public void permissionGranted() {
                        final Intent intent = new Intent(importedFileIntent);
                        intent.setClass(MainActivity.this, CameraExampleAppCompatActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void permissionDenied() {
                        finish();
                    }
                });
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

    private void startGiniVisionCompat() {
        mRuntimePermissionHandler.requestCameraPermission(
                new RuntimePermissionHandler.Listener() {
                    @Override
                    public void permissionGranted() {
                        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
                        final RequirementsReport report = GiniVisionRequirements.checkRequirements(
                                MainActivity.this);
                        if (!report.isFulfilled()) {
                            showUnfulfilledRequirementsToast(report);
                            return;
                        }
                        final Intent intent = new Intent(MainActivity.this,
                                CameraExampleAppCompatActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void permissionDenied() {

                    }
                });
    }

    private void startGiniVisionStandard() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Toast.makeText(this, "Component API Standard requires API Level 17 or higher",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mRuntimePermissionHandler.requestCameraPermission(
                new RuntimePermissionHandler.Listener() {
                    @Override
                    public void permissionGranted() {
                        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
                        final RequirementsReport report = GiniVisionRequirements.checkRequirements(
                                MainActivity.this);
                        if (!report.isFulfilled()) {
                            showUnfulfilledRequirementsToast(report);
                            return;
                        }
                        final Intent intent = new Intent(MainActivity.this,
                                CameraExampleActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void permissionDenied() {

                    }
                });
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
        Toast.makeText(this, "Requirements not fulfilled:\n" + stringBuilder,
                Toast.LENGTH_LONG).show();
    }

    private void bindViews() {
        mButtonStartGiniVisionStandard = (Button) findViewById(
                R.id.button_start_gini_vision_standard);
        mButtonStartGiniVisionCompat = (Button) findViewById(R.id.button_start_gini_vision_compat);
        mTextGiniVisionLibVersion = (TextView) findViewById(R.id.text_gini_vision_version);
        mTextAppVersion = (TextView) findViewById(R.id.text_app_version);
    }

    private void createRuntimePermissionsHandler() {
        mRuntimePermissionHandler = RuntimePermissionHandler
                .forActivity(this)
                .withCameraPermissionDeniedMessage(
                        getString(R.string.camera_permission_denied_message))
                .withCameraPermissionRationale(getString(R.string.camera_permission_rationale))
                .withStoragePermissionDeniedMessage(
                        getString(R.string.storage_permission_denied_message))
                .withStoragePermissionRationale(getString(R.string.storage_permission_rationale))
                .withGrantAccessButtonTitle(getString(R.string.grant_access))
                .withCancelButtonTitle(getString(R.string.cancel))
                .build();
    }

    private void setGiniVisionLibDebugging() {
        if (BuildConfig.DEBUG) {
            GiniVisionDebug.enable();
        }
    }

    private void showVersions() {
        mTextGiniVisionLibVersion.setText(
                "Gini Vision Library v" + net.gini.android.vision.BuildConfig.VERSION_NAME);
        mTextAppVersion.setText("v" + BuildConfig.VERSION_NAME);
    }
}
