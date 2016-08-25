package net.gini.android.vision.component;

import android.os.Bundle;
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
import net.gini.android.visionadvtest.R;

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

    private static final Logger LOG = LoggerFactory.getLogger(ExtractionsActivity.class);

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";
    public static final String EXTRA_IN_EXTRACTIONS = "EXTRA_IN_EXTRACTIONS";

    private Map<String, SpecificExtraction> mExtractions = new HashMap<>();

    private RecyclerView mRecyclerView;
    private LinearLayout mLayoutProgress;

    private ExtractionsAdapter mExtractionsAdapter;
    private Document mDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extractions);
        readExtras();
        bindViews();
        setUpRecyclerView();
    }

    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_extractions);
        mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_extractions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.feedback) {
            sendFeedback();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Bundle extractionsBundle = extras.getParcelable(EXTRA_IN_EXTRACTIONS);
            if (extractionsBundle != null) {
                for (String key : extractionsBundle.keySet()) {
                    // We only show Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference
                    if (isPay5Extraction(key)) {
                        mExtractions.put(key, (SpecificExtraction) extractionsBundle.getParcelable(key));
                    }
                }
            }
            mDocument = extras.getParcelable(EXTRA_IN_DOCUMENT);
        }
    }

    private void setUpRecyclerView() {
        //noinspection ConstantConditions
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mExtractionsAdapter = new ExtractionsAdapter(getSortedExtractions());
        mRecyclerView.setAdapter(mExtractionsAdapter);
    }

    /**
     * Returns true, if the extraction name is one of the Pay5 extractions: paymentRecipient, iban, bic, amount and paymentReference
     */
    private boolean isPay5Extraction(String extractionName) {
        return extractionName.equals("amountToPay") ||
                extractionName.equals("bic") ||
                extractionName.equals("iban") ||
                extractionName.equals("paymentReference") ||
                extractionName.equals("paymentRecipient");
    }

    private List<SpecificExtraction> getSortedExtractions() {
        ArrayList<SpecificExtraction> sortedExtractions = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>(mExtractions.keySet());
        // Ascending order
        Collections.sort(keys);
        for (String key : keys) {
            sortedExtractions.add(mExtractions.get(key));
        }
        return sortedExtractions;
    }

    private void sendFeedback() {
        DocumentTaskManager documentTaskManager = ((ComponentApiApp) getApplication()).getGiniApi().getDocumentTaskManager();

        // An example for sending feedback where we change the amount or add one if it is missing
        // Feedback should be sent only for the user visible fields. Non-visible fields should be filtered out.
        // In a real application the user input should be used as the new value.

        SpecificExtraction amount = mExtractions.get("amountToPay");
        if (amount != null) {
            // Let's assume the amount was wrong and change it
            amount.setValue("10.00:EUR");
            Toast.makeText(this, "Amount changed to 10.00:EUR", Toast.LENGTH_SHORT).show();
        } else {
            // Amount was missing, let's add it
            SpecificExtraction extraction = new SpecificExtraction("amountToPay", "10.00:EUR", "amount", null, Collections.<Extraction>emptyList());
            mExtractions.put("amountToPay", extraction);
            mExtractionsAdapter.setExtractions(getSortedExtractions());
            Toast.makeText(this, "Added amount of 10.00:EUR", Toast.LENGTH_SHORT).show();
        }
        mExtractionsAdapter.notifyDataSetChanged();

        // We require the Gini API SDK's net.gini.android.models.Document for sending the feedback
        if (mDocument != null) {
            try {
                showProgressIndicator();
                documentTaskManager.sendFeedbackForExtractions(mDocument, mExtractions)
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
                                            Toast.makeText(ExtractionsActivity.this, "Feedback error:\n" + message, Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(ExtractionsActivity.this, "Feedback successful", Toast.LENGTH_LONG).show();
                                        }
                                        hideProgressIndicator();
                                    }
                                });
                                return null;
                            }
                        });
            } catch (JSONException e) {
                LOG.error("Feedback not sent", e);
                Toast.makeText(this, "Feedback not set:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Feedback not set: no Gini Api Document available", Toast.LENGTH_LONG).show();
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

    private class ExtractionsAdapter extends RecyclerView.Adapter<ExtractionsAdapter.ExtractionsViewHolder> {

        class ExtractionsViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextName;
            public TextView mTextValue;

            public ExtractionsViewHolder(View itemView) {
                super(itemView);

                mTextName = (TextView) itemView.findViewById(R.id.text_name);
                mTextValue = (TextView) itemView.findViewById(R.id.text_value);
            }
        }

        private List<SpecificExtraction> mExtractions;

        private ExtractionsAdapter(List<SpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        public void setExtractions(List<SpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        @Override
        public ExtractionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            return new ExtractionsViewHolder(layoutInflater.inflate(R.layout.item_extraction, parent, false));
        }

        @Override
        public void onBindViewHolder(ExtractionsViewHolder holder, int position) {
            holder.mTextName.setText(mExtractions.get(position).getName());
            holder.mTextValue.setText(mExtractions.get(position).getValue());
        }

        @Override
        public int getItemCount() {
            return mExtractions.size();
        }

    }
}
