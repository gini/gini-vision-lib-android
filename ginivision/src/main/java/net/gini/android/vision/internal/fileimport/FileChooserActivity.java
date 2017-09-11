package net.gini.android.vision.internal.fileimport;

import static android.content.Intent.ACTION_GET_CONTENT;
import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.ACTION_PICK;

import static net.gini.android.vision.GiniVisionError.ErrorCode.DOCUMENT_IMPORT;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;

import java.util.ArrayList;
import java.util.List;

public class FileChooserActivity extends AppCompatActivity {

    private static final int REQ_CODE_CHOOSE_FILE = 1;

    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    static final int GRID_SPAN_COUNT = 4;

    private RecyclerView mFileProvidersView;

    public static Intent createIntent(final Context context) {
        return new Intent(context, FileChooserActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_file_chooser);
        bindViews();
        setupFileProvidersView();
        overridePendingTransition(0, 0);
    }

    private void bindViews() {
        mFileProvidersView = (RecyclerView) findViewById(R.id.gv_file_providers);
    }

    private void setupFileProvidersView() {
        mFileProvidersView.setLayoutManager(new GridLayoutManager(this, GRID_SPAN_COUNT));
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFileProviders();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
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

    private void showFileProviders() {
        final List<ResolveInfo> imagePickerResolveInfos = queryImagePickers();
        final List<ResolveInfo> imageProviderResolveInfos = queryImageProviders();
        final List<ResolveInfo> pdfProviderResolveInfos = queryPdfProviders();

        final List<FileProvidersItem> providerItems = new ArrayList<>();

        providerItems.add(new FileProvidersSectionItem("Photos"));
        final Intent imagePickerIntent = createImagePickerIntent();
        for (final ResolveInfo imagePickerResolveInfo : imagePickerResolveInfos) {
            providerItems.add(new FileProvidersAppItem(imagePickerIntent, imagePickerResolveInfo));
        }
        final Intent getImageDocumentIntent = createGetImageDocumentIntent();
        for (final ResolveInfo imageProviderResolveInfo : imageProviderResolveInfos) {
            providerItems.add(
                    new FileProvidersAppItem(getImageDocumentIntent, imageProviderResolveInfo));
        }

        providerItems.add(new FileProvidersSectionItem("PDFs"));
        final Intent getPdfDocumentIntent = createGetPdfDocumentIntent();
        for (final ResolveInfo pdfProviderResolveInfo : pdfProviderResolveInfos) {
            providerItems.add(
                    new FileProvidersAppItem(getPdfDocumentIntent, pdfProviderResolveInfo));
        }

        ((GridLayoutManager) mFileProvidersView.getLayoutManager()).setSpanSizeLookup(
                new FileProvidersSpanSizeLookup(providerItems));

        mFileProvidersView.setAdapter(new FileProvidersAdapter(this, providerItems,
                new FileProvidersAppItemSelectedListener() {
                    @Override
                    public void onItemSelected(@NonNull final FileProvidersAppItem item) {
                        Intent intent = item.getIntent();
                        intent.setClassName(
                                item.getResolveInfo().activityInfo.packageName,
                                item.getResolveInfo().activityInfo.name);
                        startActivityForResult(intent, REQ_CODE_CHOOSE_FILE);
                    }
                }));
    }

    @NonNull
    private List<ResolveInfo> queryImagePickers() {
        Intent intent = createImagePickerIntent();

        return getPackageManager().queryIntentActivities(intent, 0);
    }

    @NonNull
    private Intent createImagePickerIntent() {
        Intent intent = new Intent(ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        return intent;
    }

    @NonNull
    private List<ResolveInfo> queryImageProviders() {
        Intent intent = createGetImageDocumentIntent();

        return getPackageManager().queryIntentActivities(intent, 0);
    }

    @NonNull
    private Intent createGetImageDocumentIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(ACTION_OPEN_DOCUMENT);
        } else {
            intent = new Intent(ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        return intent;
    }

    @NonNull
    private List<ResolveInfo> queryPdfProviders() {
        Intent intent = createGetPdfDocumentIntent();

        return getPackageManager().queryIntentActivities(intent, 0);
    }

    @NonNull
    private Intent createGetPdfDocumentIntent() {
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(ACTION_OPEN_DOCUMENT);
        } else {
            intent = new Intent(ACTION_GET_CONTENT);
        }
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        return intent;
    }
}
