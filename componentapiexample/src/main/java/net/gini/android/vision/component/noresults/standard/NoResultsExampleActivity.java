package net.gini.android.vision.component.noresults.standard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.gini.android.vision.Document;
import net.gini.android.vision.component.R;
import net.gini.android.vision.noresults.NoResultsFragmentListener;
import net.gini.android.vision.noresults.NoResultsFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Standard Activity using the {@link NoResultsScreenHandler} to host the
 * {@link NoResultsFragmentStandard}.
 */
public class NoResultsExampleActivity extends Activity implements
        NoResultsFragmentListener {

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";
    private NoResultsScreenHandler mNoResultsScreenHandler;

    public static Intent newInstance(final Document document, final Context context) {
        final Intent intent = new Intent(context, NoResultsExampleActivity.class);
        intent.putExtra(EXTRA_IN_DOCUMENT, document);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_results);
        mNoResultsScreenHandler = new NoResultsScreenHandler(this);
        mNoResultsScreenHandler.onCreate(savedInstanceState);
    }

    @Override
    public void onBackToCameraPressed() {
        mNoResultsScreenHandler.onBackToCameraPressed();
    }
}
