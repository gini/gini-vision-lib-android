package net.gini.android.vision.internal.fileimport;

import static android.content.Intent.ACTION_GET_CONTENT;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.ACTION_PICK;

import static net.gini.android.vision.GiniVisionError.ErrorCode.DOCUMENT_IMPORT;
import static net.gini.android.vision.internal.util.ContextHelper.isTablet;
import static net.gini.android.vision.internal.util.FeatureConfiguration.isMultiPageEnabled;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RelativeLayout;

import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.internal.fileimport.providerchooser.ProvidersAdapter;
import net.gini.android.vision.internal.fileimport.providerchooser.ProvidersAppItem;
import net.gini.android.vision.internal.fileimport.providerchooser.ProvidersAppItemSelectedListener;
import net.gini.android.vision.internal.fileimport.providerchooser.ProvidersItem;
import net.gini.android.vision.internal.fileimport.providerchooser.ProvidersSectionItem;
import net.gini.android.vision.internal.fileimport.providerchooser.ProvidersSeparatorItem;
import net.gini.android.vision.internal.fileimport.providerchooser.ProvidersSpanSizeLookup;
import net.gini.android.vision.internal.permission.PermissionRequestListener;
import net.gini.android.vision.internal.permission.RuntimePermissions;
import net.gini.android.vision.internal.ui.AlertDialogFragment;
import net.gini.android.vision.internal.ui.AlertDialogFragmentListener;
import net.gini.android.vision.internal.util.ApplicationHelper;
import net.gini.android.vision.internal.util.MimeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.Transition;
import androidx.transition.TransitionListenerAdapter;
import androidx.transition.TransitionManager;

/**
 * Internal use only.
 *
 * @suppress
 */
public class FileChooserActivity extends AppCompatActivity implements AlertDialogFragmentListener {

    private static final Logger LOG = LoggerFactory.getLogger(FileChooserActivity.class);

    private static final int REQ_CODE_CHOOSE_FILE = 1;

    public static final String EXTRA_IN_DOCUMENT_IMPORT_FILE_TYPES =
            "GV_EXTRA_IN_DOCUMENT_IMPORT_FILE_TYPES";

    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    public static final int GRID_SPAN_COUNT_PHONE = 3;
    public static final int GRID_SPAN_COUNT_TABLET = 6;

    private static final int ANIM_DURATION = 200;
    private static final int SHOW_ANIM_DELAY = 300;

    private static final String PERMISSION_DIALOG = "PERMISSION_DIALOG";
    private static final int PERMISSION_DENIED_DIALOG = 1;
    private static final int PERMISSION_RATIONALE_DIALOG = 2;

    private static final String SELECTED_APP_ITEM_KEY = "SELECTED_APP_ITEM_KEY";

    private RelativeLayout mLayoutRoot;
    private RecyclerView mFileProvidersView;
    private DocumentImportEnabledFileTypes mDocImportEnabledFileTypes =
            DocumentImportEnabledFileTypes.NONE;

    private final RuntimePermissions mRuntimePermissions = new RuntimePermissions();
    private ProvidersAppItem mSelectedAppItem;

    // Used to prevent fragment transactions after instance state has been saved
    private boolean mInstanceStateSaved;

    public static boolean canChooseFiles(@NonNull final Context context) {
        final List<ResolveInfo> imagePickerResolveInfos = queryImagePickers(context);
        final List<ResolveInfo> imageProviderResolveInfos = queryImageProviders(context);
        final List<ResolveInfo> pdfProviderResolveInfos = queryPdfProviders(context);

        return !imagePickerResolveInfos.isEmpty()
                || !imageProviderResolveInfos.isEmpty()
                || !pdfProviderResolveInfos.isEmpty();
    }

    public static Intent createIntent(final Context context) {
        return new Intent(context, FileChooserActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_file_chooser);
        bindViews();
        setInputHandlers();
        readExtras();
        setupFileProvidersView();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mInstanceStateSaved = true;
        outState.putParcelable(SELECTED_APP_ITEM_KEY, mSelectedAppItem);
    }

