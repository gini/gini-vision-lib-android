package net.gini.android.vision.scanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.gini.android.vision.R;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.scanner.photo.Photo;

import java.util.ArrayList;

public class ScannerActivity extends AppCompatActivity implements ScannerFragmentListener {

    /**
     * Type: {@code ArrayList<OnboardingPage>}
     */
    public static final String EXTRA_ONBOARDING_PAGES = "GV_EXTRA_PAGES";
    public static final String EXTRA_PHOTO = "GV_EXTRA_PHOTO";

    private ArrayList<OnboardingPage> mOnboardingPages;

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
        Intent intent = new Intent(this, ReviewPhotoActivity.class);
        intent.putExtra(EXTRA_PHOTO, photo);
        startActivityForResult(intent, REVIEW_PHOTO_REQUEST); // TODO: continue from here!
    }
}
