package net.gini.android.vision.scanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import net.gini.android.vision.ActivityHelpers;
import net.gini.android.vision.R;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.reviewphoto.ReviewPhotoActivity;
import net.gini.android.vision.scanner.photo.Photo;

import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity implements ScannerFragmentListener {

    /**
     * Type: {@code ArrayList<OnboardingPage>}
     */
    public static final String EXTRA_ONBOARDING_PAGES = "GV_EXTRA_PAGES";
    public static final String EXTRA_REVIEW_PHOTO_ACTIVITY = "GV_EXTRA_REVIEW_PHOTO_ACTIVITY";
    public static final String EXTRA_PHOTO = "GV_EXTRA_PHOTO";

    private static final int REVIEW_PHOTO_REQUEST = 1;

    private ArrayList<OnboardingPage> mOnboardingPages;
    private Intent mReviewPhotoActivityIntent;

    public static <T extends ReviewPhotoActivity> void setReviewPhotoActivityExtra(Intent target,
                                                                                   Context context,
                                                                                   Class<T> reviewPhotoActivityClass) {
        ActivityHelpers.setActivityExtra(target, EXTRA_REVIEW_PHOTO_ACTIVITY, context, reviewPhotoActivityClass);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_scanner);
        readExtras();
    }

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mOnboardingPages = extras.getParcelableArrayList(EXTRA_ONBOARDING_PAGES);
            mReviewPhotoActivityIntent = extras.getParcelable(EXTRA_REVIEW_PHOTO_ACTIVITY);
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mReviewPhotoActivityIntent == null) {
            throw new IllegalStateException("ScannerActivity requires a ReviewPhotoActivity class. Call setReviewPhotoActivityExtra() to set it.");
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
        // Start ReviewPhotoActivity
        mReviewPhotoActivityIntent.putExtra(EXTRA_PHOTO, photo);
        startActivityForResult(mReviewPhotoActivityIntent, REVIEW_PHOTO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REVIEW_PHOTO_REQUEST) {
            Toast.makeText(this, "Back from review photo", Toast.LENGTH_SHORT).show();
        }
    }
}
