package net.gini.android.vision.review.multipage;

import static net.gini.android.vision.analysis.AnalysisActivity.RESULT_NO_EXTRACTIONS;
import static net.gini.android.vision.internal.util.ActivityHelper.enableHomeAsUp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;

import java.util.List;

public class MultiPageReviewActivity extends AppCompatActivity implements
        MultiPageReviewFragmentListener {

    private static final String MP_REVIEW_FRAGMENT = "MP_REVIEW_FRAGMENT";
    private static final int ANALYSE_DOCUMENT_REQUEST = 1;

    private MultiPageReviewFragment mFragment;

    public static Intent createIntent(@NonNull final Context context) {
        return new Intent(context, MultiPageReviewActivity.class);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_multi_page_review);
        if (savedInstanceState == null) {
            initFragment();
        } else {
            retainFragment();
        }
        enableHomeAsUp(this);
    }

    private void initFragment() {
        if (!isFragmentShown()) {
            createFragment();
            showFragment();
        }
    }

    private boolean isFragmentShown() {
        return getSupportFragmentManager().findFragmentByTag(MP_REVIEW_FRAGMENT) != null;
    }

    private void createFragment() {
        mFragment = MultiPageReviewFragment.createInstance();
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_review_multi_page_document, mFragment, MP_REVIEW_FRAGMENT)
                .commit();
    }

    private void retainFragment() {
        mFragment = (MultiPageReviewFragment) getSupportFragmentManager().findFragmentByTag(
                MP_REVIEW_FRAGMENT);
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final GiniVisionMultiPageDocument multiPageDocument) {
        final List documents = multiPageDocument.getDocuments();
        if (documents.size() == 0) {
            return;
        }
        final Intent intent = new Intent(this, AnalysisActivity.class);
        intent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, multiPageDocument);
        startActivityForResult(intent, ANALYSE_DOCUMENT_REQUEST);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        if (requestCode == ANALYSE_DOCUMENT_REQUEST) {
            if (resultCode == RESULT_NO_EXTRACTIONS) {
                finish();
            } else if (resultCode != Activity.RESULT_CANCELED) {
                setResult(resultCode, data);
                finish();
            }
        }
    }

}
