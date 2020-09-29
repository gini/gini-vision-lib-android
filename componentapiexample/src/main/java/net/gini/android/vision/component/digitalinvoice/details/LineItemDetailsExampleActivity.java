package net.gini.android.vision.component.digitalinvoice.details;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import net.gini.android.vision.component.R;
import net.gini.android.vision.digitalinvoice.SelectableLineItem;
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsFragment;
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsFragmentListener;
import net.gini.android.vision.network.model.GiniVisionReturnReason;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class LineItemDetailsExampleActivity extends AppCompatActivity implements
        LineItemDetailsFragmentListener {

    private static final String EXTRA_IN_SELECTABLE_LINE_ITEM = "EXTRA_IN_SELECTABLE_LINE_ITEM";
    private static final String EXTRA_IN_RETURN_REASONS = "EXTRA_IN_RETURN_REASONS";
    public static final String EXTRA_OUT_SELECTABLE_LINE_ITEM = "EXTRA_OUT_SELECTABLE_LINE_ITEM";

    private LineItemDetailsFragment mLineItemDetailsFragment;
    private SelectableLineItem mSelectableLineItem;
    private List<GiniVisionReturnReason> mReturnReasons;

    public static Intent newInstance(@NonNull final Context context, @NonNull final SelectableLineItem selectableLineItem,
            @NonNull final List<GiniVisionReturnReason> returnReasons) {
        final Intent intent = new Intent(context, LineItemDetailsExampleActivity.class);
        intent.putExtra(EXTRA_IN_SELECTABLE_LINE_ITEM, selectableLineItem);
        intent.putParcelableArrayListExtra(EXTRA_IN_RETURN_REASONS, new ArrayList<Parcelable>(returnReasons));
        return intent;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_item_details);
        setUpActionBar();
        setTitles();
        readExtras();

        if (savedInstanceState == null) {
            createLineItemDetailsFragment();
            showLineItemDetailsFragment();
        } else {
            retrieveLineItemDetailsFragment();
        }
    }

    @Override
    public void onSave(@NotNull final SelectableLineItem selectableLineItem) {
        final Intent data = new Intent();
        data.putExtra(EXTRA_OUT_SELECTABLE_LINE_ITEM, selectableLineItem);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    private void readExtras() {
        mSelectableLineItem = getIntent().getParcelableExtra(EXTRA_IN_SELECTABLE_LINE_ITEM);
        mReturnReasons = getIntent().getParcelableArrayListExtra(EXTRA_IN_RETURN_REASONS);
    }

    private void setUpActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void setTitles() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(R.string.line_item_details_screen_title);
        actionBar.setSubtitle(getString(R.string.line_item_details_screen_subtitle));
    }

    private void createLineItemDetailsFragment() {
        mLineItemDetailsFragment = LineItemDetailsFragment.createInstance(mSelectableLineItem, mReturnReasons);
    }

    private void showLineItemDetailsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.line_item_details_screen_container, mLineItemDetailsFragment)
                .commit();
    }

    private void retrieveLineItemDetailsFragment() {
        mLineItemDetailsFragment =
                (LineItemDetailsFragment) getSupportFragmentManager().findFragmentById(
                        R.id.line_item_details_screen_container);
    }
}
