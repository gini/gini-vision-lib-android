package net.gini.android.vision.noresults;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import net.gini.android.vision.R;

public class NoResultsActivity extends AppCompatActivity implements NoResultsFragmentListener {

    @Override
    public void onBackToCameraPressed() {
        finish();
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_noresults);

        final ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            NoResultsFragmentCompat noResultsFragment = NoResultsFragmentCompat.newInstance();
            noResultsFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.gv_fragment_noresults, noResultsFragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
