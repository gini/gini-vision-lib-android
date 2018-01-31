package net.gini.android.vision.component;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import net.gini.android.models.Document;
import net.gini.android.models.Extraction;
import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.GiniVisionApplication;
import net.gini.android.vision.example.BaseExampleApp;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetwork;
import net.gini.android.vision.network.GiniVisionNetworkHandler;
import net.gini.android.vision.network.model.GiniVisionExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bolts.Continuation;
import bolts.Task;

/**
 * <p>
 *     Displays the Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference.
 * </p>
 * <p>
 *     A menu item is added to send feedback. The amount is changed to 10.00:EUR or an amount of 10.00:EUR is added, if missing.
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.feedback) {
            sendFeedback();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readExtras() {
        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            final Bundle extractionsBundle = extras.getParcelable(EXTRA_IN_EXTRACTIONS);
            if (extractionsBundle != null) {
                for (final String key : extractionsBundle.keySet()) {
                    // We only show Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference
                    if (isPay5Extraction(key)) {
                        try {
                            mExtractions.put(key,
                                    (GiniVisionSpecificExtraction) extractionsBundle.getParcelable(
                                            key));
                        } catch (final ClassCastException e) {
                            mLegacyExtractions.put(key,
                                    (SpecificExtraction) extractionsBundle.getParcelable(key));
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
     * Returns true, if the extraction name is one of the Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference
     */
    private boolean isPay5Extraction(final String extractionName) {
        return extractionName.equals("amountToPay") ||
                extractionName.equals("bic") ||
                extractionName.equals("iban") ||
                extractionName.equals("paymentReference") ||
                extractionName.equals("paymentRecipient");
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

        final GiniVisionNetworkHandler networkHandler =
                (GiniVisionNetworkHandler) ((GiniVisionApplication) getApplication()).getGiniVisionNetwork();

        showProgressIndicator();
        networkHandler.sendFeedback(mExtractions, new GiniVisionNetwork.Callback<Void, Error>() {
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
                ((BaseExampleApp) getApplication()).getSingleDocumentAnalyzer().getGiniApiDocument();

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

    private static class LegacyExtractionsAdapter  extends
            ExtractionsAdapter<SpecificExtraction>  {

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
