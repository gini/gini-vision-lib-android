package net.gini.android.vision.component;

import static net.gini.android.vision.example.ExampleUtil.isIntentActionViewOrSend;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.gini.android.GiniApiType;
import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.component.camera.compat.CameraExampleAppCompatActivity;
import net.gini.android.vision.component.camera.standard.CameraExampleActivity;
import net.gini.android.vision.example.BaseExampleApp;
import net.gini.android.vision.example.RuntimePermissionHandler;
import net.gini.android.vision.onboarding.DefaultPagesPhone;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.requirements.GiniVisionRequirements;
import net.gini.android.vision.requirements.RequirementReport;
import net.gini.android.vision.requirements.RequirementsReport;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Entry point for the component api example app.
 */
public class MainActivity extends AppCompatActivity {

    private Button mButtonStartGiniVisionCompat;
    private Button mButtonStartGiniVisionStandard;
    private boolean mRestoredInstance;
    private RuntimePermissionHandler mRuntimePermissionHandler;
    private TextView mTextAppVersion;
    private TextView mTextGiniVisionLibVersion;
    private Spinner mGiniApiTypeSpinner;
    private GiniApiType mGiniApiType = GiniApiType.DEFAULT;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
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

    private void initGiniVision() {
        final BaseExampleApp app = (BaseExampleApp) getApplication();
        GiniVision.cleanup(this);
        app.clearGiniVisionNetworkInstances();
        final GiniVision.Builder builder = GiniVision.newInstance()
                .setGiniVisionNetworkService(
                        app.getGiniVisionNetworkService("ComponentAPI",
                                mGiniApiType)
                ).setGiniVisionNetworkApi(app.getGiniVisionNetworkApi());
        if (mGiniApiType == GiniApiType.DEFAULT) {
            builder.setDocumentImportEnabledFileTypes(DocumentImportEnabledFileTypes.PDF_AND_IMAGES)
                    .setFileImportEnabled(true)
                    .setQRCodeScanningEnabled(true)
                    .setMultiPageEnabled(true);
        }
        builder.setFlashButtonEnabled(true);
        // Uncomment to turn off the camera flash by default
//        builder.setFlashOnByDefault(false);
        // Uncomment to add an extra page to the Onboarding pages
//        builder.setCustomOnboardingPages(getOnboardingPages());
        // Uncomment to remove the Supported Formats help screen
//        builder.setSupportedFormatsHelpScreenEnabled(false);
        builder.build();
    }

    private ArrayList<OnboardingPage> getOnboardingPages() {
        // Adding a custom page to the default pages
        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();
        pages.add(new OnboardingPage(R.string.additional_onboarding_page,
                R.drawable.additional_onboarding_illustration));
        return pages;
    }

    private void startGiniVisionLibraryForImportedFile(final Intent importedFileIntent) {
        initGiniVision();
        new AlertDialog.Builder(this)
                .setMessage(R.string.open_file_standard_or_compat)
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
            public void onClick(final View v) {
                startGiniVisionStandard();
            }
        });
        mButtonStartGiniVisionCompat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startGiniVisionCompat();
            }
        });
        mGiniApiTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                mGiniApiType = GiniApiType.values()[position];
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {

            }
        });
    }

    private void startGiniVisionCompat() {
        initGiniVision();
        mRuntimePermissionHandler.requestCameraPermission(
                new RuntimePermissionHandler.Listener() {
                    @Override
                    public void permissionGranted() {
                        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
                        final RequirementsReport report = GiniVisionRequirements.checkRequirements(
                                MainActivity.this);
                        if (!report.isFulfilled()) {
                            // In production apps you should not launch GVL if requirements were not fulfilled
                            // We make an exception here to allow running the app on emulators
                            showUnfulfilledRequirementsToast(report);
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
        initGiniVision();
        mRuntimePermissionHandler.requestCameraPermission(
                new RuntimePermissionHandler.Listener() {
                    @Override
                    public void permissionGranted() {
                        // NOTE: on Android 6.0 and later the camera permission is required before checking the requirements
                        final RequirementsReport report = GiniVisionRequirements.checkRequirements(
                                MainActivity.this);
                        if (!report.isFulfilled()) {
                            // In production apps you should not launch GVL if requirements were not fulfilled
                            // We make an exception here to allow running the app on emulators
                            showUnfulfilledRequirementsToast(report);
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

    private void showUnfulfilledRequirementsToast(final RequirementsReport report) {
        final StringBuilder stringBuilder = new StringBuilder();
        final List<RequirementReport> requirementReports = report.getRequirementReports();
        for (int i = 0; i < requirementReports.size(); i++) {
            final RequirementReport requirementReport = requirementReports.get(i);
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
        Toast.makeText(this, getString(R.string.unfulfilled_requirements, stringBuilder),
                Toast.LENGTH_LONG).show();
    }

    private void bindViews() {
        mButtonStartGiniVisionStandard = (Button) findViewById(
                R.id.button_start_gini_vision_standard);
        mButtonStartGiniVisionCompat = (Button) findViewById(R.id.button_start_gini_vision_compat);
        mTextGiniVisionLibVersion = (TextView) findViewById(R.id.text_gini_vision_version);
        mTextAppVersion = (TextView) findViewById(R.id.text_app_version);
        mGiniApiTypeSpinner = findViewById(R.id.gini_api_type_spinner);
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

    @SuppressLint("SetTextI18n")
    private void showVersions() {
        mTextGiniVisionLibVersion.setText(
                "Gini Vision Library v" + net.gini.android.vision.BuildConfig.VERSION_NAME);
        mTextAppVersion.setText("v" + BuildConfig.VERSION_NAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
