package net.gini.android.vision.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.gini.android.DocumentTaskManager;
import net.gini.android.GiniApiType;
import net.gini.android.models.Document;
import net.gini.android.models.Extraction;
import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.accounting.network.GiniVisionAccountingNetworkService;
import net.gini.android.vision.example.BaseExampleApp;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetworkApi;
import net.gini.android.vision.network.GiniVisionNetworkCallback;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.model.GiniVisionExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

/**
 * <p>
 * Displays the Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference.
 * </p>
 * <p>
 * A menu item is added to send feedback. The amount is changed to 10.00:EUR or an amount of
 * 10.00:EUR is added, if missing.
 * </p>
 */
public class ExtractionsActivity extends AppCompatActivity {

    private static final Logger LOG = LoggerFactory.getLogger(
            ExtractionsActivity.class);

    public static final String EXTRA_IN_EXTRACTIONS = "EXTRA_IN_EXTRACTIONS";

    private final Map<String, GiniVisionSpecificExtraction> mExtractions = new HashMap<>();
    private final Map<String, SpecificExtraction> mLegacyExtractions = new HashMap<>();

    private RecyclerView mRecyclerView;
    private LinearLayout mLayoutProgress;

    private ExtractionsAdapter mExtractionsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extractions);
        readExtras();
        bindViews();
        setUpRecyclerView();
    }

    private void bindViews() {
        mRecyclerView = findViewById(R.id.recyclerview_extractions);
        mLayoutProgress = findViewById(R.id.layout_progress);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_extractions, menu);
        final MenuItem viewPictureItem = menu.findItem(R.id.view_picture);
        if (viewPictureItem != null) {
            final boolean pictureAvailable = getAnalyzedCameraPicture() != null;
            viewPictureItem.setEnabled(pictureAvailable);
            viewPictureItem.setVisible(pictureAvailable);
        }
        return true;
    }

    @Nullable
    private byte[] getAnalyzedCameraPicture() {
        final BaseExampleApp app = (BaseExampleApp) getApplication();
        final GiniVisionNetworkService networkService = app.getGiniVisionNetworkService("ScreenApi",
                GiniApiType.ACCOUNTING);
        if (networkService instanceof GiniVisionAccountingNetworkService) {
            return ((GiniVisionAccountingNetworkService) networkService)
                    .getAnalyzedCameraPictureAsJpeg();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.feedback:
                sendFeedback();
                return true;
            case R.id.view_picture:
                viewPicture();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void viewPicture() {
        final File pictureFile = savePictureToFile();
        if (pictureFile != null) {
            final Uri fileUri;
            try {
                fileUri = FileProvider.getUriForFile(
                        this,
                        "net.gini.android.vision.screen.fileprovider",
                        pictureFile);
            } catch (final Exception e) {
                LOG.error("Error sharing the pictue {} ", pictureFile.getAbsolutePath(), e);
                Toast.makeText(this,
                        "Error sharing the picture {} " + pictureFile.getAbsolutePath(),
                        Toast.LENGTH_LONG).show();
                return;
            }
            if (fileUri != null) {
                final Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(fileUri, getContentResolver().getType(fileUri));
                if (getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
                    Toast.makeText(this, "No image viewer app found",
                            Toast.LENGTH_LONG).show();
                } else {
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, "Could not write picture to file",
                    Toast.LENGTH_LONG).show();
        }

    }

    @Nullable
    private File savePictureToFile() {
        final byte[] picture = getAnalyzedCameraPicture();
        if (picture == null) {
            return null;
        }

        final long time = new Date().getTime();
        final String jpegFilename = time + ".jpeg";
        final File picDir = createPictureDir();
        if (picDir == null) {
            LOG.error("Could not write picture to file {}", jpegFilename);
            return null;
        }
        final File jpegFile = new File(picDir, jpegFilename);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(jpegFile);
            fileOutputStream.write(picture, 0, picture.length);
            LOG.debug("Picture written to {}", jpegFile.getAbsolutePath());
            return jpegFile;
        } catch (final IOException e) {
            LOG.error("Failed to save picture to {}", jpegFile.getAbsolutePath(), e);
            return null;
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (final IOException e) {
                    LOG.error("Closing FileOutputStream failed for {}", jpegFile.getAbsolutePath(),
                            e);
                }
            }
        }
    }

    @Nullable
    private File createPictureDir() {
        final File externalFilesDir = getExternalFilesDir(null);
        final File pictureDir = new File(externalFilesDir, "camera-pictures");
        if (pictureDir.exists() || pictureDir.mkdir()) {
            return pictureDir;
        }
        return null;
    }

    private void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final Bundle extractionsBundle = extras.getParcelable(EXTRA_IN_EXTRACTIONS);
            if (extractionsBundle != null) {
                for (final String key : extractionsBundle.keySet()) {
                    try {
                        final GiniVisionSpecificExtraction extraction =
                                extractionsBundle.getParcelable(key);
                        if (extraction != null) {
                            mExtractions.put(key, extraction);
                        }
                    } catch (final ClassCastException e) {
                        final SpecificExtraction extraction =
                                extractionsBundle.getParcelable(key);
                        if (extraction != null) {
                            mLegacyExtractions.put(key, extraction);
                        }
                    }
                }
            }
        }
    }

    private void setUpRecyclerView() {
        //noinspection ConstantConditions
        mRecyclerView.setHasFixedSize(true);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        if (!mExtractions.isEmpty()) {
            mExtractionsAdapter = new ExtractionsAdapterImpl(getSortedExtractions(mExtractions));
        } else if (!mLegacyExtractions.isEmpty()) {
            mExtractionsAdapter = new LegacyExtractionsAdapter(
                    getSortedExtractions(mLegacyExtractions));
        }
        mRecyclerView.setAdapter(mExtractionsAdapter);
    }

    /**
     * Returns true, if the extraction name is one of the Pay5 extractions: paymentRecipient, iban,
     * bic, amount and paymentReference
     */
    private boolean isPay5Extraction(final String extractionName) {
        return extractionName.equals("amountToPay")
                || extractionName.equals("bic")
                || extractionName.equals("iban")
                || extractionName.equals("paymentReference")
                || extractionName.equals("paymentRecipient");
    }

    private <T> List<T> getSortedExtractions(@NonNull final Map<String, T> extractions) {
        final ArrayList<T> sortedExtractions = new ArrayList<>();
        final ArrayList<String> keys = new ArrayList<>(extractions.keySet());
        // Ascending order
        Collections.sort(keys);
        for (final String key : keys) {
            sortedExtractions.add(extractions.get(key));
        }
        return sortedExtractions;
    }

    private void sendFeedback() {
        // An example for sending feedback where we change the amount or add one if it is missing
        // Feedback should be sent only for the user visible fields. Non-visible fields should be filtered out.
        // In a real application the user input should be used as the new value.

        final GiniVisionSpecificExtraction amount = mExtractions.get("amountToPay");
        if (amount != null) {
            // Let's assume the amount was wrong and change it
            amount.setValue("10.00:EUR");
            Toast.makeText(this, "Amount changed to 10.00:EUR", Toast.LENGTH_SHORT).show();
        } else {
            // Amount was missing, let's add it
            final GiniVisionSpecificExtraction extraction = new GiniVisionSpecificExtraction(
                    "amountToPay", "10.00:EUR",
                    "amount", null, Collections.<GiniVisionExtraction>emptyList());
            mExtractions.put("amountToPay", extraction);
            //noinspection unchecked
            mExtractionsAdapter.setExtractions(getSortedExtractions(mExtractions));
            Toast.makeText(this, "Added amount of 10.00:EUR", Toast.LENGTH_SHORT).show();
        }
        mExtractionsAdapter.notifyDataSetChanged();

        showProgressIndicator();
        final GiniVisionNetworkApi giniVisionNetworkApi =
                GiniVision.getInstance().getGiniVisionNetworkApi();
        if (giniVisionNetworkApi == null) {
            Toast.makeText(this, "Feedback not sent: missing GiniVisionNetworkApi implementation.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        giniVisionNetworkApi
                .sendFeedback(mExtractions, new GiniVisionNetworkCallback<Void, Error>() {
                    @Override
                    public void failure(final Error error) {
                        hideProgressIndicator();
                        Toast.makeText(ExtractionsActivity.this,
                                "Feedback error:\n" + error.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void success(final Void result) {
                        hideProgressIndicator();
                        Toast.makeText(ExtractionsActivity.this,
                                "Feedback successful",
                                Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void cancelled() {
                        hideProgressIndicator();
                    }
                });
    }

    private void legacySendFeedback() {
        final DocumentTaskManager documentTaskManager =
                ((BaseExampleApp) getApplication()).getGiniApi().getDocumentTaskManager();

        // An example for sending feedback where we change the amount or add one if it is missing
        // Feedback should be sent only for the user visible fields. Non-visible fields should be filtered out.
        // In a real application the user input should be used as the new value.

        final SpecificExtraction amount = mLegacyExtractions.get("amountToPay");
        if (amount != null) {
            // Let's assume the amount was wrong and change it
            amount.setValue("10.00:EUR");
            Toast.makeText(this, "Amount changed to 10.00:EUR", Toast.LENGTH_SHORT).show();
        } else {
            // Amount was missing, let's add it
            final SpecificExtraction extraction = new SpecificExtraction("amountToPay", "10.00:EUR",
                    "amount", null, Collections.<Extraction>emptyList());
            mLegacyExtractions.put("amountToPay", extraction);
            //noinspection unchecked
            mExtractionsAdapter.setExtractions(getSortedExtractions(mLegacyExtractions));
            Toast.makeText(this, "Added amount of 10.00:EUR", Toast.LENGTH_SHORT).show();
        }
        mExtractionsAdapter.notifyDataSetChanged();

        final Document document =
                ((BaseExampleApp) getApplication()).getSingleDocumentAnalyzer()
                        .getGiniApiDocument();

        // We require the Gini API SDK's net.gini.android.models.Document for sending the feedback
        if (document != null) {
            try {
                showProgressIndicator();
                documentTaskManager.sendFeedbackForExtractions(document, mLegacyExtractions)
                        .continueWith(new Continuation<Document, Object>() {
                            @Override
                            public Object then(final Task<Document> task) throws Exception {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (task.isFaulted()) {
                                            LOG.error("Feedback error", task.getError());
                                            String message = "unknown";
                                            if (task.getError() != null) {
                                                message = task.getError().getMessage();
                                            }
                                            Toast.makeText(
                                                    ExtractionsActivity.this,
                                                    "Feedback error:\n" + message,
                                                    Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(
                                                    ExtractionsActivity.this,
                                                    "Feedback successful",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                        hideProgressIndicator();
                                    }
                                });
                                return null;
                            }
                        });
            } catch (final JSONException e) {
                LOG.error("Feedback not sent", e);
                Toast.makeText(this, "Feedback not set:\n" + e.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Feedback not set: no Gini Api Document available",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void showProgressIndicator() {
        mRecyclerView.animate().alpha(0.5f);
        mLayoutProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgressIndicator() {
        mRecyclerView.animate().alpha(1.0f);
        mLayoutProgress.setVisibility(View.GONE);
    }

    private abstract static class ExtractionsAdapter<T> extends
            RecyclerView.Adapter<ExtractionsViewHolder> {

        abstract void setExtractions(@NonNull final List<T> extractions);
    }

    private static class ExtractionsViewHolder extends RecyclerView.ViewHolder {

        TextView mTextName;
        TextView mTextValue;

        ExtractionsViewHolder(final View itemView) {
            super(itemView);

            mTextName = (TextView) itemView.findViewById(R.id.text_name);
            mTextValue = (TextView) itemView.findViewById(R.id.text_value);
        }
    }

    private static class ExtractionsAdapterImpl extends
            ExtractionsAdapter<GiniVisionSpecificExtraction> {

        private List<GiniVisionSpecificExtraction> mExtractions;

        private ExtractionsAdapterImpl(final List<GiniVisionSpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        @Override
        public void setExtractions(@NonNull final List<GiniVisionSpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        @Override
        public ExtractionsViewHolder onCreateViewHolder(final ViewGroup parent,
                final int viewType) {
            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new ExtractionsViewHolder(
                    layoutInflater.inflate(R.layout.item_extraction, parent, false));
        }

        @Override
        public void onBindViewHolder(final ExtractionsViewHolder holder, final int position) {
            holder.mTextName.setText(mExtractions.get(position).getName());
            holder.mTextValue.setText(mExtractions.get(position).getValue());
        }

        @Override
        public int getItemCount() {
            return mExtractions.size();
        }

    }

    private static class LegacyExtractionsAdapter extends
            ExtractionsAdapter<SpecificExtraction> {

        private List<SpecificExtraction> mExtractions;

        private LegacyExtractionsAdapter(final List<SpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        @Override
        public void setExtractions(@NonNull final List<SpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        @Override
        public ExtractionsViewHolder onCreateViewHolder(final ViewGroup parent,
                final int viewType) {
            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new ExtractionsViewHolder(
                    layoutInflater.inflate(R.layout.item_extraction, parent, false));
        }

        @Override
        public void onBindViewHolder(final ExtractionsViewHolder holder, final int position) {
            holder.mTextName.setText(mExtractions.get(position).getName());
            holder.mTextValue.setText(mExtractions.get(position).getValue());
        }

        @Override
        public int getItemCount() {
            return mExtractions.size();
        }

    }
}
