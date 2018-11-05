package net.gini.android.vision.component.review.multipage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.compat.AnalysisExampleAppCompatActivity;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.review.multipage.MultiPageReviewFragment;
import net.gini.android.vision.review.multipage.MultiPageReviewFragmentListener;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class MultiPageReviewExampleActivity extends AppCompatActivity implements
        MultiPageReviewFragmentListener {

    private static final int ANALYSIS_REQUEST = 1;
    private MultiPageReviewFragment mMultiPageReviewFragment;

    public static Intent newInstance(final Context context) {
        return new Intent(context, MultiPageReviewExampleActivity.class);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final GiniVisionMultiPageDocument document) {
        final Intent intent = AnalysisExampleAppCompatActivity.newInstance(document, null,
                this);
        startActivityForResult(intent, ANALYSIS_REQUEST);
    }

    @Override
    public void onReturnToCameraScreen() {
        finish();
    }

    @Override
    public void onImportedDocumentReviewCancelled() {
        finish();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ANALYSIS_REQUEST:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_OK);
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_page_review);
        setUpActionBar();
        setTitles();

        if (savedInstanceState == null) {
            createMultiPageReviewFragment();
            showMultiPageReviewFragment();
        } else {
            retrieveMultiPageReviewFragment();
        }
    }

    private void setUpActionBar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void setTitles() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(R.string.multi_page_review_screen_title);
        actionBar.setSubtitle(getString(R.string.multi_page_review_screen_subtitle));
    }

    private void createMultiPageReviewFragment() {
        mMultiPageReviewFragment = MultiPageReviewFragment.createInstance();
    }

    private void showMultiPageReviewFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.multi_page_review_screen_container, mMultiPageReviewFragment)
                .commit();
    }

    private void retrieveMultiPageReviewFragment() {
        mMultiPageReviewFragment =
                (MultiPageReviewFragment) getSupportFragmentManager().findFragmentById(
                        R.id.multi_page_review_screen_container);
    }
}
