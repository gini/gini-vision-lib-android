package net.gini.android.vision.component.review.multipage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.Document;
import net.gini.android.vision.analysis.AnalysisActivity;
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

    public static final String EXTRA_IN_DOCUMENT = "GV_EXTRA_IN_DOCUMENT";
    public static final int RESULT_MULTI_PAGE_DOCUMENT = RESULT_FIRST_USER + 2001;
    public static final String EXTRA_OUT_DOCUMENT = "GV_EXTRA_OUT_DOCUMENT";

    private static final int ANALYSIS_REQUEST = 1;
    private GiniVisionMultiPageDocument mMultiPageDocument;
    private MultiPageReviewFragment mMultiPageReviewFragment;

    public static Intent newInstance(final GiniVisionMultiPageDocument document, final Context context) {
        final Intent intent = new Intent(context, MultiPageReviewExampleActivity.class);
        intent.putExtra(MultiPageReviewExampleActivity.EXTRA_IN_DOCUMENT, document);
        return intent;
    }

    @Override
    public void onAddMorePages(@NonNull final Document document) {
        onBackPressed();
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document) {
        final Intent intent = AnalysisExampleAppCompatActivity.newInstance(document, null,
                this);
        intent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, mMultiPageDocument);
        startActivityForResult(intent, ANALYSIS_REQUEST);
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
    public void onBackPressed() {
        final Intent data = new Intent();
        data.putExtra(EXTRA_OUT_DOCUMENT, mMultiPageReviewFragment.getMultiPageDocument());
        setResult(RESULT_MULTI_PAGE_DOCUMENT, data);
        super.onBackPressed();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_page_review);
        setUpActionBar();
        setTitles();
        readDocumentFromExtras();

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

    private void readDocumentFromExtras() {
        mMultiPageDocument = getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
    }

    private void createMultiPageReviewFragment() {
        mMultiPageReviewFragment = MultiPageReviewFragment.createInstance(mMultiPageDocument);
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
