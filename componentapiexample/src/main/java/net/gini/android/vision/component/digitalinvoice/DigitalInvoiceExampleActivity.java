package net.gini.android.vision.component.digitalinvoice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.component.ExtractionsActivity;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.digitalinvoice.details.LineItemDetailsExampleActivity;
import net.gini.android.vision.digitalinvoice.DigitalInvoiceFragment;
import net.gini.android.vision.digitalinvoice.DigitalInvoiceFragmentListener;
import net.gini.android.vision.digitalinvoice.SelectableLineItem;
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class DigitalInvoiceExampleActivity extends AppCompatActivity implements
        DigitalInvoiceFragmentListener {

    private static final Logger LOG = LoggerFactory.getLogger(DigitalInvoiceExampleActivity.class);

    private static final String EXTRA_IN_EXTRACTIONS = "EXTRA_IN_EXTRACTIONS";
    private static final String EXTRA_IN_COMPOUND_EXTRACTIONS = "EXTRA_IN_COMPOUND_EXTRACTIONS";

    private static final int EDIT_LINE_ITEM_REQUEST = 1;

    private DigitalInvoiceFragment mDigitalInvoiceFragment;

    private Map<String, GiniVisionSpecificExtraction> mExtractions = Collections.emptyMap();
    private Map<String, GiniVisionCompoundExtraction> mCompoundExtractions = Collections.emptyMap();

    public static Intent newInstance(@NonNull final Context context,
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final Map<String, GiniVisionCompoundExtraction> compoundExtractions) {
        final Intent intent = new Intent(context, DigitalInvoiceExampleActivity.class);
        intent.putExtra(EXTRA_IN_EXTRACTIONS, mapToBundle(extractions));
        intent.putExtra(EXTRA_IN_COMPOUND_EXTRACTIONS, mapToBundle(compoundExtractions));
        return intent;
    }

    private static <T extends Parcelable> Bundle mapToBundle(@NonNull final Map<String, T> map) {
        final Bundle bundle = new Bundle();
        for (final Map.Entry<String, T> entry : map.entrySet()) {
            bundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return bundle;
    }

    private static <T extends Parcelable> Map<String, T> bundleToMap(@Nullable final Bundle bundle) {
        if (bundle == null) {
            return Collections.emptyMap();
        }
        final Map<String, T> map = new HashMap<>();
        for (final String key : bundle.keySet()) {
            //noinspection unchecked
            map.put(key, (T) bundle.getParcelable(key));
        }
        return map;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_LINE_ITEM_REQUEST) {
            if (resultCode == RESULT_OK) {
                final SelectableLineItem selectableLineItem = data.getParcelableExtra(LineItemDetailsExampleActivity.EXTRA_OUT_SELECTABLE_LINE_ITEM);
                if (selectableLineItem != null) {
                    mDigitalInvoiceFragment.updateLineItem(selectableLineItem);
                }
            }
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digital_invoice);
        setUpActionBar();
        setTitles();
        readExtras();

        if (savedInstanceState == null) {
            createDigitalInvoiceFragment();
            showDigitalInvoiceFragment();
        } else {
            retrieveDigitalInvoiceFragment();
        }
    }

    @Override
    public void onEditLineItem(final SelectableLineItem selectableLineItem) {
        startActivityForResult(LineItemDetailsExampleActivity.newInstance(this, selectableLineItem), EDIT_LINE_ITEM_REQUEST);
    }

    @Override
    public void onPayInvoice(@NonNull final Map<String, ? extends GiniVisionSpecificExtraction> specificxtractions,
            @NonNull final Map<String, ? extends GiniVisionCompoundExtraction> compoundExtractions) {
        LOG.debug("Show extractions with line items");
        final Intent intent = new Intent(this, ExtractionsActivity.class);
        intent.putExtra(ExtractionsActivity.EXTRA_IN_EXTRACTIONS, mapToBundle(specificxtractions));
        intent.putExtra(ExtractionsActivity.EXTRA_IN_COMPOUND_EXTRACTIONS, mapToBundle(compoundExtractions));
        startActivity(intent);
        setResult(RESULT_OK);
        finish();
    }

    private void readExtras() {
        mExtractions = bundleToMap(getIntent().getBundleExtra(EXTRA_IN_EXTRACTIONS));
        mCompoundExtractions = bundleToMap(getIntent().getBundleExtra(EXTRA_IN_COMPOUND_EXTRACTIONS));
    }

    private void setUpActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void setTitles() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(R.string.digital_invoice_screen_title);
        actionBar.setSubtitle(getString(R.string.digital_invoice_screen_subtitle));
    }

    private void createDigitalInvoiceFragment() {
        mDigitalInvoiceFragment = DigitalInvoiceFragment.createInstance(mExtractions, mCompoundExtractions);
    }

    private void showDigitalInvoiceFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.digital_invoice_screen_container, mDigitalInvoiceFragment)
                .commit();
    }

    private void retrieveDigitalInvoiceFragment() {
        mDigitalInvoiceFragment =
                (DigitalInvoiceFragment) getSupportFragmentManager().findFragmentById(
                        R.id.digital_invoice_screen_container);
    }
}
