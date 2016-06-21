package net.gini.android.vision.scanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.gini.android.vision.ActivityHelpers;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.analyse.AnalyseDocumentActivity;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.reviewdocument.ReviewDocumentActivity;
import net.gini.android.vision.scanner.photo.Photo;

import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity implements ScannerFragmentListener {

    /**
     * Type: {@code ArrayList<OnboardingPage>}
     */
    public static final String EXTRA_IN_ONBOARDING_PAGES = "GV_EXTRA_IN_ONBOARDING_PAGES";
    public static final String EXTRA_IN_REVIEW_DOCUMENT_ACTIVITY = "GV_EXTRA_IN_REVIEW_DOCUMENT_ACTIVITY";
    public static final String EXTRA_IN_ANALYSE_DOCUMENT_ACTIVITY = "EXTRA_IN_ANALYSE_DOCUMENT_ACTIVITY";

    public static final String EXTRA_OUT_ORIGINAL_DOCUMENT = "GV_EXTRA_OUT_ORIGINAL_DOCUMENT";
    public static final String EXTRA_OUT_DOCUMENT = "GV_EXTRA_OUT_DOCUMENT";
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    private static final int REVIEW_DOCUMENT_REQUEST = 1;
    private static final int ANALYSE_DOCUMENT_REQUEST = 2;

    private ArrayList<OnboardingPage> mOnboardingPages;
    private Intent mReviewDocumentActivityIntent;
    private Intent mAnalyseDocumentActivityIntent;
    private Photo mPhoto;

    public static <T extends ReviewDocumentActivity> void setReviewDocumentActivityExtra(Intent target,
                                                                                      Context context,
                                                                                      Class<T> reviewPhotoActivityClass) {
        ActivityHelpers.setActivityExtra(target, EXTRA_IN_REVIEW_DOCUMENT_ACTIVITY, context, reviewPhotoActivityClass);
    }

    public static <T extends AnalyseDocumentActivity> void setAnalyseDocumentActivityExtra(Intent target,
                                                                                           Context context,
                                                                                           Class<T> reviewPhotoActivityClass) {
        ActivityHelpers.setActivityExtra(target, EXTRA_IN_ANALYSE_DOCUMENT_ACTIVITY, context, reviewPhotoActivityClass);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_scanner);
        readExtras();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearMemory();
    }

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mOnboardingPages = extras.getParcelableArrayList(EXTRA_IN_ONBOARDING_PAGES);
            mReviewDocumentActivityIntent = extras.getParcelable(EXTRA_IN_REVIEW_DOCUMENT_ACTIVITY);
            mAnalyseDocumentActivityIntent = extras.getParcelable(EXTRA_IN_ANALYSE_DOCUMENT_ACTIVITY);
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mReviewDocumentActivityIntent == null) {
            throw new IllegalStateException("ScannerActivity requires a ReviewDocumentActivity class. Call setReviewDocumentActivityExtra() to set it.");
        }
        if (mAnalyseDocumentActivityIntent == null) {
            throw new IllegalStateException("ScannerActivity requires an AnalyseDocumentActivity class. Call setAnalyseDocumentActivityExtra() to set it.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gv_scanner, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.gv_action_show_onboarding) {
            startOnboardingActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startOnboardingActivity() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        if (mOnboardingPages != null) {
            intent.putParcelableArrayListExtra(OnboardingActivity.EXTRA_ONBOARDING_PAGES, mOnboardingPages);
        }
        startActivity(intent);
    }

    @Override
    public void onPhotoTaken(Photo photo) {
        mPhoto = photo;
        // Start ReviewDocumentActivity
        mReviewDocumentActivityIntent.putExtra(ReviewDocumentActivity.EXTRA_IN_PHOTO, photo);
        startActivityForResult(mReviewDocumentActivityIntent, REVIEW_DOCUMENT_REQUEST);
    }

    @Override
    public void onError(GiniVisionError error) {
        Intent result = new Intent();
        result.putExtra(EXTRA_OUT_ERROR, error);
        setResult(RESULT_ERROR, result);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REVIEW_DOCUMENT_REQUEST) {
            switch (resultCode) {
                case ReviewDocumentActivity.RESULT_PHOTO_WAS_REVIEWED:
                    if (data != null) {
                        Document document = data.getParcelableExtra(ReviewDocumentActivity.EXTRA_OUT_DOCUMENT);
                        mAnalyseDocumentActivityIntent.putExtra(AnalyseDocumentActivity.EXTRA_IN_DOCUMENT, document);
                        startActivityForResult(mAnalyseDocumentActivityIntent, ANALYSE_DOCUMENT_REQUEST);
                    }
                    break;
                case ReviewDocumentActivity.RESULT_PHOTO_WAS_REVIEWED_AND_ANALYZED:
                    if (data == null) {
                        data = new Intent();
                    }
                    if (mPhoto != null) {
                        data.putExtra(EXTRA_OUT_ORIGINAL_DOCUMENT, Document.fromPhoto(mPhoto));
                    }
                    setResult(resultCode, data);
                    finish();
                    break;
                case ReviewDocumentActivity.RESULT_ERROR:
                    setResult(RESULT_ERROR, data);
                    finish();
                    break;
            }
        } else if (requestCode == ANALYSE_DOCUMENT_REQUEST) {
            switch (resultCode) {
                case RESULT_OK:
                    if (data == null) {
                        data = new Intent();
                    }
                    if (mPhoto != null) {
                        data.putExtra(EXTRA_OUT_ORIGINAL_DOCUMENT, Document.fromPhoto(mPhoto));
                    }
                    setResult(resultCode, data);
                    finish();
                    break;
                case AnalyseDocumentActivity.RESULT_ERROR:
                    setResult(RESULT_ERROR, data);
                    finish();
                    break;
            }
        }
        clearMemory();
    }

    private void clearMemory() {
        mPhoto = null;
    }
}
