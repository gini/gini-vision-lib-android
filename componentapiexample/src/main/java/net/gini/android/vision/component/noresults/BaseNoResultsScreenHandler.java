package net.gini.android.vision.component.noresults;

import static net.gini.android.vision.component.noresults.compat.NoResultsExampleAppCompatActivity.EXTRA_IN_DOCUMENT;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.gini.android.vision.Document;
import net.gini.android.vision.noresults.NoResultsFragmentListener;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public abstract class BaseNoResultsScreenHandler implements NoResultsFragmentListener {

    private final Activity mActivity;
    private Document mDocument;

    public BaseNoResultsScreenHandler(final Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onBackToCameraPressed() {
        final Intent intent = getCameraActivityIntent();
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    protected abstract Intent getCameraActivityIntent();

    public Activity getActivity() {
        return mActivity;
    }

    public Document getDocument() {
        return mDocument;
    }

    public void onCreate(final Bundle savedInstanceState) {
        setUpActionBar();
        setTitles();
        readExtras();

        if (savedInstanceState == null) {
            showNoResultsFragment();
        } else {
            retainNoResultsFragment();
        }
    }

    protected abstract void retainNoResultsFragment();

    protected abstract void showNoResultsFragment();

    private void readExtras() {
        mDocument = mActivity.getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
    }

    protected abstract void setTitles();

    protected abstract void setUpActionBar();
}