    @Override
    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSelectedAppItem = savedInstanceState.getParcelable(SELECTED_APP_ITEM_KEY);
    }

    @Override
    public void onPositiveButtonClicked(@NonNull final DialogInterface dialog, final int dialogId) {
        switch (dialogId) {
            case PERMISSION_DENIED_DIALOG:
                LOG.info("Open app details in Settings app");
                showAppDetailsSettingsScreen();
                break;
            case PERMISSION_RATIONALE_DIALOG:
                if (mSelectedAppItem != null) {
                    requestStoragePermissionWithoutRationale();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onNegativeButtonClicked(@NonNull final DialogInterface dialog, final int dialogId) {
    }

    private void bindViews() {
        mLayoutRoot = findViewById(R.id.gv_layout_root);
        mFileProvidersView = findViewById(R.id.gv_file_providers);
    }

    private void setInputHandlers() {
        mLayoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (mFileProvidersView == null) {
                    return;
                }
                final Object isShown = mFileProvidersView.getTag();
                if (isShown != null && (boolean) isShown) {
                    hideFileProviders(new TransitionListenerAdapter() {
                        @Override
                        public void onTransitionEnd(@NonNull final Transition transition) {
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final DocumentImportEnabledFileTypes enabledFileTypes =
                    (DocumentImportEnabledFileTypes) extras.getSerializable(
                            EXTRA_IN_DOCUMENT_IMPORT_FILE_TYPES);
            if (enabledFileTypes != null) {
                mDocImportEnabledFileTypes = enabledFileTypes;
            }
        }
    }

    private void setupFileProvidersView() {
        mFileProvidersView.setLayoutManager(new GridLayoutManager(this, getGridSpanCount()));
    }

    private int getGridSpanCount() {
        return isTablet(this) ? GRID_SPAN_COUNT_TABLET : GRID_SPAN_COUNT_PHONE;
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFileProviders();
        showFileProviders();
        // Sometimes onRestoreInstanceState() is not called after onSaveInstanceState() - seen on
        // a Galaxy S5 Neo with Android 6.0.1
        mInstanceStateSaved = false;
    }

    private void showFileProviders() {
        mLayoutRoot.postDelayed(new Runnable() {
            @Override
            public void run() {
                final AutoTransition transition = new AutoTransition();
                transition.setDuration(ANIM_DURATION);
                TransitionManager.beginDelayedTransition(mLayoutRoot, transition);
                final RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) mFileProvidersView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.BELOW);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                mFileProvidersView.setLayoutParams(layoutParams);
                mFileProvidersView.setTag(true);
            }
        }, SHOW_ANIM_DELAY);
    }

    @Override
    public void onBackPressed() {
        final boolean isShown = (boolean) mFileProvidersView.getTag();
        if (!isShown) {
            return;
        }
        overridePendingTransition(0, 0);
        hideFileProviders(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(@NonNull final Transition transition) {
                FileChooserActivity.super.onBackPressed();
            }
        });
    }

    private void hideFileProviders(
            @NonNull final Transition.TransitionListener transitionListener) {
        final AutoTransition transition = new AutoTransition();
        transition.setDuration(ANIM_DURATION);
        transition.addListener(transitionListener);
        TransitionManager.beginDelayedTransition(mLayoutRoot, transition);
        final RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) mFileProvidersView.getLayoutParams();
        layoutParams.addRule(RelativeLayout.BELOW, R.id.gv_space);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mFileProvidersView.setLayoutParams(layoutParams);
        mFileProvidersView.setTag(false);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CHOOSE_FILE) {
            setResult(resultCode, data);
        } else {
            final GiniVisionError error = new GiniVisionError(DOCUMENT_IMPORT,
                    "Unexpected request code for activity result.");
            final Intent result = new Intent();
            result.putExtra(EXTRA_OUT_ERROR, error);
            setResult(RESULT_ERROR, result);
        }
        finish();
    }

    private void populateFileProviders() {
        final List<ProvidersItem> providerItems = new ArrayList<>();
        List<ProvidersItem> imageProviderItems = new ArrayList<>();
        List<ProvidersItem> pdfProviderItems = new ArrayList<>();
        if (shouldShowImageProviders()) {
            final List<ResolveInfo> imagePickerResolveInfos = queryImagePickers(this);
            final List<ResolveInfo> imageProviderResolveInfos = queryImageProviders(this);
            imageProviderItems = getImageProviderItems(imagePickerResolveInfos,
                    imageProviderResolveInfos);
        }
        if (shouldShowPdfProviders()) {
            final List<ResolveInfo> pdfProviderResolveInfos = queryPdfProviders(this);
            pdfProviderItems = getPdfProviderItems(pdfProviderResolveInfos);
        }

        providerItems.addAll(imageProviderItems);
        if (!imageProviderItems.isEmpty() && !pdfProviderItems.isEmpty()) {
            providerItems.add(new ProvidersSeparatorItem());
        }
        providerItems.addAll(pdfProviderItems);

        ((GridLayoutManager) mFileProvidersView.getLayoutManager()).setSpanSizeLookup(
                new ProvidersSpanSizeLookup(providerItems, getGridSpanCount()));

        mFileProvidersView.setAdapter(new ProvidersAdapter(this, providerItems,
                new ProvidersAppItemSelectedListener() {
                    @Override
                    public void onItemSelected(@NonNull final ProvidersAppItem item) {
                        // Store the selected item, it is needed for requesting permission
                        // after showing the rationale
                        mSelectedAppItem = item;
                        requestStoragePermission();
                    }
                }));
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LOG.info("Requesting read storage permission");
            mRuntimePermissions.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                    createPermissionRequestListener());
        } else {
            storagePermissionGranted();
        }
    }

    private void requestStoragePermissionWithoutRationale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            LOG.info("Requesting read storage permission without rationale");
            mRuntimePermissions.requestPermissionWithoutRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE, createPermissionRequestListener());
        } else {
            storagePermissionGranted();
        }
    }

    @NonNull
    private PermissionRequestListener createPermissionRequestListener() {
        return new PermissionRequestListener() {
            @Override
            public void permissionGranted() {
                storagePermissionGranted();
            }

            @Override
            public void permissionDenied() {
                storagePermisionDenied();
            }

            @Override
            public void shouldShowRequestPermissionRationale(
                    @NonNull final RationaleResponse response) {
                LOG.info("Show read storage permission rationale");
                showStoragePermissionRationale();
            }
        };
    }

    private void storagePermissionGranted() {
        LOG.info("Read storage permission granted");
        if (mSelectedAppItem != null) {
            launchApp(mSelectedAppItem);
        }
    }

    private void storagePermisionDenied() {
        LOG.info("Read storage permission denied");
        showStoragePermissionDeniedDialog();
    }

    private void launchApp(@NonNull final ProvidersAppItem item) {
        final Intent intent = item.getIntent();
        intent.setClassName(
                item.getResolveInfo().activityInfo.packageName,
                item.getResolveInfo().activityInfo.name);
        startActivityForResult(intent, REQ_CODE_CHOOSE_FILE);
    }

    private void showStoragePermissionDeniedDialog() {
        final AlertDialogFragment dialogFragment = new AlertDialogFragment.Builder()
                .setMessage(R.string.gv_storage_permission_denied)
                .setPositiveButton(R.string.gv_storage_permission_denied_positive_button)
                .setNegativeButton(R.string.gv_storage_permission_denied_negative_button)
                .setDialogId(PERMISSION_DENIED_DIALOG)
                .disableCancelOnTouchOutside()
                .create();
        showPermissionDialog(dialogFragment);
    }

    private void showPermissionDialog(final AlertDialogFragment dialogFragment) {
        if (mInstanceStateSaved) {
            return;
        }
        final FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        final Fragment previous = getSupportFragmentManager().findFragmentByTag(PERMISSION_DIALOG);
        if (previous != null) {
            transaction.remove(previous);
        }
        transaction.addToBackStack(null);
        dialogFragment.show(transaction, PERMISSION_DIALOG);
    }

    private void showStoragePermissionRationale() {
        final AlertDialogFragment dialogFragment = new AlertDialogFragment.Builder()
                .setMessage(R.string.gv_storage_permission_rationale)
                .setPositiveButton(R.string.gv_storage_permission_rationale_positive_button)
                .setNegativeButton(R.string.gv_storage_permission_rationale_negative_button)
                .setDialogId(PERMISSION_RATIONALE_DIALOG)
                .disableCancelOnTouchOutside()
                .create();
        showPermissionDialog(dialogFragment);
    }

    private void showAppDetailsSettingsScreen() {
        ApplicationHelper.startApplicationDetailsSettings(this);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
            @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        final boolean handled = mRuntimePermissions.onRequestPermissionsResult(requestCode,
                permissions, grantResults);
        if (!handled && isReadExternalStoragePermission(permissions)) {
            if (isPermissionGranted(grantResults)) {
                storagePermissionGranted();
            } else {
                storagePermisionDenied();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean isReadExternalStoragePermission(@NonNull final String[] permissions) {
        return permissions.length == 1
                && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    private boolean isPermissionGranted(@NonNull final int[] grantResults) {
        return grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
    }

    private boolean shouldShowImageProviders() {
        return mDocImportEnabledFileTypes == DocumentImportEnabledFileTypes.IMAGES
                || mDocImportEnabledFileTypes == DocumentImportEnabledFileTypes.PDF_AND_IMAGES;
    }

    private boolean shouldShowPdfProviders() {
        return mDocImportEnabledFileTypes == DocumentImportEnabledFileTypes.PDF
                || mDocImportEnabledFileTypes == DocumentImportEnabledFileTypes.PDF_AND_IMAGES;
    }

    private List<ProvidersItem> getImageProviderItems(
            final List<ResolveInfo> imagePickerResolveInfos,
            final List<ResolveInfo> imageProviderResolveInfos) {
        final List<ProvidersItem> providerItems = new ArrayList<>();
        if (!imagePickerResolveInfos.isEmpty()
                || !imageProviderResolveInfos.isEmpty()) {
            providerItems.add(new ProvidersSectionItem(
                    getString(R.string.gv_file_chooser_fotos_section_header)));
            final Intent imagePickerIntent = createImagePickerIntent();
            for (final ResolveInfo imagePickerResolveInfo : imagePickerResolveInfos) {
                providerItems.add(new ProvidersAppItem(imagePickerIntent, imagePickerResolveInfo)); // NOPMD
            }
            final Intent getImageDocumentIntent = createGetImageDocumentIntent();
            for (final ResolveInfo imageProviderResolveInfo : imageProviderResolveInfos) {
                providerItems.add(
                        new ProvidersAppItem(getImageDocumentIntent, imageProviderResolveInfo)); // NOPMD
            }
        }
        return providerItems;
    }

    private List<ProvidersItem> getPdfProviderItems(
            final List<ResolveInfo> pdfProviderResolveInfos) {
        final List<ProvidersItem> providerItems = new ArrayList<>();
        if (!pdfProviderResolveInfos.isEmpty()) {
            providerItems.add(new ProvidersSectionItem(
                    getString(R.string.gv_file_chooser_pdfs_section_header)));
            final Intent getPdfDocumentIntent = createGetPdfDocumentIntent();
            for (final ResolveInfo pdfProviderResolveInfo : pdfProviderResolveInfos) {
                providerItems.add(
                        new ProvidersAppItem(getPdfDocumentIntent, pdfProviderResolveInfo)); // NOPMD
            }
        }
        return providerItems;
    }

    @NonNull
    private static List<ResolveInfo> queryImagePickers(@NonNull final Context context) {
        final Intent intent = createImagePickerIntent();

        return context.getPackageManager().queryIntentActivities(intent, 0);
    }

    @NonNull
    private static Intent createImagePickerIntent() {
        final Intent intent = new Intent(ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                MimeType.IMAGE_WILDCARD.asString());
        if (isMultiPageEnabled()) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        return intent;
    }

    @NonNull
    private static List<ResolveInfo> queryImageProviders(@NonNull final Context context) {
        final Intent intent = createGetImageDocumentIntent();

        return context.getPackageManager().queryIntentActivities(intent, 0);
    }

    @NonNull
    private static Intent createGetImageDocumentIntent() {
        final Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(ACTION_OPEN_DOCUMENT);
        } else {
            intent = new Intent(ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(MimeType.IMAGE_WILDCARD.asString());
        if (isMultiPageEnabled()) {
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        return intent;
    }

    @NonNull
    private static List<ResolveInfo> queryPdfProviders(@NonNull final Context context) {
        final Intent intent = createGetPdfDocumentIntent();

        return context.getPackageManager().queryIntentActivities(intent, 0);
    }

    @NonNull
    private static Intent createGetPdfDocumentIntent() {
        final Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(ACTION_OPEN_DOCUMENT);
        } else {
            intent = new Intent(ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(MimeType.APPLICATION_PDF.asString());
        return intent;
    }
}
