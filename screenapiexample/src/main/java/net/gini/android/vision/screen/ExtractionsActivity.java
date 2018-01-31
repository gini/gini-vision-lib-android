package net.gini.android.vision.screen;

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

import net.gini.android.vision.GiniVisionApplication;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetwork;
import net.gini.android.vision.network.GiniVisionNetworkHandler;
import net.gini.android.vision.network.model.GiniVisionExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final String EXTRA_IN_EXTRACTIONS = "EXTRA_IN_EXTRACTIONS";

    private final Map<String, GiniVisionSpecificExtraction> mExtractions = new HashMap<>();

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
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_extractions);
        mLayoutProgress = (LinearLayout) findViewById(R.id.layout_progress);
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
                        mExtractions.put(key,
                                (GiniVisionSpecificExtraction) extractionsBundle.getParcelable(key));
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

        mExtractionsAdapter = new ExtractionsAdapter(getSortedExtractions());
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

    private List<GiniVisionSpecificExtraction> getSortedExtractions() {
        final ArrayList<GiniVisionSpecificExtraction> sortedExtractions = new ArrayList<>();
        final ArrayList<String> keys = new ArrayList<>(mExtractions.keySet());
        // Ascending order
        Collections.sort(keys);
        for (final String key : keys) {
            sortedExtractions.add(mExtractions.get(key));
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
            final GiniVisionSpecificExtraction extraction = new GiniVisionSpecificExtraction("amountToPay", "10.00:EUR",
                    "amount", null, Collections.<GiniVisionExtraction>emptyList());
            mExtractions.put("amountToPay", extraction);
            mExtractionsAdapter.setExtractions(getSortedExtractions());
            Toast.makeText(this, "Added amount of 10.00:EUR", Toast.LENGTH_SHORT).show();
        }
        mExtractionsAdapter.notifyDataSetChanged();

        final GiniVisionNetworkHandler networkHandler =
                (GiniVisionNetworkHandler) ((GiniVisionApplication)getApplication()).getGiniVisionNetwork();

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

    private void showProgressIndicator() {
        mRecyclerView.animate().alpha(0.5f);
        mLayoutProgress.setVisibility(View.VISIBLE);
    }

    private void hideProgressIndicator() {
        mRecyclerView.animate().alpha(1.0f);
        mLayoutProgress.setVisibility(View.GONE);
    }

    private class ExtractionsAdapter extends
            RecyclerView.Adapter<ExtractionsAdapter.ExtractionsViewHolder> {

        class ExtractionsViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextName;
            public TextView mTextValue;

            public ExtractionsViewHolder(final View itemView) {
                super(itemView);

                mTextName = (TextView) itemView.findViewById(R.id.text_name);
                mTextValue = (TextView) itemView.findViewById(R.id.text_value);
            }
        }

        private List<GiniVisionSpecificExtraction> mExtractions;

        private ExtractionsAdapter(final List<GiniVisionSpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        public void setExtractions(final List<GiniVisionSpecificExtraction> extractions) {
            mExtractions = extractions;
        }

        @Override
        public ExtractionsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
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
